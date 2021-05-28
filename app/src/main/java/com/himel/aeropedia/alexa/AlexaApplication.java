package com.himel.aeropedia.alexa;

import android.app.Application;

public class AlexaApplication extends Application {

    //Our Amazon application product ID, this is passed to the server when we authenticate
    private static final String PRODUCT_ID = Global.PRODUCT_ID;


    //Our Application instance if we need to reference it directly
    private static AlexaApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    /**
     * Return a reference to our mInstance instance
     * @return our current application instance, created in onCreate()
     */
    public static AlexaApplication getInstance(){
        return mInstance;
    }



}
