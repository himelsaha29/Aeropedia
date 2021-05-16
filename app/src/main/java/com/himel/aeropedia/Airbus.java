package com.himel.aeropedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

public class Airbus extends AppCompatActivity {

    private CardView a350Card;
    private CardView a330Card;
    private CardView a380Card;
    private CardView a220Card;
    private CardView a319Card;
    private TextView a319text;
    private Animation translate = null;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airbus);
        a350Card = findViewById(R.id.a350Card);
        a380Card = findViewById(R.id.a380Card);
        a330Card = findViewById(R.id.a330Card);
        a319Card = findViewById(R.id.a319Card);
        a220Card = findViewById(R.id.a220Card);


        a350Card.getBackground().setAlpha(65);
        a330Card.getBackground().setAlpha(65);
        a380Card.getBackground().setAlpha(65);
        a319Card.getBackground().setAlpha(65);
        a220Card.getBackground().setAlpha(65);

        animateCards();

        a350Card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AirbusA350.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


    }




    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        scrollView = findViewById(R.id.main_scroll);
        scrollView.scrollTo(0, scrollView.getTop());
        animateCards();
    }

    private void animateCards() {
        translate = AnimationUtils.loadAnimation(this, R.anim.animation);
        a330Card.setAnimation(translate);
        a350Card.setAnimation(translate);
        a380Card.setAnimation(translate);
        a220Card.setAnimation(translate);
    }
}