package com.himel.aeropedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Airbus extends AppCompatActivity {

    private CardView a350Card;
    private CardView a330Card;
    private CardView a380Card;
    private CardView a319Card;
    private TextView a319text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airbus);
        a350Card = findViewById(R.id.a350Card);
        a380Card = findViewById(R.id.a380Card);
        a330Card = findViewById(R.id.a330Card);
        a319Card = findViewById(R.id.a319Card);


        a350Card.getBackground().setAlpha(65);
        a330Card.getBackground().setAlpha(65);
        a380Card.getBackground().setAlpha(65);
        a319Card.getBackground().setAlpha(65);


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
}