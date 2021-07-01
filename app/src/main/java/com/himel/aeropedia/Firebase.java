package com.himel.aeropedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amazon.identity.auth.device.api.authorization.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Firebase extends AppCompatActivity {
    Button button;
    List Userlist = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        button = findViewById(R.id.firebase);
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("Aircraft Preference");


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AircraftPreference ap = new AircraftPreference("Boeing 777");
                firebase.push().setValue(ap);

                firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            System.out.println(ds.child("preferredAircraft").getValue().toString());
                        }

                        //System.out.println(dataSnapshot.child("preferredAircraft").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });







    }
}