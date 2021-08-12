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
import soup.neumorphism.NeumorphCardView;

public class Firebase extends AppCompatActivity {
    private Button button;
    private Button update;
    private Map<String, Integer> map;
    private List<String> selected = new ArrayList<>();
    private int childCount = 0;
    private DecimalFormat df = new DecimalFormat("###.##");
    private Set<String> chosenAircrafts = new HashSet<String>();;
    private Animation translate = null;
    private NeumorphCardView textCard;
    private ConstraintLayout layout;
    private ScrollView scroll;

    private Button a220Button, a300Button, a310Button, a318Button, a319Button, a320Button, a321Button, a319neoButton, a320neoButton, a321neoButton, a330Button, a330neoButton,
            a340Button, a350Button, a380Button, belugaButton, an22Button, an72Button, an124Button, an225Button, cessnaButton, havillandButton, save;

    boolean a220, a300, a310, a318, a319, a320, a321, a319neo, a320neo, a321neo, a330, a330neo, a340, a350, a380, beluga, an22, an72, an124, an225, citation, de_havilland;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        a220Button = findViewById(R.id.a220);
        a300Button = findViewById(R.id.a300);
        a310Button = findViewById(R.id.a310);
        a318Button = findViewById(R.id.a318);
        a319Button = findViewById(R.id.a319);
        a320Button = findViewById(R.id.a320);
        a321Button = findViewById(R.id.a321);
        a319neoButton = findViewById(R.id.a319neo);
        a320neoButton = findViewById(R.id.a320neo);
        a321neoButton = findViewById(R.id.a321neo);
        a330Button = findViewById(R.id.a330);
        a330neoButton = findViewById(R.id.a330neo);
        a340Button = findViewById(R.id.a340);
        a350Button = findViewById(R.id.a350);
        a380Button = findViewById(R.id.a380);
        belugaButton = findViewById(R.id.beluga);
        an22Button = findViewById(R.id.an_22);
        an72Button = findViewById(R.id.an_72);
        an124Button = findViewById(R.id.an_124);
        an225Button = findViewById(R.id.an_225);


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

                        System.out.println("Children count : " + dataSnapshot.getChildrenCount());

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

