package com.willblaschko.android.alexa.system;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.AlarmClock;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.willblaschko.android.alexa.AlexaManager;
import com.willblaschko.android.alexa.callbacks.ImplAsyncCallback;
import com.willblaschko.android.alexa.data.Directive;
import com.willblaschko.android.alexa.data.Event;
import com.willblaschko.android.alexa.interfaces.AvsItem;
import com.willblaschko.android.alexa.interfaces.AvsResponse;
import com.willblaschko.android.alexa.interfaces.alerts.AvsDeleteAlertItem;
import com.willblaschko.android.alexa.interfaces.alerts.AvsSetAlertItem;
import com.willblaschko.android.alexa.interfaces.playbackcontrol.AvsMediaNextCommandItem;
import com.willblaschko.android.alexa.interfaces.playbackcontrol.AvsMediaPauseCommandItem;
import com.willblaschko.android.alexa.interfaces.playbackcontrol.AvsMediaPlayCommandItem;
import com.willblaschko.android.alexa.interfaces.playbackcontrol.AvsMediaPreviousCommandItem;
import com.willblaschko.android.alexa.interfaces.response.ResponseParser;
import com.willblaschko.android.alexa.interfaces.speaker.AvsAdjustVolumeItem;
import com.willblaschko.android.alexa.interfaces.speaker.AvsSetMuteItem;
import com.willblaschko.android.alexa.interfaces.speaker.AvsSetVolumeItem;
import com.willblaschko.android.alexa.interfaces.system.AvsSetEndpointItem;
import com.willblaschko.android.alexa.service.DownChannelService;

import java.io.IOException;
import java.text.ParseException;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by will on 4/8/2017.
 */

public class AndroidSystemHandler {
    private static final String TAG = "AndroidSystemHandler";
    private static AndroidSystemHandler instance;
    private Context context;
    private AndroidSystemHandler(Context context){
        this.context = context.getApplicationContext();
    }
    public static AndroidSystemHandler getInstance(Context context){
        if(instance == null){
            instance = new AndroidSystemHandler(context);
        }
        return instance;
    }
    public void handleItems(@NonNull AvsResponse response){
        for(AvsItem current: response){

            Log.i(TAG, "Handling AvsItem: " + current.getClass());
            if(current instanceof AvsSetEndpointItem){
                Log.i(TAG, "Setting URL endpoint: " + ((AvsSetEndpointItem) current).getEndpoint());
                AlexaManager.getInstance(context)
                        .setUrlEndpoint(((AvsSetEndpointItem) current).getEndpoint());

                context.stopService(new Intent(context, DownChannelService.class));
                context.startService(new Intent(context, DownChannelService.class));
            }else if (current instanceof AvsSetVolumeItem) {
                //set our volume
                setVolume(((AvsSetVolumeItem) current).getVolume());
            } else if(current instanceof AvsAdjustVolumeItem){
                //adjust the volume
                adjustVolume(((AvsAdjustVolumeItem) current).getAdjustment());
            } else if(current instanceof AvsSetMuteItem){
                //mute/unmute the device
                setMute(((AvsSetMuteItem) current).isMute());
            }else if(current instanceof AvsMediaPlayCommandItem){
                //fake a hardware "play" press
                sendMediaButton(KeyEvent.KEYCODE_MEDIA_PLAY);
                Log.i(TAG, "Media play command issued");
            }else if(current instanceof AvsMediaPauseCommandItem){
                //fake a hardware "pause" press
                sendMediaButton(KeyEvent.KEYCODE_MEDIA_PAUSE);
                Log.i(TAG, "Media pause command issued");
            }else if(current instanceof AvsMediaNextCommandItem){
                //fake a hardware "next" press
                sendMediaButton(KeyEvent.KEYCODE_MEDIA_NEXT);
                Log.i(TAG, "Media next command issued");
            }else if(current instanceof AvsMediaPreviousCommandItem){
                //fake a hardware "previous" press
                sendMediaButton(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                Log.i(TAG, "Media previous command issued");
            }else if (current instanceof AvsSetAlertItem){
                if(((AvsSetAlertItem) current).isAlarm()){
                    setAlarm((AvsSetAlertItem) current);
                }else if(((AvsSetAlertItem) current).isTimer()){
                    setTimer((AvsSetAlertItem) current);
                }
            }else if (current instanceof AvsDeleteAlertItem){

            }
        }
    }


    public void handleDirective(Directive directive){
        try {
            AvsItem item = ResponseParser.parseDirective(directive);
            handleItem(item);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleItem(AvsItem item){
        if(item == null){
            return;
        }
        AvsResponse response = new AvsResponse();
        response.add(item);
        handleItems(response);
    }

    private void setTimer(final AvsSetAlertItem item){
        Intent i = new Intent(AlarmClock.ACTION_SET_TIMER);
        try {
            int time = (int) ((item.getScheduledTimeMillis() - System.currentTimeMillis()) / 1000);
            i.putExtra(AlarmClock.EXTRA_LENGTH, time);
            i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            context.startActivity(i);
            AlexaManager.getInstance(context)
                    .sendEvent(Event.getSetAlertSucceededEvent(item.getToken()), null);

            //cheating way to tell Alexa that the timer happened successfully--this SHOULD be improved
            //todo make this better
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlexaManager.getInstance(context)
                            .sendEvent(Event.getAlertStartedEvent(item.getToken()), new ImplAsyncCallback<AvsResponse, Exception>() {
                                @Override
                                public void complete() {
                                    AlexaManager.getInstance(context)
                                            .sendEvent(Event.getAlertStoppedEvent(item.getToken()), null);
                                }
                            });
                }
            }, time * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    private void setAlarm(AvsSetAlertItem item){
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        try {
            i.putExtra(AlarmClock.EXTRA_HOUR, item.getHour());
            i.putExtra(AlarmClock.EXTRA_MINUTES, item.getMinutes());
            i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            context.startActivity(i);
            AlexaManager.getInstance(context)
                    .sendEvent(Event.getSetAlertSucceededEvent(item.getToken()), null);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * Force the device to think that a hardware button has been pressed, this is used for Play/Pause/Previous/Next Media commands
     * @param keyCode keycode for the hardware button we're emulating
     */
    private static void sendMediaButton(int keyCode) {
        Instrumentation inst = new Instrumentation();
        inst.sendKeyDownUpSync(keyCode);
    }

    private void adjustVolume(long adjust){
        setVolume(adjust, true);
    }
    private void setVolume(long volume){
        setVolume(volume, false);
    }
    private void setVolume(final long volume, final boolean adjust){

        AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        final int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        long vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (adjust) {
            vol += volume * max / 100;
        } else {
            vol = volume * max / 100;
        }
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) vol, AudioManager.FLAG_VIBRATE);

        AlexaManager.getInstance(context).sendVolumeChangedEvent(volume, vol == 0, null);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(adjust) {
                    Toast.makeText(context, "Volume adjusted.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Volume set to: " + (volume / 10), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void setMute(final boolean isMute){
        AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        am.setStreamMute(AudioManager.STREAM_MUSIC, isMute);

        AlexaManager.getInstance(context).sendMutedEvent(isMute, null);

        Log.i(TAG, "Mute set to : "+isMute);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "Volume " + (isMute ? "muted" : "unmuted"), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
