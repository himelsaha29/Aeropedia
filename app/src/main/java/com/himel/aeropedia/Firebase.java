package com.himel.aeropedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.amazon.identity.auth.device.api.authorization.User;
import com.amazon.identity.auth.device.utils.JSONUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.aeropedia.alexa.Global;
import com.himel.aeropedia.flightmap.FlightMap;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import soup.neumorphism.NeumorphButton;

public class Firebase extends AppCompatActivity {
    Button button;
    Button update;
    Map<String, Integer> map;
    List<String> selected = new ArrayList<>();
    int childCount = 0;

    Button a350Button, a340Button, a380Button, b787Button, cessnaButton, havillandButton, save, show;

    boolean a350, a340, a380, b787, citation, de_havilland;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        a350Button = findViewById(R.id.a350);
        a340Button = findViewById(R.id.a340);
        a380Button = findViewById(R.id.a380);
        b787Button = findViewById(R.id.b787);
        cessnaButton = findViewById(R.id.citation);
        havillandButton = findViewById(R.id.havilland);
        save = findViewById(R.id.save);
        show = findViewById(R.id.show);
        //button = findViewById(R.id.firebase);
        //update = findViewById(R.id.button);
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("Aircraft Preference");

//        button.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                AircraftPreference ap = new AircraftPreference("Boeing 777");
//                firebase.push().setValue(ap);
//            }
//        });
//
//
//

        assignButtons();

        show.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        map = new HashMap<>();
                        System.out.println(dataSnapshot.getChildrenCount());
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if(!map.containsKey(ds.child("preferredAircraft").getValue().toString())) {
                                map.put(ds.child("preferredAircraft").getValue().toString(), 1);
                            } else {
                                int x = map.get(ds.child("preferredAircraft").getValue().toString());
                                map.put(ds.child("preferredAircraft").getValue().toString(), ++x);
                            }

                            childCount++;

                            if(childCount == dataSnapshot.getChildrenCount()) {

                                for(String s : map.keySet()) {
                                    System.out.println(s + " : " +  map.get(s));
                                }

                                childCount = 0;
                                switchLayout();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });



            }
        });



        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for(String aircraft : selected) {
                    AircraftPreference preference = new AircraftPreference(aircraft);
                    firebase.push().setValue(preference);
                }
            }
        });


    }


    private void assignButtons() {
        a350Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!a350) {
                    a350Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a350Button.getText());
                    a350 = true;
                }
                else {
                    a350Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a350Button.getText());
                    a350 = false;
                }
            }
        });

        a340Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!a340) {
                    a340Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a340Button.getText());
                    a340 = true;
                }
                else {
                    a340Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a340Button.getText());
                    a340 = false;
                }
            }
        });

        a380Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!a380) {
                    a380Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a380Button.getText());
                    a380 = true;
                }
                else {
                    a380Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a380Button.getText());
                    a380 = false;
                }
            }
        });

        b787Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!b787) {
                    b787Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) b787Button.getText());
                    b787 = true;
                }
                else {
                    b787Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) b787Button.getText());
                    b787 = false;
                }
            }
        });

        cessnaButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!citation) {
                    cessnaButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) cessnaButton.getText());
                    citation = true;
                }
                else {
                    cessnaButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) cessnaButton.getText());
                    citation = false;
                }
            }
        });

        havillandButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!de_havilland) {
                    havillandButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) havillandButton.getText());
                    de_havilland = true;
                }
                else {
                    havillandButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) havillandButton.getText());
                    de_havilland = false;
                }
            }
        });
    }

    private void switchLayout() {
        setContentView(R.layout.activity_firebase_results);
        NeumorphButton button = findViewById(R.id.button);
        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
        ((ViewGroup.LayoutParams) layoutParams).height = dpToPx(130, this);
        button.setLayoutParams(layoutParams);
        button.setBackgroundColor(Color.RED);
        button.setTextColor(Color.RED);
    }

    private static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}