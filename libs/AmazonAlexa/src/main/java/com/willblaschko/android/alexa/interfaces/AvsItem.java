package com.willblaschko.android.alexa.interfaces;

/**
 * @author wblaschko on 8/13/15.
 */
public abstract class AvsItem {
    String token;
    public AvsItem(String token){
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
