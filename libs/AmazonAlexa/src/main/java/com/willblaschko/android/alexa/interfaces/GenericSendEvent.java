package com.willblaschko.android.alexa.interfaces;

import android.util.Log;

import com.willblaschko.android.alexa.callbacks.AsyncCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;

/**
 * @author will on 5/21/2016.
 */
public class GenericSendEvent extends SendEvent{

    public static final String TAG = "GenericSendEvent";

    String event;

    public GenericSendEvent(String url, String accessToken, String event,
                            final AsyncCallback<Call, Exception> callback){
        this.event = event;

        if (callback != null){
            callback.start();
        }
        try {
            prepareConnection(url, accessToken);
            if (callback != null) {
                callback.success(completePost());
                callback.complete();
            } else {
                completePost();
            }
            Log.i(TAG, "Event sent");
        } catch (IOException | AvsException e) {
            onError(callback, e);
        }

    }

    @NotNull
    @Override
    public String getEvent() {
        return event;
    }


    public void onError(final AsyncCallback<Call, Exception> callback, Exception e) {
        if (callback != null) {
            callback.failure(e);
            callback.complete();
        }
    }
}
