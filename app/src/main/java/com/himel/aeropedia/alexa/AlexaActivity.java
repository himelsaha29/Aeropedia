package com.himel.aeropedia.alexa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.TextView;

import com.himel.aeropedia.R;
import com.willblaschko.android.alexa.requestbody.DataRequestBody;
import com.willblaschko.android.recorderview.RecorderView;

import java.io.IOException;

import ee.ioc.phon.android.speechutils.AudioRecorder;
import ee.ioc.phon.android.speechutils.RawAudioRecorder;
import okio.BufferedSink;


public class AlexaActivity extends CoreActivity {

    private View statusBar;
    private TextView status;
    private View loading;

    private final static int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int AUDIO_RATE = 16000;
    private RawAudioRecorder recorder;
    private RecorderView recorderView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_audio);
        recorderView = (RecorderView) findViewById(R.id.recorder);
        recorderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recorder == null) {
                    startListening();
                }else{
                    stopListening();
                }
            }
        });


        //statusBar = findViewById(R.id.status_bar);
        status = (TextView) findViewById(R.id.status);
        loading = findViewById(R.id.loading);

    }

    @Override
    protected void startListening() {

        if(recorder == null){
            recorder = new RawAudioRecorder(AUDIO_RATE);
        }
        recorder.start();
        alexaManager.sendAudioRequest(requestBody, getRequestCallback());

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
                                recorderView.setRmsdbLevel(rmsdb);
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
    }


    protected void stateListening(){

        if(status != null) {
            status.setText(R.string.status_listening);
            loading.setVisibility(View.GONE);
            //statusBar.animate().alpha(1);
        }
    }
    protected void stateProcessing(){

        if(status != null) {
            status.setText(R.string.status_processing);
            loading.setVisibility(View.VISIBLE);
            //statusBar.animate().alpha(1);
        }
    }
    protected void stateSpeaking(){

        if(status != null) {
            status.setText(R.string.status_speaking);
            loading.setVisibility(View.VISIBLE);
            //statusBar.animate().alpha(1);
        }
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
            loading.setVisibility(View.GONE);
            //statusBar.animate().alpha(0);
        }
    }
    protected void stateNone(){
        //statusBar.animate().alpha(0);
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