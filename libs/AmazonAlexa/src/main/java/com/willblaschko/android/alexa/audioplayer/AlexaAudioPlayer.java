package com.willblaschko.android.alexa.audioplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.willblaschko.android.alexa.interfaces.AvsItem;
import com.willblaschko.android.alexa.interfaces.audioplayer.AvsPlayContentItem;
import com.willblaschko.android.alexa.interfaces.audioplayer.AvsPlayRemoteItem;
import com.willblaschko.android.alexa.interfaces.speechsynthesizer.AvsSpeakItem;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that abstracts the Android MediaPlayer and adds additional functionality to handle AvsItems
 * as well as properly handle multiple callbacks--be care not to leak Activities by not removing the callback
 */
public class AlexaAudioPlayer {

    public static final String TAG = "AlexaAudioPlayer";

    private static AlexaAudioPlayer mInstance;

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private AvsItem mItem;
    private final List<Callback> mCallbacks = new ArrayList<>();

    /**
     * Create our new AlexaAudioPlayer
     * @param context any context, we will get the application level to store locally
     */
    private AlexaAudioPlayer(Context context){
       mContext = context.getApplicationContext();
    }

    /**
     * Get a reference to the AlexaAudioPlayer instance, if it's null, we will create a new one
     * using the supplied context.
     * @param context any context, we will get the application level to store locally
     * @return our instance of the AlexaAudioPlayer
     */
    public static AlexaAudioPlayer getInstance(Context context){
        if(mInstance == null){
            mInstance = new AlexaAudioPlayer(context);
            trimCache(context);
        }
        return mInstance;
    }

