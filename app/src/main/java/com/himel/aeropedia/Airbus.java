package com.himel.aeropedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.royrodriguez.transitionbutton.TransitionButton;

public class Airbus extends AppCompatActivity {

    private TransitionButton bombardier;
    private TransitionButton embraer;
    private TransitionButton boeing;
    private TransitionButton airbus;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airbus);
        bombardier = findViewById(R.id.a320);
        boeing = findViewById(R.id.a340);
        airbus = findViewById(R.id.a350);
        embraer = findViewById(R.id.a380);

        Animation translate = AnimationUtils.loadAnimation(this, R.anim.animation);


        bombardier.getBackground().setAlpha(100);
        bombardier.setAnimation(translate);
        airbus.getBackground().setAlpha(100);
        airbus.setAnimation(translate);
        boeing.getBackground().setAlpha(100);
        boeing.setAnimation(translate);
        embraer.getBackground().setAlpha(100);
        embraer.setAnimation(translate);



        airbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                airbus.startAnimation();

                // Do your networking task or background work here.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        airbus.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                            @Override
                            public void onAnimationStopEnd() {
                                Intent intent = new Intent(getBaseContext(), Airbus.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                            }
                        });
                    }
                }, 200);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }


}