                                    if (s.equalsIgnoreCase("Airbus A220")) {
                                        NeumorphButton button = findViewById(R.id.a220);
                                        TextView status = findViewById(R.id.a220status);
                                        status.setText("Airbus A220\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A220")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A300")) {
                                        NeumorphButton button = findViewById(R.id.a300);
                                        TextView status = findViewById(R.id.a300status);
                                        status.setText("Airbus A300\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A300")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A310")) {
                                        NeumorphButton button = findViewById(R.id.a310);
                                        TextView status = findViewById(R.id.a310status);
                                        status.setText("Airbus A310\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A310")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A318")) {
                                        NeumorphButton button = findViewById(R.id.a318);
                                        TextView status = findViewById(R.id.a318status);
                                        status.setText("Airbus A318\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A318")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A319")) {
                                        NeumorphButton button = findViewById(R.id.a319);
                                        TextView status = findViewById(R.id.a319status);
                                        status.setText("Airbus A319\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A319")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A320")) {
                                        NeumorphButton button = findViewById(R.id.a320);
                                        TextView status = findViewById(R.id.a320status);
                                        status.setText("Airbus A320\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A320")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A321")) {
                                        NeumorphButton button = findViewById(R.id.a321);
                                        TextView status = findViewById(R.id.a321status);
                                        status.setText("Airbus A321\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A321")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A319neo")) {
                                        NeumorphButton button = findViewById(R.id.a319neo);
                                        TextView status = findViewById(R.id.a319neostatus);
                                        status.setText("Airbus A319neo\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A319neo")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A320neo")) {
                                        NeumorphButton button = findViewById(R.id.a320neo);
                                        TextView status = findViewById(R.id.a320neostatus);
                                        status.setText("Airbus A320neo\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A320neo")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A321neo")) {
                                        NeumorphButton button = findViewById(R.id.a321neo);
                                        TextView status = findViewById(R.id.a321neostatus);
                                        status.setText("Airbus A321neo\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A321neo")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A330")) {
                                        NeumorphButton button = findViewById(R.id.a330);
                                        TextView status = findViewById(R.id.a330status);
                                        status.setText("Airbus A330\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A330")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A330neo")) {
                                        NeumorphButton button = findViewById(R.id.a330neo);
                                        TextView status = findViewById(R.id.a330neostatus);
                                        status.setText("Airbus A330neo\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus A330neo")) {
                                            button.setBackgroundColor(Color.RED);
                                        }
                                    }
                                    if (s.equalsIgnoreCase("Airbus A340")) {
                                        NeumorphButton button = findViewById(R.id.a340);
                                        TextView status = findViewById(R.id.a340status);
                                        status.setText("Airbus A340\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
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
                                    }
                                    if (s.equalsIgnoreCase("Airbus A350")) {
                                        NeumorphButton button = findViewById(R.id.a350);
                                        TextView status = findViewById(R.id.a350status);
                                        status.setText("Airbus A350\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
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
                                    if (s.equalsIgnoreCase("Airbus A380")) {
                                        NeumorphButton button = findViewById(R.id.a380);
                                        TextView status = findViewById(R.id.a380status);
                                        status.setText("Airbus A380\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
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
                                    if (s.equalsIgnoreCase("Airbus Beluga")) {
                                        NeumorphButton button = findViewById(R.id.beluga);
                                        TextView status = findViewById(R.id.belugastatus);
                                        status.setText("Airbus Beluga\n" + df.format(((float) map.get(s) / (float) childCount) * 100) + "%");
                                        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                                        float size = (float) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        if(size <= 0 || size <= 100) {
                                            layoutParams.height = 100;
                                        } else {
                                            layoutParams.height = (int) (((float) map.get(s) / (float) childCount) * 0.7 * height);
                                        }
                                        button.setLayoutParams(layoutParams);
                                        if(aircraftsChosen.contains("Airbus Beluga")) {
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

        a220Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a220) {
                    a220Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a220Button.getText());
                    a220 = true;
                } else {
                    a220Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a220Button.getText());
                    a220 = false;
                }
            }
        });

        a300Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a300) {
                    a300Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a300Button.getText());
                    a300 = true;
                } else {
                    a300Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a300Button.getText());
                    a300 = false;
                }
            }
        });

        a310Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a310) {
                    a310Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a310Button.getText());
                    a310 = true;
                } else {
                    a310Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a310Button.getText());
                    a310 = false;
                }
            }
        });

        a318Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a318) {
                    a318Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a318Button.getText());
                    a318 = true;
                } else {
                    a318Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a318Button.getText());
                    a318 = false;
                }
            }
        });

        a319Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a319) {
                    a319Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a319Button.getText());
                    a319 = true;
                } else {
                    a319Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a319Button.getText());
                    a319 = false;
                }
            }
        });

        a320Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a320) {
                    a320Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a320Button.getText());
                    a320 = true;
                } else {
                    a320Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a320Button.getText());
                    a320 = false;
                }
            }
        });

        a321Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a321) {
                    a321Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a321Button.getText());
                    a321 = true;
                } else {
                    a321Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a321Button.getText());
                    a321 = false;
                }
            }
        });

        a319neoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a319neo) {
                    a319neoButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a319neoButton.getText());
                    a319neo = true;
                } else {
                    a319neoButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a319neoButton.getText());
                    a319neo = false;
                }
            }
        });

        a320neoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a320neo) {
                    a320neoButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a320neoButton.getText());
                    a320neo = true;
                } else {
                    a320neoButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a320neoButton.getText());
                    a320neo = false;
                }
            }
        });

        a321neoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a321neo) {
                    a321neoButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a321neoButton.getText());
                    a321neo = true;
                } else {
                    a321neoButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a321neoButton.getText());
                    a321neo = false;
                }
            }
        });

        a330Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a330) {
                    a330Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a330Button.getText());
                    a330 = true;
                } else {
                    a321neoButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a330Button.getText());
                    a330 = false;
                }
            }
        });

        a330neoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a330neo) {
                    a330neoButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) a330neoButton.getText());
                    a330neo = true;
                } else {
                    a330neoButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) a330neoButton.getText());
                    a330neo = false;
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

        belugaButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!beluga) {
                    belugaButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) belugaButton.getText());
                    beluga = true;
                } else {
                    belugaButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) belugaButton.getText());
                    beluga = false;
                }
            }
        });

        an22Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!an22) {
                    an22Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) an22Button.getText());
                    an22 = true;
                } else {
                    an22Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) an22Button.getText());
                    an22 = false;
                }
            }
        });

        an72Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!an72) {
                    an72Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) an72Button.getText());
                    an72 = true;
                } else {
                    an72Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) an72Button.getText());
                    an72 = false;
                }
            }
        });

        an124Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!an124) {
                    an124Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) an124Button.getText());
                    an124 = true;
                } else {
                    an124Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) an124Button.getText());
                    an124 = false;
                }
            }
        });

        an225Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!an225) {
                    an225Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) an225Button.getText());
                    an225 = true;
                } else {
                    an225Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) an225Button.getText());
                    an225 = false;
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