    private static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        if(dir != null){
            // The directory is now empty so delete it
            return dir.delete();
        }
        return false;
    }

    /**
     * Return a reference to the MediaPlayer instance, if it does not exist,
     * then create it and configure it to our needs
     * @return Android native MediaPlayer
     */
    private MediaPlayer getMediaPlayer(){
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
        }
        return mMediaPlayer;
    }

    /**
     * Add a callback to our AlexaAudioPlayer, this is added to our list of callbacks
     * @param callback Callback that listens to changes of player state
     */
    public void addCallback(Callback callback){
        synchronized (mCallbacks) {
            if (!mCallbacks.contains(callback)) {
                mCallbacks.add(callback);
            }
        }
    }

    @Nullable
    public AvsItem getCurrentItem() {
        return mItem;
    }

    /**
     * Remove a callback from our AlexaAudioPlayer, this is removed from our list of callbacks
     * @param callback Callback that listens to changes of player state
     */
    public void removeCallback(Callback callback){
        synchronized (mCallbacks) {
            mCallbacks.remove(callback);
        }
    }

    /**
     * A helper function to play an AvsPlayContentItem, this is passed to play() and handled accordingly,
     * @param item a speak type item
     */
    public void playItem(AvsPlayContentItem item){
        play(item);
    }

    /**
     * A helper function to play an AvsSpeakItem, this is passed to play() and handled accordingly,
     * @param item a speak type item
     */
    public void playItem(AvsSpeakItem item){
        play(item);
    }

    /**
     * A helper function to play an AvsPlayRemoteItem, this is passed to play() and handled accordingly,
     * @param item a play type item, usually a url
     */
    public void playItem(AvsPlayRemoteItem item){
        play(item);
    }

    /**
     * Request our MediaPlayer to play an item, if it's an AvsPlayRemoteItem (url, usually), we set that url as our data source for the MediaPlayer
     * if it's an AvsSpeakItem, then we write the raw audio to a file and then read it back using the MediaPlayer
     * @param item
     */
    private void play(AvsItem item){
        if(isPlaying()){
            Log.w(TAG, "Already playing an item, did you mean to play another?");
        }
        mItem = item;
        if(getMediaPlayer().isPlaying()){
            //if we're playing, stop playing before we continue
            getMediaPlayer().stop();
        }

        //reset our player
        getMediaPlayer().reset();

        if(!TextUtils.isEmpty(mItem.getToken()) && mItem.getToken().contains("PausePrompt")){
            //a gross work around for a broke pause mp3 coming from Amazon, play the local mp3
            try {
                AssetFileDescriptor afd = mContext.getAssets().openFd("shhh.mp3");
                getMediaPlayer().setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            } catch (IOException e) {
                e.printStackTrace();
                //bubble up our error
                bubbleUpError(e);
            }
        }else if(mItem instanceof AvsPlayRemoteItem){
            //cast our item for easy access
            AvsPlayRemoteItem playItem = (AvsPlayRemoteItem) item;
            try {
                //set stream
                getMediaPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);
                //play new url
                getMediaPlayer().setDataSource(playItem.getUrl());
            } catch (IOException e) {
                e.printStackTrace();
                //bubble up our error
                bubbleUpError(e);
            }
        }else if(mItem instanceof AvsPlayContentItem){
            //cast our item for easy access
            AvsPlayContentItem playItem = (AvsPlayContentItem) item;
            try {
                //set stream
                getMediaPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);
                //play new url
                getMediaPlayer().setDataSource(mContext, playItem.getUri());
            } catch (IOException e) {
                e.printStackTrace();
                //bubble up our error
                bubbleUpError(e);
            } catch (IllegalStateException e){
                e.printStackTrace();
                //bubble up our error
                bubbleUpError(e);
            }
        }else if(mItem instanceof AvsSpeakItem){
            //cast our item for easy access
            AvsSpeakItem playItem = (AvsSpeakItem) item;
            //write out our raw audio data to a file
            File path=new File(mContext.getCacheDir(), System.currentTimeMillis()+".mp3");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path);
                fos.write(playItem.getAudio());
                fos.close();
                //play our newly-written file
                getMediaPlayer().setDataSource(path.getPath());
            } catch (IOException|IllegalStateException e) {
                e.printStackTrace();
                //bubble up our error
                bubbleUpError(e);
            }

        }
        //prepare our player, this will start once prepared because of mPreparedListener
        try {
            getMediaPlayer().prepareAsync();
        }catch (IllegalStateException e){
            bubbleUpError(e);
        }
    }

    /**
     * Check whether our MediaPlayer is currently playing
     * @return true playing, false not
     */
    public boolean isPlaying(){
        return getMediaPlayer().isPlaying();
    }

    /**
     * A helper function to pause the MediaPlayer
     */
    public void pause(){
        getMediaPlayer().pause();
    }

    /**
     * A helper function to play the MediaPlayer
     */
    public void play(){
        getMediaPlayer().start();
    }

    /**
     * A helper function to stop the MediaPlayer
     */
    public void stop(){
        getMediaPlayer().stop();
    }

    /**
     * A helper function to release the media player and remove it from memory
     */
    public void release(){
        if(mMediaPlayer != null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
        mMediaPlayer = null;
    }

    public void duck(float value) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(value, value);
        }
    }

    public void unDuck() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(1F, 1F);
        }
    }

    /**
     * If our callback is not null, post our player progress back to the controlling
     * application so we can do "almost done" type of calls
     */
    private void postProgress(final float percent){
        synchronized (mCallbacks) {
            for (Callback callback : mCallbacks) {
                if(mMediaPlayer != null && callback != null) {
                    callback.playerProgress(mItem, mMediaPlayer.getCurrentPosition(), percent);
                }
            }
        }
    }

    /**
     * A callback to keep track of the state of the MediaPlayer and various AvsItem states
     */
    public interface Callback{
        void playerPrepared(AvsItem pendingItem);
        void playerProgress(AvsItem currentItem, long offsetInMilliseconds, float percent);
        void itemComplete(AvsItem completedItem);
        boolean playerError(AvsItem item, int what, int extra);
        void dataError(AvsItem item, Exception e);
    }

    /**
     * Pass our Exception to all the Callbacks, handle it at the top level
     * @param e the thrown exception
     */
    private void bubbleUpError(Exception e){
        for(Callback callback: mCallbacks){
            callback.dataError(mItem, e);
        }
    }

    /**
     * Pass our MediaPlayer completion state to all the Callbacks, handle it at the top level
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            for(Callback callback: mCallbacks){
                callback.playerProgress(mItem, 1, 1);
                callback.itemComplete(mItem);
            }
        }
    };

    /**
     * Pass our MediaPlayer prepared state to all the Callbacks, handle it at the top level
     */
    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            for(Callback callback: mCallbacks){
                callback.playerPrepared(mItem);
                callback.playerProgress(mItem, mMediaPlayer.getCurrentPosition(), 0);
            }
            mMediaPlayer.start();
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        while (getMediaPlayer() != null && getMediaPlayer().isPlaying()) {
                            int pos = getMediaPlayer().getCurrentPosition();
                            final float percent = (float) pos / (float) getMediaPlayer().getDuration();
                            postProgress(percent);
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }catch (NullPointerException|IllegalStateException e){
                        e.printStackTrace();
                    }
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    /**
     * Pass our MediaPlayer error state to all the Callbacks, handle it at the top level
     */
    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            for(Callback callback: mCallbacks){
                boolean response = callback.playerError(mItem, what, extra);
                if(response){
                    return response;
                }
            }
            return false;
        }
    };


}
