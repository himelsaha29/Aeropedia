package com.willblaschko.android.alexa.interfaces.speechrecognizer;

import android.util.Log;

import com.willblaschko.android.alexa.callbacks.AsyncCallback;
import com.willblaschko.android.alexa.interfaces.AvsException;
import com.willblaschko.android.alexa.requestbody.DataRequestBody;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.RequestBody;

/**
 * A subclass of {@link SpeechSendEvent} that sends a RequestBody to the AVS servers, this request body can either be a byte[]
 * straight write, or a threaded write loop based on incoming data (recorded audio).
 *
 * @author will on 4/17/2016.
 */
public class SpeechSendAudio extends SpeechSendEvent {

    private final static String TAG = "SpeechSendAudio";

    long start = 0;
    DataRequestBody requestBody;

    /**
     * Post an audio byte[] to the Alexa Speech Recognizer API
     * @param url the URL to which we're sending the AVS post
     * @param accessToken our user's access token for the server
     * @param requestBody our OkHttp RequestBody for our mulitpart send, this request body can either be a byte[]
     *                    straight write, or a threaded write loop based on incoming data (recorded audio).
     * @param callback our event callbacks
     * @throws IOException
     */
    public void sendAudio(final String url, final String accessToken, @NotNull DataRequestBody requestBody,
                          final AsyncCallback<Call, Exception> callback) throws IOException {
        this.requestBody = requestBody;
        if(callback != null){
            callback.start();
        }
        Log.i(TAG, "Starting SpeechSendAudio procedure");
        start = System.currentTimeMillis();

        //call the parent class's prepareConnection() in order to prepare our URL POST
        try {
            prepareConnection(url, accessToken);
            final Call response = completePost();

            if (callback != null) {
                if (response != null) {
                    callback.success(response);
                }
                callback.complete();
            }

            Log.i(TAG, "Audio sent");
            Log.i(TAG, "Audio sending process took: " + (System.currentTimeMillis() - start));
        } catch (IOException|AvsException e) {
            onError(callback, e);
        }
    }

    public void cancelRequest() {
        cancelCall();
    }

    public void onError(final AsyncCallback<Call, Exception> callback, Exception e) {
        if(callback != null){
            callback.failure(e);
            callback.complete();
        }
    }

    @NotNull
    @Override
    protected RequestBody getRequestBody() {
        return requestBody;
    }
}
