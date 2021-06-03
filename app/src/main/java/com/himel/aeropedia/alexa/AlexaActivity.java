package com.himel.aeropedia.alexa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.himel.aeropedia.R;
import com.willblaschko.android.alexa.requestbody.DataRequestBody;

import java.io.IOException;

import ee.ioc.phon.android.speechutils.AudioRecorder;
import ee.ioc.phon.android.speechutils.RawAudioRecorder;
import io.alterac.blurkit.BlurLayout;
import okio.BufferedSink;
import soup.neumorphism.NeumorphImageButton;


public class AlexaActivity extends CoreActivity {

    private View statusBar;
    private TextView status;
    private View loading;
    LottieAnimationView listening;
    LottieAnimationView speaking;

    private final static int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int AUDIO_RATE = 16000;
    private RawAudioRecorder recorder;
    private NeumorphImageButton recorderView;

    private boolean speak = false;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alexa);
        recorderView = findViewById(R.id.recorder);
        recorderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speak) {
                    AlexaActivity.super.avsQueue.clear();
                    onStop();
                    stateFinished();
                    speaking.setVisibility(View.GONE);
                    listening.setVisibility(View.GONE);
                }
                else if(recorder == null) {
                    startListening();
                }else{
                    stopListening();
                    listening.setVisibility(View.GONE);
                }
            }
        });


        //statusBar = findViewById(R.id.status_bar);
        status = findViewById(R.id.status);
        loading = findViewById(R.id.loading);
        listening = findViewById(R.id.listening);
        speaking = findViewById(R.id.speaking);

    }

    @Override
    protected void startListening() {

        if(recorder == null){
            recorder = new RawAudioRecorder(AUDIO_RATE);
        }
        recorder.start();
        alexaManager.sendAudioRequest(requestBody, getRequestCallback());
        listening.setVisibility(View.VISIBLE);
        speaking.setVisibility(View.GONE);
    }

    private DataRequestBody requestBody = new DataRequestBody() {
        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            while (recorder != null && recorder.getState() != AudioRecorder.State.ERROR && !recorder.isPausing()) {
                if(recorder != null) {
                    final float rmsdb = recorder.getRmsdb();
                    if(recorderView != null) {
                        recorderView.post(new Runnable() {
                            @Override
                            public void run() {
                                //recorderView.setRmsdbLevel(rmsdb);
                                //listening.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    if(sink != null && recorder != null) {
                        sink.write(recorder.consumeRecording());
                    }
//                    if(BuildConfig.DEBUG){
//                        // Log.i(TAG, "Received audio");
//                        // Log.i(TAG, "RMSDB: " + rmsdb);
//                    }
                }

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopListening();
        }

    };


    private void stopListening(){
        if(recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //tear down our recorder on stop
        if(recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        listening.setVisibility(View.GONE);
        speak = false;
    }


    protected void stateListening(){

        if(status != null) {
            status.setText(R.string.status_listening);
            loading.setVisibility(View.GONE);
            //statusBar.animate().alpha(1);
            speak = false;
        }
        listening.setVisibility(View.VISIBLE);
    }
    protected void stateProcessing(){

        if(status != null) {
            status.setText(R.string.status_processing);
            loading.setVisibility(View.VISIBLE);
            //statusBar.animate().alpha(1);
        }
        speak = false;
    }
    protected void stateSpeaking(){

        speak = true;
        if(status != null) {
            status.setText(R.string.status_speaking);
            loading.setVisibility(View.VISIBLE);
            //statusBar.animate().alpha(1);
        }
        listening.setVisibility(View.GONE);
        speaking.setVisibility(View.VISIBLE);
    }
    protected void statePrompting(){

        if(status != null) {
            status.setText("");
            loading.setVisibility(View.VISIBLE);
            //statusBar.animate().alpha(1);
        }
    }
    protected void stateFinished(){
        if(status != null) {
            status.setText("");
            //statusBar.animate().alpha(0);
            speak = false;
        }
        loading.setVisibility(View.GONE);
        speaking.setVisibility(View.GONE);
    }
    protected void stateNone(){
        //statusBar.animate().alpha(0);
        speaking.setVisibility(View.GONE);
        listening.setVisibility(View.GONE);
        speak = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        }
    }



}