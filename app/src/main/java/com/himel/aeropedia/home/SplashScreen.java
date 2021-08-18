package com.himel.aeropedia.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.himel.aeropedia.R;

import java.util.Timer;
import java.util.TimerTask;


public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);


        Timer timer = new Timer();
        timer.schedule(new TimerTask(){

            @Override
            public void run() {
                Intent home = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(home);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_in);
                finish();
            }}, 2300);
    }
}