package com.himel.aeropedia.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.aeropedia.R;
import com.himel.aeropedia.firebase.AircraftPreference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soup.neumorphism.NeumorphButton;

public class Firebase extends AppCompatActivity {
    private Button button;
    private Button update;
    private Map<String, Integer> map;
    private List<String> selected = new ArrayList<>();
    private int childCount = 0;
    private DecimalFormat df = new DecimalFormat("###.##");
    private Set<String> chosenAircrafts = new HashSet<String>();;
    private Animation translate = null;
    private CardView textCard;
    private ConstraintLayout layout;
    private ScrollView scroll;

    Button a350Button, a340Button, a380Button, b787Button, cessnaButton, havillandButton, save;

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
        textCard = findViewById(R.id.textCard);
        layout = findViewById(R.id.buttonContainer);
        scroll = findViewById(R.id.firebaseScroll);
        Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        scroll.setFadingEdgeLength((int) (display.getHeight() *0.12));
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("Aircraft Preference");

        animate();
        assignButtons();

        textCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        map = new HashMap<>();
                        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                        Set<String> aircraftsChosen = prefs.getStringSet("ChosenAircrafts", null);

                        System.out.println(dataSnapshot.getChildrenCount());

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (!map.containsKey(ds.child("preferredAircraft").getValue().toString())) {
                                map.put(ds.child("preferredAircraft").getValue().toString(), 1);
                            } else {
                                int count = map.get(ds.child("preferredAircraft").getValue().toString());
                                map.put(ds.child("preferredAircraft").getValue().toString(), ++count);
                            }

                            childCount++;

                            if (childCount == dataSnapshot.getChildrenCount()) {

                                switchLayout();
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int height = displayMetrics.heightPixels;
                                int width = displayMetrics.widthPixels;

                                for (String s : map.keySet()) {

                                    if (s.equalsIgnoreCase("Airbus A350")) {
                                        NeumorphButton button = findViewById(R.id.a350);
                                        TextView status = findViewById(R.id.a350status);
                                        status.setText("Airbus A350 \n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A350")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }

                                    if (s.equalsIgnoreCase("Airbus A340")) {
                                        NeumorphButton button = findViewById(R.id.a340);
                                        TextView status = findViewById(R.id.a340status);
                                        status.setText("Airbus A340 \n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A340")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                        System.out.println();
                                    }

                                    if (s.equalsIgnoreCase("Airbus A380")) {
                                        NeumorphButton button = findViewById(R.id.a380);
                                        TextView status = findViewById(R.id.a380status);
                                        status.setText("Airbus A380 \n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A380")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }

                                    if (s.equalsIgnoreCase("Boeing 787")) {
                                        NeumorphButton button = findViewById(R.id.b787);
                                        TextView status = findViewById(R.id.b787status);
                                        status.setText("Boeing 787 \n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Boeing 787")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }


                                    System.out.println(s + " : " + map.get(s));
                                    System.out.println(childCount);

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


        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (String aircraft : selected) {
                    AircraftPreference preference = new AircraftPreference(aircraft);
                    firebase.push().setValue(preference);
                    chosenAircrafts.add(aircraft);
                }
                SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                editor.putStringSet("ChosenAircrafts", chosenAircrafts);
                editor.apply();
            }
        });


    }


    private void assignButtons() {
        a350Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a350) {
                    a350Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a350Button.getText());
                    a350 = true;
                } else {
                    a350Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a350Button.getText());
                    a350 = false;
                }
            }
        });

        a340Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a340) {
                    a340Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a340Button.getText());
                    a340 = true;
                } else {
                    a340Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a340Button.getText());
                    a340 = false;
                }
            }
        });

        a380Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a380) {
                    a380Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a380Button.getText());
                    a380 = true;
                } else {
                    a380Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a380Button.getText());
                    a380 = false;
                }
            }
        });

        b787Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b787) {
                    b787Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) b787Button.getText());
                    b787 = true;
                } else {
                    b787Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) b787Button.getText());
                    b787 = false;
                }
            }
        });

        cessnaButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!citation) {
                    cessnaButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) cessnaButton.getText());
                    citation = true;
                } else {
                    cessnaButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) cessnaButton.getText());
                    citation = false;
                }
            }
        });

        havillandButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!de_havilland) {
                    havillandButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) havillandButton.getText());
                    de_havilland = true;
                } else {
                    havillandButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) havillandButton.getText());
                    de_havilland = false;
                }
            }
        });
    }

    private void switchLayout() {
        setContentView(R.layout.activity_firebase_results);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void animate() {
        translate = AnimationUtils.loadAnimation(this, R.anim.animation);
        textCard.setAnimation(translate);
        layout.setAnimation(translate);
    }

}