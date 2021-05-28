package com.willblaschko.android.alexa.interfaces;

/**
 * Custom exception type to wrap exceptions thrown by the Alexa server.
 */
public class AvsException extends Exception {

    public AvsException() {
    }

    public AvsException(String message) {
        super(message);
    }

    public AvsException(Throwable cause) {
        super(cause);
    }

    public AvsException(String message, Throwable cause) {
        super(message, cause);
    }



}
