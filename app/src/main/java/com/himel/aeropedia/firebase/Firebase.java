package com.himel.aeropedia.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himel.aeropedia.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    private DecimalFormat df = new DecimalFormat("###.##");
    private Set<String> chosenAircrafts = new HashSet<String>();
    private Animation translate = null;
    private NeumorphCardView textCard;
    private ConstraintLayout layout;
    private ScrollView scroll;
    private int totalAircraft = 0;

    private Button a220Button, a300Button, a310Button, a318Button, a319Button, a320Button, a321Button, a319neoButton, a320neoButton, a321neoButton, a330Button, a330neoButton,
            a340Button, a350Button, a380Button, belugaButton, an22Button, an72Button, an124Button, an225Button, b737Button, b747Button, b757Button, b767Button,
            b777Button, b787Button, bombardierChallenger650Button, bombardierCRJButton, bombardierLearjetButton, bombardierGlobalButton,
            embraerERJButton, embraerEJetE2Button, embraerLineageButton, embraerPhenomButton, cessnaSkylaneButton, cessnaCaravanButton, cessnaLatitudeButton,
            cessnaLongitudeButton, gulfstreamG650Button, gulfstreamG280Button, gulfstreamGIVButton, save;

    boolean a220, a300, a310, a318, a319, a320, a321, a319neo, a320neo, a321neo, a330, a330neo, a340, a350, a380, beluga, an22, an72, an124, an225, b737, b747, b757, b767,
            b777, b787, bombardierChallenger650, bombardierCRJ, bombardierLearjet, bombardierGlobal, embraerERJ, embraerEJetE2, embraerLineage, embraerPhenom, cessnaSkylane,
            cessnaCaravan, cessnaLatitude, cessnaLongitude, gulfstreamG650, gulfstreamG280, gulfstreamGIV;


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
        b737Button = findViewById(R.id.b737);
        b747Button = findViewById(R.id.b747);
        b757Button = findViewById(R.id.b757);
        b767Button = findViewById(R.id.b767);
        b777Button = findViewById(R.id.b777);
        b787Button = findViewById(R.id.b787);
        bombardierChallenger650Button = findViewById(R.id.bombardier_challenger_650);
        bombardierCRJButton = findViewById(R.id.bombardier_crj_100_200);
        bombardierLearjetButton = findViewById(R.id.bombardier_learjet_75);
        bombardierGlobalButton = findViewById(R.id.bombardier_global_7500);
        embraerERJButton = findViewById(R.id.embraer_erj);
        embraerEJetE2Button = findViewById(R.id.embraer_e_jet_e2_family);
        embraerLineageButton = findViewById(R.id.embraer_lineage);
        embraerPhenomButton = findViewById(R.id.embraer_phenom);
        cessnaSkylaneButton = findViewById(R.id.cessna_skylane);
        cessnaCaravanButton = findViewById(R.id.cessna_caravan);
        cessnaLongitudeButton = findViewById(R.id.cessna_longitude);
        cessnaLatitudeButton = findViewById(R.id.cessna_latitude);
        gulfstreamG650Button = findViewById(R.id.gulfstream_g650);
        gulfstreamG280Button = findViewById(R.id.gulfstream_g280);


        gulfstreamGIVButton = findViewById(R.id.gulfstream_giv);
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

                        System.out.println("Children count : " + dataSnapshot.getChildrenCount());

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (!map.containsKey(ds.child("preferredAircraft").getValue().toString())) {
                                map.put(ds.child("preferredAircraft").getValue().toString(), 1);
                            } else {
                                int count = map.get(ds.child("preferredAircraft").getValue().toString());
                                count++;
                                map.put(ds.child("preferredAircraft").getValue().toString(), count);
                            }

                        }
                        drawGraph();
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

        b737Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b737) {
                    b737Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) b737Button.getText());
                    b737 = true;
                } else {
                    b737Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) b737Button.getText());
                    b737 = false;
                }
            }
        });

        b747Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b747) {
                    b747Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) b747Button.getText());
                    b747 = true;
                } else {
                    b747Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) b747Button.getText());
                    b747 = false;
                }
            }
        });

        b757Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b757) {
                    b757Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) b757Button.getText());
                    b757 = true;
                } else {
                    b757Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) b757Button.getText());
                    b757 = false;
                }
            }
        });

        b767Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b767) {
                    b767Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) b767Button.getText());
                    b767 = true;
                } else {
                    b767Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) b767Button.getText());
                    b767 = false;
                }
            }
        });

        b777Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b777) {
                    b777Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) b777Button.getText());
                    b777 = true;
                } else {
                    b777Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) b777Button.getText());
                    b777 = false;
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

        bombardierChallenger650Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!bombardierChallenger650) {
                    bombardierChallenger650Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) bombardierChallenger650Button.getText());
                    bombardierChallenger650 = true;
                } else {
                    bombardierChallenger650Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) bombardierChallenger650Button.getText());
                    bombardierChallenger650 = false;
                }
            }
        });

        bombardierCRJButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!bombardierCRJ) {
                    bombardierCRJButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) bombardierCRJButton.getText());
                    bombardierCRJ = true;
                } else {
                    bombardierCRJButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) bombardierCRJButton.getText());
                    bombardierCRJ = false;
                }
            }
        });

        bombardierLearjetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!bombardierLearjet) {
                    bombardierLearjetButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) bombardierLearjetButton.getText());
                    bombardierLearjet = true;
                } else {
                    bombardierLearjetButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) bombardierLearjetButton.getText());
                    bombardierLearjet = false;
                }
            }
        });

        bombardierGlobalButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!bombardierGlobal) {
                    bombardierGlobalButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) bombardierGlobalButton.getText());
                    bombardierGlobal = true;
                } else {
                    bombardierGlobalButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) bombardierGlobalButton.getText());
                    bombardierGlobal = false;
                }
            }
        });

        embraerERJButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!embraerERJ) {
                    embraerERJButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) embraerERJButton.getText());
                    embraerERJ = true;
                } else {
                    embraerERJButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) embraerERJButton.getText());
                    embraerERJ = false;
                }
            }
        });

        embraerEJetE2Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!embraerEJetE2) {
                    embraerEJetE2Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) embraerEJetE2Button.getText());
                    embraerEJetE2 = true;
                } else {
                    embraerEJetE2Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) embraerEJetE2Button.getText());
                    embraerEJetE2 = false;
                }
            }
        });

        embraerLineageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!embraerLineage) {
                    embraerLineageButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) embraerLineageButton.getText());
                    embraerLineage = true;
                } else {
                    embraerLineageButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) embraerLineageButton.getText());
                    embraerLineage = false;
                }
            }
        });

        embraerPhenomButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!embraerPhenom) {
                    embraerPhenomButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) embraerPhenomButton.getText());
                    embraerPhenom = true;
                } else {
                    embraerPhenomButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) embraerPhenomButton.getText());
                    embraerPhenom = false;
                }
            }
        });

        cessnaSkylaneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cessnaSkylane) {
                    cessnaSkylaneButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) cessnaSkylaneButton.getText());
                    cessnaSkylane = true;
                } else {
                    cessnaSkylaneButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) cessnaSkylaneButton.getText());
                    cessnaSkylane = false;
                }
            }
        });

        cessnaCaravanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cessnaCaravan) {
                    cessnaCaravanButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) cessnaCaravanButton.getText());
                    cessnaCaravan = true;
                } else {
                    cessnaCaravanButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) cessnaCaravanButton.getText());
                    cessnaCaravan = false;
                }
            }
        });

        cessnaLatitudeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cessnaLatitude) {
                    cessnaLatitudeButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) cessnaLatitudeButton.getText());
                    cessnaLatitude = true;
                } else {
                    cessnaLatitudeButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) cessnaLatitudeButton.getText());
                    cessnaLatitude = false;
                }
            }
        });

        cessnaLongitudeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cessnaLongitude) {
                    cessnaLongitudeButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) cessnaLongitudeButton.getText());
                    cessnaLongitude = true;
                } else {
                    cessnaLongitudeButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) cessnaLongitudeButton.getText());
                    cessnaLongitude = false;
                }
            }
        });

        gulfstreamG650Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!gulfstreamG650) {
                    gulfstreamG650Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) gulfstreamG650Button.getText());
                    gulfstreamG650 = true;
                } else {
                    gulfstreamG650Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) gulfstreamG650Button.getText());
                    gulfstreamG650 = false;
                }
            }
        });

        gulfstreamG280Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!gulfstreamG280) {
                    gulfstreamG280Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) gulfstreamG280Button.getText());
                    gulfstreamG280 = true;
                } else {
                    gulfstreamG280Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) gulfstreamG280Button.getText());
                    gulfstreamG280 = false;
                }
            }
        });







        gulfstreamGIVButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!gulfstreamGIV) {
                    gulfstreamGIVButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    selected.add((String) gulfstreamGIVButton.getText());
                    gulfstreamGIV = true;
                } else {
                    gulfstreamGIVButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    selected.remove((String) gulfstreamGIVButton.getText());
                    gulfstreamGIV = false;
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

    private int findLargest() {
        Iterator<String> key = map.keySet().iterator();
        int largestValue = 0;
        while (key.hasNext()) {
            String keyValue = key.next();
            if(map.get(keyValue) > largestValue) {
                largestValue = map.get(keyValue);
            }
            totalAircraft += map.get(keyValue);
        }
        System.out.println("TOTAL : " + totalAircraft);
        System.out.println("LARGEST : " + largestValue);
        return largestValue;
    }

    private void drawGraph() {

        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        Set<String> aircraftsChosen = prefs.getStringSet("ChosenAircrafts", null);

        int highestValue = findLargest();

        switchLayout();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        for (String s : map.keySet()) {

            if (s.equalsIgnoreCase("Airbus A220")) {
                NeumorphButton button = findViewById(R.id.a220);
                TextView status = findViewById(R.id.a220status);
                status.setText("Airbus A220\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A220")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A300")) {
                NeumorphButton button = findViewById(R.id.a300);
                TextView status = findViewById(R.id.a300status);
                status.setText("Airbus A300\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A300")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A310")) {
                NeumorphButton button = findViewById(R.id.a310);
                TextView status = findViewById(R.id.a310status);
                status.setText("Airbus A310\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A310")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A318")) {
                NeumorphButton button = findViewById(R.id.a318);
                TextView status = findViewById(R.id.a318status);
                status.setText("Airbus A318\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A318")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A319")) {
                NeumorphButton button = findViewById(R.id.a319);
                TextView status = findViewById(R.id.a319status);
                status.setText("Airbus A319\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A319")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A320")) {
                NeumorphButton button = findViewById(R.id.a320);
                TextView status = findViewById(R.id.a320status);
                status.setText("Airbus A320\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A320")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A321")) {
                NeumorphButton button = findViewById(R.id.a321);
                TextView status = findViewById(R.id.a321status);
                status.setText("Airbus A321\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A321")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A319neo")) {
                NeumorphButton button = findViewById(R.id.a319neo);
                TextView status = findViewById(R.id.a319neostatus);
                status.setText("Airbus A319neo\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A319neo")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A320neo")) {
                NeumorphButton button = findViewById(R.id.a320neo);
                TextView status = findViewById(R.id.a320neostatus);
                status.setText("Airbus A320neo\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A320neo")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A321neo")) {
                NeumorphButton button = findViewById(R.id.a321neo);
                TextView status = findViewById(R.id.a321neostatus);
                status.setText("Airbus A321neo\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A321neo")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A330")) {
                NeumorphButton button = findViewById(R.id.a330);
                TextView status = findViewById(R.id.a330status);
                status.setText("Airbus A330\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A330")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A330neo")) {
                NeumorphButton button = findViewById(R.id.a330neo);
                TextView status = findViewById(R.id.a330neostatus);
                status.setText("Airbus A330neo\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A330neo")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A340")) {
                NeumorphButton button = findViewById(R.id.a340);
                TextView status = findViewById(R.id.a340status);
                status.setText("Airbus A340\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A340")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A350")) {
                NeumorphButton button = findViewById(R.id.a350);
                TextView status = findViewById(R.id.a350status);
                status.setText("Airbus A350\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A350")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus A380")) {
                NeumorphButton button = findViewById(R.id.a380);
                TextView status = findViewById(R.id.a380status);
                status.setText("Airbus A380\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus A380")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Airbus Beluga")) {
                NeumorphButton button = findViewById(R.id.beluga);
                TextView status = findViewById(R.id.belugastatus);
                status.setText("Airbus Beluga\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Airbus Beluga")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Antonov An-22")) {
                NeumorphButton button = findViewById(R.id.an22);
                TextView status = findViewById(R.id.an22status);
                status.setText("Antonov An-22\nAntei\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Antonov An-22")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Antonov An-72")) {
                NeumorphButton button = findViewById(R.id.an72);
                TextView status = findViewById(R.id.an72status);
                status.setText("Antonov An-72\nCheburashka\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Antonov An-72")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Antonov An-124")) {
                NeumorphButton button = findViewById(R.id.an124);
                TextView status = findViewById(R.id.an124status);
                status.setText("Antonov An-124\nRuslan\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Antonov An-124")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Antonov An-225")) {
                NeumorphButton button = findViewById(R.id.an225);
                TextView status = findViewById(R.id.an225status);
                status.setText("Antonov An-225\nMriya\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Antonov An-225")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Boeing 737")) {
                NeumorphButton button = findViewById(R.id.b737);
                TextView status = findViewById(R.id.b737status);
                status.setText("Boeing 737\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Boeing 737")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Boeing 747")) {
                NeumorphButton button = findViewById(R.id.b747);
                TextView status = findViewById(R.id.b747status);
                status.setText("Boeing 747\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Boeing 747")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Boeing 757")) {
                NeumorphButton button = findViewById(R.id.b757);
                TextView status = findViewById(R.id.b757status);
                status.setText("Boeing 757\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Boeing 757")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Boeing 767")) {
                NeumorphButton button = findViewById(R.id.b767);
                TextView status = findViewById(R.id.b767status);
                status.setText("Boeing 767\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Boeing 767")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Boeing 777")) {
                NeumorphButton button = findViewById(R.id.b777);
                TextView status = findViewById(R.id.b777status);
                status.setText("Boeing 777\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Boeing 777")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Boeing 787")) {
                NeumorphButton button = findViewById(R.id.b787);
                TextView status = findViewById(R.id.b787status);
                status.setText("Boeing 787\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Boeing 787")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Bombardier\nChallenger 650")) {
                NeumorphButton button = findViewById(R.id.bombardier_challenger_650);
                TextView status = findViewById(R.id.bombardier_challenger_650_status);
                status.setText("Bombardier\nChallenger 650\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Bombardier\nChallenger 650")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Bombardier\nCRJ 100/200")) {
                NeumorphButton button = findViewById(R.id.bombardier_crj);
                TextView status = findViewById(R.id.bombardier_crj_status);
                status.setText("Bombardier\nCRJ 100/200\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Bombardier\nCRJ 100/200")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Bombardier\nLearjet 75")) {
                NeumorphButton button = findViewById(R.id.bombardier_learjet_75);
                TextView status = findViewById(R.id.bombardier_learjet_75_status);
                status.setText("Bombardier\nLearjet 75\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Bombardier\nLearjet 75")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Bombardier\nGlobal 7500")) {
                NeumorphButton button = findViewById(R.id.bombardier_global_7500);
                TextView status = findViewById(R.id.bombardier_global_7500_status);
                status.setText("Bombardier\nGlobal 7500\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Bombardier\nGlobal 7500")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Embraer ERJ")) {
                NeumorphButton button = findViewById(R.id.embraer_erj);
                TextView status = findViewById(R.id.embraer_erj_status);
                status.setText("Embraer ERJ\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Embraer ERJ")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Embraer E-Jet\nE2")) {
                NeumorphButton button = findViewById(R.id.embraer_e_jet_e2);
                TextView status = findViewById(R.id.embraer_e_jet_e2_status);
                status.setText("Embraer E-Jet\nE2\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Embraer E-Jet\nE2")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Embraer Lineage\n1000")) {
                NeumorphButton button = findViewById(R.id.embraer_lineage);
                TextView status = findViewById(R.id.embraer_lineage_status);
                status.setText("Embraer Lineage\n1000\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Embraer Lineage\n1000")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Embraer Phenom\n300")) {
                NeumorphButton button = findViewById(R.id.embraer_phenom);
                TextView status = findViewById(R.id.embraer_phenom_status);
                status.setText("Embraer Phenom\n300\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Embraer Phenom\n300")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Cessna 182\nSkylane")) {
                NeumorphButton button = findViewById(R.id.cessna_skylane);
                TextView status = findViewById(R.id.cessna_skylane_status);
                status.setText("Cessna 182\nSkylane\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Cessna 182\nSkylane")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Cessna 208\nCaravan")) {
                NeumorphButton button = findViewById(R.id.cessna_caravan);
                TextView status = findViewById(R.id.cessna_caravan_status);
                status.setText("Cessna 208\nCaravan\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Cessna 208\nCaravan")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Cessna Latitude")) {
                NeumorphButton button = findViewById(R.id.cessna_latitude);
                TextView status = findViewById(R.id.cessna_latitude_status);
                status.setText("Cessna Latitude\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Cessna Latitude")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Cessna Longitude")) {
                NeumorphButton button = findViewById(R.id.cessna_longitude);
                TextView status = findViewById(R.id.cessna_longitude_status);
                status.setText("Cessna Longitude\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Cessna Longitude")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Gulfstream G650")) {
                NeumorphButton button = findViewById(R.id.gulfstream_g650);
                TextView status = findViewById(R.id.gulfstream_g650_status);
                status.setText("Gulfstream G650\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Gulfstream G650")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }
            else if (s.equalsIgnoreCase("Gulfstream G280")) {
                NeumorphButton button = findViewById(R.id.gulfstream_g280);
                TextView status = findViewById(R.id.gulfstream_g280_status);
                status.setText("Gulfstream G280\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Gulfstream G280")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }

        }

    }

}