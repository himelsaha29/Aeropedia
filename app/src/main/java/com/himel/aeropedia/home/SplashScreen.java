package com.himel.aeropedia.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.himel.aeropedia.R;

import java.util.Timer;
import java.util.TimerTask;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){

            @Override
            public void run() {
                Intent home = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(home);
                overridePendingTransition(R.anim.zoom_out, R.anim.zoom_out);
                finish();
            }}, 2300);
    }
}