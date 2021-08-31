package com.himel.aeropedia.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

        boolean onBoardDone = verifyOnBoard();

        Timer timer = new Timer();
        timer.schedule(new TimerTask(){

            @Override
            public void run() {
                if(!onBoardDone) {
                    startActivity(new Intent(SplashScreen.this, OnBoardScreen.class));
                }
                else {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_in);
                finish();
            }}, 900);
    }

    private boolean verifyOnBoard() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        if (prefs.getString("OnBoardDone", "No").equalsIgnoreCase("") || prefs.getString("OnBoardDone", "No") == null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("OnBoardDone", "No");
            editor.apply();
        }
        String onBoardDone = prefs.getString("OnBoardDone", "No");
        if(onBoardDone.equalsIgnoreCase("No")) {
            return false;
        } else {
            return true;
        }
    }
}