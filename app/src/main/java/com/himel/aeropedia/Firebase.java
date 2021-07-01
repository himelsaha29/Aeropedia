package com.himel.aeropedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    int childCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        button = findViewById(R.id.firebase);
        update = findViewById(R.id.button);
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("Aircraft Preference");

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AircraftPreference ap = new AircraftPreference("Boeing 777");
                firebase.push().setValue(ap);
            }
        });



        update.setOnClickListener(new View.OnClickListener() {

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
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

            }
        });


    }
}