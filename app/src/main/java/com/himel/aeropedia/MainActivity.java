package com.himel.aeropedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.royrodriguez.transitionbutton.TransitionButton;

public class MainActivity extends AppCompatActivity {

    private TransitionButton bombardier;
    private TransitionButton embraer;
    private TransitionButton boeing;
    private TransitionButton airbus;
    private boolean flag = false;
    private FlowingDrawer mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public void onBackPressed() {
        if(!flag) {
            Toast.makeText(MainActivity.this, "Press again to exit", Toast.LENGTH_LONG).show();
            flag = true;
            new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // no function
                }
                public void onFinish() {
                    flag = false;
                }
            }.start();
        } else {
            this.finishAffinity();
        }
    }


}