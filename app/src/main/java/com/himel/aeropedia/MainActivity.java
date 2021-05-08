package com.himel.aeropedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        bombardier.getBackground().setAlpha(100);
        airbus.getBackground().setAlpha(100);
        boeing.getBackground().setAlpha(100);
        embraer.getBackground().setAlpha(100);

    }


}