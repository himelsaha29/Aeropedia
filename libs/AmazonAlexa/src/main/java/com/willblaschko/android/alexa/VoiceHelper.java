package com.willblaschko.android.alexa;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A helper class that utilizes the TextToSpeech engine built into Android to turn a string-based AVS intent
 * into parsable raw audio that can be sent to the server. This is a work around that allows for more flexibility
 * if the application wants to pre/post-pend strings to the user's request.
 *
 * This could also be done using the SendEvent byte[] buffer with pre-recorded or generated audio
 */
public class VoiceHelper{

    private final static String TAG = "VoiceHelper";

    private static VoiceHelper mInstance;
    private Context mContext;
    private TextToSpeech mTextToSpeech;
    private boolean mIsIntialized = false;

    Map<String, SpeechFromTextCallback> mCallbacks = new HashMap<>();

    /**
     * Initalize our TextToSpeech engine, use a few tricks to get it to use a smaller file size
     * and be more easily recognized by the Alexa parser
     * @param context local/application level context
     */
    private VoiceHelper(Context context){
        mContext = context.getApplicationContext();
        mTextToSpeech = new TextToSpeech(mContext, mInitListener);
        mTextToSpeech.setPitch(.8f);
        mTextToSpeech.setSpeechRate(1.3f);
        mTextToSpeech.setOnUtteranceProgressListener(mUtteranceProgressListener);
    }

    /**
     * Get an instance of the VoiceHelper utility class, if it's currently null,
     * create a new instance
     * @param context
     * @return
     */
    public static VoiceHelper getInstance(Context context){
        if(mInstance == null){
            mInstance = new VoiceHelper(context);
        }

        return mInstance;
    }

    /**
     * Our TextToSpeech Init state changed listener
     */
    private TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if(status == TextToSpeech.SUCCESS){
                mIsIntialized = true;
            }else{
                new IllegalStateException("Unable to initialize Text to Speech engine").printStackTrace();
            }
        }
    };

    /**
     * Our TextToSpeech UtteranceProgress state changed listener
     * We keep track of when we're done and pass back the byte[] raw audio of the recorded speech
     */
    private UtteranceProgressListener mUtteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {
            //this allows us to keep track of multiple callbacks
            SpeechFromTextCallback callback = mCallbacks.get(utteranceId);
            if(callback != null){
                //get our cache file where we'll be storing the audio
                File cacheFile = getCacheFile(utteranceId);
                try {
                    byte[] data = FileUtils.readFileToByteArray(cacheFile);
                    callback.onSuccess(data);
                } catch (IOException e) {
                    e.printStackTrace();
                    //bubble up our error
                    callback.onError(e);
                }

                cacheFile.delete();
                //remove the utteranceId from our callback once we're done
                mCallbacks.remove(utteranceId);
            }
        }

        @Override
        public void onError(String utteranceId) {
            //add more information to our error
            this.onError(utteranceId, TextToSpeech.ERROR);
        }

        @Override
        public void onError(String utteranceId, int errorCode) {
            if(mCallbacks == null){
                return;
            }
            SpeechFromTextCallback callback = mCallbacks.get(utteranceId);
            if(callback != null){
                //if we have a callback, bubble up the error
                callback.onError(new Exception("Unable to process request, error code: "+errorCode));
            }
        }
    };

    /**
     * Create a new audio recording based on text passed in, update the callback with the changing states
     * @param text the text to render
     * @param callback
     */
    public void getSpeechFromText(String text, SpeechFromTextCallback callback){

        //create a new unique ID
        String utteranceId = AuthorizationManager.createCodeVerifier();

        //add the callback to our list of callbacks
        mCallbacks.put(utteranceId, callback);

        //get our TextToSpeech engine
        TextToSpeech textToSpeech = getTextToSpeech();

        //set up our arguments
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);

        //request an update from TTS
        textToSpeech.synthesizeToFile(text, params, getCacheFile(utteranceId).toString());
    }

    /**
     * Get the TextToSpeech instance
     * @return our TextToSpeech instance, if it's initialized
     */
    private TextToSpeech getTextToSpeech(){
        int count = 0;

        while(!mIsIntialized){
            if(count < 100)
            {
                count++;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                throw new IllegalStateException("Text to Speech engine is not initalized");
            }
        }
        return mTextToSpeech;
    }

    /**
     * Our cache file based on the unique id generated for the intent
     * @param utteranceId
     * @return
     */
    private File getCacheFile(String utteranceId){
        return new File(getCacheDir(), utteranceId+".wav");
    }

    //helper function to get the default cache dir for the app
    private File getCacheDir(){
        return mContext.getCacheDir();
    }

    /**
     * State-based callback for the VoiceHelper class
     */
    public interface SpeechFromTextCallback{
        void onSuccess(byte[] data);
        void onError(Exception e);
    }
}
