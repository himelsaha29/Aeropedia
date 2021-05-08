package com.himel.aeropedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button bombardier;
    private Button airbus;
    private Button embraer;
    private Button boeing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bombardier = findViewById(R.id.bombardier);
        boeing = findViewById(R.id.boeing);
        airbus = findViewById(R.id.airbus);
        embraer = findViewById(R.id.embraer);

        Animation translate = AnimationUtils.loadAnimation(this, R.anim.animation);


        bombardier.getBackground().setAlpha(100);
        bombardier.setAnimation(translate);
        airbus.getBackground().setAlpha(100);
        airbus.setAnimation(translate);
        boeing.getBackground().setAlpha(100);
        boeing.setAnimation(translate);
        embraer.getBackground().setAlpha(100);
        embraer.setAnimation(translate);

    }


}