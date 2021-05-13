package com.himel.aeropedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import android.widget.ScrollView;
import android.widget.Toast;

import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

public class MainActivity extends AppCompatActivity {

    private CardView airbusCard;
    private CardView boeingCard;
    private CardView bombardierCard;
    private CardView antonovCard;
    private CardView embraerCard;
    private boolean flag = false;
    private Animation translate = null;
    private ScrollView scrollView;
    public final FragmentManager fragmentManager = getSupportFragmentManager();

    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        airbusCard = findViewById(R.id.airbusCard);
        boeingCard = findViewById(R.id.boeingCard);
        bombardierCard = findViewById(R.id.bombardierCard);
        embraerCard = findViewById(R.id.embraerCard);
        antonovCard = findViewById(R.id.antonovCard);


        airbusCard.getBackground().setAlpha(65);
        boeingCard.getBackground().setAlpha(65);
        bombardierCard.getBackground().setAlpha(65);
        embraerCard.getBackground().setAlpha(65);
        antonovCard.getBackground().setAlpha(65);



        animateCards();


        airbusCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Airbus.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        antonovCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AirbusA350.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        scrollView = findViewById(R.id.main_scroll);
        scrollView.scrollTo(0, scrollView.getTop());
        animateCards();
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

    private void animateCards() {
        translate = AnimationUtils.loadAnimation(this, R.anim.animation);
        airbusCard.setAnimation(translate);
        boeingCard.setAnimation(translate);
        bombardierCard.setAnimation(translate);
        embraerCard.setAnimation(translate);
        antonovCard.setAnimation(translate);
    }


}