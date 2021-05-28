package com.willblaschko.android.alexa.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author will on 4/17/2016.
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        //start our service in the background
        Intent stickyIntent = new Intent(context, DownChannelService.class);
        context.startService(stickyIntent);
        Log.i(TAG, "Started down channel service.");
    }
}
