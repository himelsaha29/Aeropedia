package com.himel.aeropedia.alexa;


import com.willblaschko.android.alexa.callbacks.AsyncCallback;
import com.willblaschko.android.alexa.interfaces.AvsResponse;

public interface AVSListener {
    AsyncCallback<AvsResponse, Exception> getRequestCallback();
}
