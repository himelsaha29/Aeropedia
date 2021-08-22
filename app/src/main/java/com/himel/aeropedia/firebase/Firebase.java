package com.himel.aeropedia.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.himel.aeropedia.airbus.AirbusA220;
import com.himel.aeropedia.airbus.AirbusA300;
import com.himel.aeropedia.airbus.AirbusA310;
import com.himel.aeropedia.airbus.AirbusA318;
import com.himel.aeropedia.airbus.AirbusA319;
import com.himel.aeropedia.airbus.AirbusA320;
import com.himel.aeropedia.airbus.AirbusA320neoFamily;
import com.himel.aeropedia.airbus.AirbusA321;
import com.himel.aeropedia.airbus.AirbusA330;
import com.himel.aeropedia.airbus.AirbusA330neo;
import com.himel.aeropedia.airbus.AirbusA340;
import com.himel.aeropedia.airbus.AirbusA350;
import com.himel.aeropedia.airbus.AirbusA380;
import com.himel.aeropedia.airbus.AirbusBeluga;
import com.himel.aeropedia.alexa.AlexaActivity;
import com.himel.aeropedia.antonov.AntonovAn124Ruslan;
import com.himel.aeropedia.antonov.AntonovAn225Mriya;
import com.himel.aeropedia.antonov.AntonovAn22Antei;
import com.himel.aeropedia.antonov.AntonovAn72Cheburashka;
import com.himel.aeropedia.boeing.Boeing737;
import com.himel.aeropedia.boeing.Boeing747;
import com.himel.aeropedia.boeing.Boeing757;
import com.himel.aeropedia.boeing.Boeing767;
import com.himel.aeropedia.boeing.Boeing777;
import com.himel.aeropedia.boeing.Boeing787;
import com.himel.aeropedia.bombardier.CRJ100200;
import com.himel.aeropedia.bombardier.Challenger650;
import com.himel.aeropedia.bombardier.Global7500;
import com.himel.aeropedia.bombardier.Learjet75;
import com.himel.aeropedia.cessna.Caravan;
import com.himel.aeropedia.cessna.CitationLatitude;
import com.himel.aeropedia.cessna.CitationLongitude;
import com.himel.aeropedia.cessna.Skylane;
import com.himel.aeropedia.embraer.EJetE2;
import com.himel.aeropedia.embraer.ERJFamily;
import com.himel.aeropedia.embraer.Lineage1000;
import com.himel.aeropedia.embraer.Phenom300;
import com.himel.aeropedia.flightmap.FlightMap;
import com.himel.aeropedia.gulfstream.G280;
import com.himel.aeropedia.gulfstream.G650;
import com.himel.aeropedia.gulfstream.GulfstreamIV;
import com.himel.aeropedia.treeview.IconTreeItemHolder;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.alterac.blurkit.BlurLayout;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageButton;
import soup.neumorphism.ShapeType;

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

    private FlowingDrawer mDrawer;
    private BlurLayout blur;
    private AndroidTreeView tView;
    private String enableDark;
    private String enableDarkOnCreate;
    private NeumorphButton langToggle;
    private Locale locale;
    private NeumorphImageButton darkToggle;


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
        loadLocale();
        locale = Locale.getDefault();
        enableDarkOnCreate = verifyDarkMode();
        if(enableDark.equals("No")) {
            setContentView(R.layout.activity_firebase_light);
        } else {
            setContentView(R.layout.activity_firebase_dark);
        }

        langToggle = findViewById(R.id.lang_toggle);
        darkToggle = findViewById(R.id.dark_toggle);
        mDrawer = findViewById(R.id.drawerlayout);
        blur = findViewById(R.id.blurLayout);

        // setting NeumorphismButton shape based on state
        if (locale.toString().contains("en")) {
            langToggle.setShapeType(ShapeType.FLAT);
        } else if (locale.toString().contains("fr")) {
            langToggle.setShapeType(ShapeType.PRESSED);
        }

        langToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(getResources().getConfiguration().locale.toString().contains("fr")) {
                    setLocale("en");
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else if (getResources().getConfiguration().toString().contains("en")) {
                    setLocale("fr");
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        });

        darkToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                if (prefs.getString("DarkMode", "").equals("Yes")) {
                    toggleDark("No");
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else if (prefs.getString("DarkMode", "").equals("No")) {
                    toggleDark("Yes");
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

            }
        });

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
        drawer();

        textCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switchLayout();
                drawer();
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
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a220Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a220Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a220Button.getText());
                    a220 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a220Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a220Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a220Button.getText());
                    a220 = false;
                }
            }
        });

        a300Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a300) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a300Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a300Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a300Button.getText());
                    a300 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a300Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a300Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a300Button.getText());
                    a300 = false;
                }
            }
        });

        a310Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a310) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a310Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a310Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a310Button.getText());
                    a310 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a310Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a310Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a310Button.getText());
                    a310 = false;
                }
            }
        });

        a318Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a318) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a318Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a318Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a318Button.getText());
                    a318 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a318Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a318Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a318Button.getText());
                    a318 = false;
                }
            }
        });

        a319Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a319) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a319Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a319Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a319Button.getText());
                    a319 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a319Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a319Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a319Button.getText());
                    a319 = false;
                }
            }
        });

        a320Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a320) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a320Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a320Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a320Button.getText());
                    a320 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a320Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a320Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a320Button.getText());
                    a320 = false;
                }
            }
        });

        a321Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a321) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a321Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a321Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a321Button.getText());
                    a321 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a321Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a321Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a321Button.getText());
                    a321 = false;
                }
            }
        });

        a319neoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a319neo) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a319neoButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a319neoButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a319neoButton.getText());
                    a319neo = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a319neoButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a319neoButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a319neoButton.getText());
                    a319neo = false;
                }
            }
        });

        a320neoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a320neo) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a320neoButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a320neoButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a320neoButton.getText());
                    a320neo = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a320neoButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a320neoButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a320neoButton.getText());
                    a320neo = false;
                }
            }
        });

        a321neoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a321neo) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a321neoButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a321neoButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a321neoButton.getText());
                    a321neo = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a321neoButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a321neoButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a321neoButton.getText());
                    a321neo = false;
                }
            }
        });

        a330Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a330) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a330Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a330Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a330Button.getText());
                    a330 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a330Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a330Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a330Button.getText());
                    a330 = false;
                }
            }
        });

        a330neoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a330neo) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a330neoButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a330neoButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a330neoButton.getText());
                    a330neo = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a330neoButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a330neoButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a330neoButton.getText());
                    a330neo = false;
                }
            }
        });

        a340Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a340) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a340Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a340Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a340Button.getText());
                    a340 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a340Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a340Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a340Button.getText());
                    a340 = false;
                }
            }
        });

        a350Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a350) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a350Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a350Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a350Button.getText());
                    a350 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a350Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a350Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a350Button.getText());
                    a350 = false;
                }
            }
        });

        a380Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!a380) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a380Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        a380Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) a380Button.getText());
                    a380 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        a380Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        a380Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) a380Button.getText());
                    a380 = false;
                }
            }
        });

        belugaButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!beluga) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        belugaButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        belugaButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) belugaButton.getText());
                    beluga = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        belugaButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        belugaButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) belugaButton.getText());
                    beluga = false;
                }
            }
        });

        an22Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!an22) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        an22Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        an22Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) an22Button.getText());
                    an22 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        an22Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        an22Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) an22Button.getText());
                    an22 = false;
                }
            }
        });

        an72Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!an72) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        an72Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        an72Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) an72Button.getText());
                    an72 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        an72Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        an72Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) an72Button.getText());
                    an72 = false;
                }
            }
        });

        an124Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!an124) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        an124Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        an124Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) an124Button.getText());
                    an124 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        an124Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        an124Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) an124Button.getText());
                    an124 = false;
                }
            }
        });

        an225Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!an225) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        an225Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        an225Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) an225Button.getText());
                    an225 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        an225Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        an225Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) an225Button.getText());
                    an225 = false;
                }
            }
        });

        b737Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b737) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b737Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        b737Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) b737Button.getText());
                    b737 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b737Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        b737Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) b737Button.getText());
                    b737 = false;
                }
            }
        });

        b747Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b747) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b747Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        b747Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) b747Button.getText());
                    b747 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b747Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        b747Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) b747Button.getText());
                    b747 = false;
                }
            }
        });

        b757Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b757) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b757Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        b757Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) b757Button.getText());
                    b757 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b757Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        b757Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) b757Button.getText());
                    b757 = false;
                }
            }
        });

        b767Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b767) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b767Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        b767Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) b767Button.getText());
                    b767 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b767Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        b767Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) b767Button.getText());
                    b767 = false;
                }
            }
        });

        b777Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b777) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b777Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        b777Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) b777Button.getText());
                    b777 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b777Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        b777Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) b777Button.getText());
                    b777 = false;
                }
            }
        });

        b787Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!b787) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b787Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        b787Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) b787Button.getText());
                    b787 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        b787Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        b787Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) b787Button.getText());
                    b787 = false;
                }
            }
        });

        bombardierChallenger650Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!bombardierChallenger650) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        bombardierChallenger650Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        bombardierChallenger650Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) bombardierChallenger650Button.getText());
                    bombardierChallenger650 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        bombardierChallenger650Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        bombardierChallenger650Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) bombardierChallenger650Button.getText());
                    bombardierChallenger650 = false;
                }
            }
        });

        bombardierCRJButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!bombardierCRJ) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        bombardierCRJButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        bombardierCRJButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) bombardierCRJButton.getText());
                    bombardierCRJ = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        bombardierCRJButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        bombardierCRJButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) bombardierCRJButton.getText());
                    bombardierCRJ = false;
                }
            }
        });

        bombardierLearjetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!bombardierLearjet) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        bombardierLearjetButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        bombardierLearjetButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) bombardierLearjetButton.getText());
                    bombardierLearjet = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        bombardierLearjetButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        bombardierLearjetButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) bombardierLearjetButton.getText());
                    bombardierLearjet = false;
                }
            }
        });

        bombardierGlobalButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!bombardierGlobal) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        bombardierGlobalButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        bombardierGlobalButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) bombardierGlobalButton.getText());
                    bombardierGlobal = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        bombardierGlobalButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        bombardierGlobalButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) bombardierGlobalButton.getText());
                    bombardierGlobal = false;
                }
            }
        });

        embraerERJButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!embraerERJ) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        embraerERJButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        embraerERJButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) embraerERJButton.getText());
                    embraerERJ = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        embraerERJButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        embraerERJButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) embraerERJButton.getText());
                    embraerERJ = false;
                }
            }
        });

        embraerEJetE2Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!embraerEJetE2) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        embraerEJetE2Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        embraerEJetE2Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) embraerEJetE2Button.getText());
                    embraerEJetE2 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        embraerEJetE2Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        embraerEJetE2Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) embraerEJetE2Button.getText());
                    embraerEJetE2 = false;
                }
            }
        });

        embraerLineageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!embraerLineage) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        embraerLineageButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        embraerLineageButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) embraerLineageButton.getText());
                    embraerLineage = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        embraerLineageButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        embraerLineageButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) embraerLineageButton.getText());
                    embraerLineage = false;
                }
            }
        });

        embraerPhenomButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!embraerPhenom) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        embraerPhenomButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        embraerPhenomButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) embraerPhenomButton.getText());
                    embraerPhenom = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        embraerPhenomButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        embraerPhenomButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) embraerPhenomButton.getText());
                    embraerPhenom = false;
                }
            }
        });

        cessnaSkylaneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cessnaSkylane) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        cessnaSkylaneButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        cessnaSkylaneButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) cessnaSkylaneButton.getText());
                    cessnaSkylane = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        cessnaSkylaneButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        cessnaSkylaneButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) cessnaSkylaneButton.getText());
                    cessnaSkylane = false;
                }
            }
        });

        cessnaCaravanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cessnaCaravan) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        cessnaCaravanButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        cessnaCaravanButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) cessnaCaravanButton.getText());
                    cessnaCaravan = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        cessnaCaravanButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        cessnaCaravanButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) cessnaCaravanButton.getText());
                    cessnaCaravan = false;
                }
            }
        });

        cessnaLatitudeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cessnaLatitude) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        cessnaLatitudeButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        cessnaLatitudeButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) cessnaLatitudeButton.getText());
                    cessnaLatitude = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        cessnaLatitudeButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        cessnaLatitudeButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) cessnaLatitudeButton.getText());
                    cessnaLatitude = false;
                }
            }
        });

        cessnaLongitudeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cessnaLongitude) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        cessnaLongitudeButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        cessnaLongitudeButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) cessnaLongitudeButton.getText());
                    cessnaLongitude = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        cessnaLongitudeButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        cessnaLongitudeButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) cessnaLongitudeButton.getText());
                    cessnaLongitude = false;
                }
            }
        });

        gulfstreamG650Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!gulfstreamG650) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        gulfstreamG650Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        gulfstreamG650Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) gulfstreamG650Button.getText());
                    gulfstreamG650 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        gulfstreamG650Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        gulfstreamG650Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) gulfstreamG650Button.getText());
                    gulfstreamG650 = false;
                }
            }
        });

        gulfstreamG280Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!gulfstreamG280) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        gulfstreamG280Button.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        gulfstreamG280Button.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) gulfstreamG280Button.getText());
                    gulfstreamG280 = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        gulfstreamG280Button.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        gulfstreamG280Button.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) gulfstreamG280Button.getText());
                    gulfstreamG280 = false;
                }
            }
        });

        gulfstreamGIVButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!gulfstreamGIV) {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        gulfstreamGIVButton.setBackgroundColor(Color.parseColor("#092c51"));
                    } else {
                        gulfstreamGIVButton.setBackgroundColor(Color.parseColor("#b6d8fc"));
                    }
                    selected.add((String) gulfstreamGIVButton.getText());
                    gulfstreamGIV = true;
                } else {
                    if (enableDarkOnCreate.equalsIgnoreCase("Yes")) {
                        gulfstreamGIVButton.setBackgroundColor(Color.parseColor("#141635"));
                    } else {
                        gulfstreamGIVButton.setBackgroundColor(Color.parseColor("#f2f4f6"));
                    }
                    selected.remove((String) gulfstreamGIVButton.getText());
                    gulfstreamGIV = false;
                }
            }
        });
    }

    private void switchLayout() {
        setContentView(R.layout.activity_firebase_results_dark);
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
        int x = 0;
        while (key.hasNext()) {
            String keyValue = key.next();
            if(map.get(keyValue) > largestValue) {
                largestValue = map.get(keyValue);
            }
            totalAircraft += map.get(keyValue);
            x++;
        }
        System.out.println("TOTAL : " + totalAircraft + " = " + x);
        System.out.println("LARGEST : " + largestValue);
        return largestValue;
    }

    private void drawGraph() {

        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        Set<String> aircraftsChosen = prefs.getStringSet("ChosenAircrafts", null);

        int highestValue = findLargest();

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
            else if (s.equalsIgnoreCase("Gulfstream G-IV")) {
                NeumorphButton button = findViewById(R.id.gulfstream_giv);
                TextView status = findViewById(R.id.gulfstream_giv_status);
                status.setText("Gulfstream G-IV\n" + df.format(((float) map.get(s) / (float) totalAircraft) * 100) + "%");
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                float size = (float) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                if(size <= 0 || size <= 100) {
                    layoutParams.height = 100;
                } else {
                    layoutParams.height = (int) (((float) map.get(s) / (float) highestValue) * 0.60 * height);
                }
                button.setLayoutParams(layoutParams);
                if(aircraftsChosen.contains("Gulfstream G-IV")) {
                    button.setBackgroundColor(Color.parseColor("#fcb6b6"));
                }
            }

        }

    }


    private void drawer() {
        Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mDrawer.setMenuSize((int)(display.getWidth()*0.635));

        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_FULLSCREEN);
        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSING) {
                    Log.i("MainActivity", "Drawer STATE_CLOSED");
                    blur.setVisibility(View.GONE);
                    blur.setAlpha(0f);
                } else if (newState == ElasticDrawer.STATE_OPENING) {
                    blur.invalidate();
                    blur.setAlpha(0.0f);
                    blur.setVisibility(View.VISIBLE);
                    blur.animate().alpha(1.0f).setDuration(1400);
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
            }
        });

        createTreeView();
    }

    /** TreeView **/

    private void createTreeView() {

        ViewGroup containerView = (ViewGroup) findViewById(R.id.inside);


        TreeNode root = TreeNode.root();
        TreeNode manufacturerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_manufacturer, this.getString(R.string.manufacturers), "No", "Manufacturers", null));
        TreeNode alexaRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_alexa, this.getString(R.string.ask_alexa), "No", "Alexa", AlexaActivity.class));
        TreeNode flightTrackerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_radar, this.getString(R.string.flight_tracker), "No", "flightTracker", FlightMap.class));
        TreeNode firebaseRoot = null;
        if (verifyDarkMode().equals("Yes")) {
            firebaseRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_firebase, this.getString(R.string.firebase), "HighlightLight", "firebase", null));
        } else {
            firebaseRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_firebase, this.getString(R.string.firebase), "HighlightDark", "firebase", null));
        }


        TreeNode airbus = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.airbus), "No", "airbus", null));
        TreeNode a220Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a220), "No", "a220", AirbusA220.class));
        TreeNode a300Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a300), "No", "a300", AirbusA300.class));
        TreeNode a310Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a310), "No", "a310", AirbusA310.class));
        TreeNode a318Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a318), "No", "a318", AirbusA318.class));
        TreeNode a319Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a319), "No", "a319", AirbusA319.class));
        TreeNode a320Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a320), "No", "a320", AirbusA320.class));
        TreeNode a321Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a321), "No", "a321", AirbusA321.class));
        TreeNode a320neoNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a320neofamily), "No", "a320neofamily", AirbusA320neoFamily.class));
        TreeNode a330Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a330), "No", "a330", AirbusA330.class));
        TreeNode a330neoNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a330neo), "No", "a330neo", AirbusA330neo.class));
        TreeNode a340Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a340), "No", "a340", AirbusA340.class));
        TreeNode a350Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a350), "No", "a350", AirbusA350.class));
        TreeNode a380Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a380), "No", "a380", AirbusA380.class));
        TreeNode belugaNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.beluga), "No", "beluga", AirbusBeluga.class));

        airbus.addChildren(a220Node, a300Node, a310Node, a318Node, a319Node, a320Node, a321Node, a320neoNode, a330Node, a330neoNode, a340Node, a350Node, a380Node, belugaNode);

        TreeNode antonov = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.antonov), "No", "antonov", null));

        TreeNode an124Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.an124), "No", "an124", AntonovAn124Ruslan.class));
        TreeNode an72Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.an72), "No", "an72", AntonovAn72Cheburashka.class));
        TreeNode an22Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.an22), "No", "an22", AntonovAn22Antei.class));
        TreeNode an225Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.an225), "No", "an22", AntonovAn225Mriya.class));


        antonov.addChildren(an22Node, an72Node, an124Node, an225Node);
        TreeNode boeing = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.boeing), "No", "boeing", null));
        TreeNode b777 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, "777", "No", "b777", Boeing777.class));
        TreeNode b787 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, "787", "No", "b787", Boeing787.class));
        TreeNode b737 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b737), "No", "b737", Boeing737.class));
        TreeNode b757 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b757), "No", "b757", Boeing757.class));
        TreeNode b747 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b747), "No", "b747", Boeing747.class));
        TreeNode b767 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b767), "No", "b767", Boeing767.class));
        boeing.addChildren(b737, b747, b757, b767, b777, b787);
        TreeNode bombardier = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.bombardier), "No", "bombardier", null));
        TreeNode learjet75 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.learjet75), "No", "b767", Learjet75.class));
        TreeNode challenger650 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.challenger650), "No", "challenger650", Challenger650.class));
        TreeNode global7500 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.global7500), "No", "global7500", Global7500.class));
        TreeNode crj100200 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.crj_100_200), "No", "crj100200", CRJ100200.class));

        bombardier.addChildren(challenger650, crj100200, learjet75, global7500);
        TreeNode embraer = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.embraer), "No", "embraer", null));
        TreeNode erjFamily = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.erj_family), "No", "erjfamily", ERJFamily.class));

        TreeNode ejete2Family = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.ejet_e2_family), "No", "ejete2Family", EJetE2.class));
        TreeNode lineage1000 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.lineage1000), "No", "lineage1000", Lineage1000.class));
        TreeNode phenom300 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.phenom300), "No", "phenom300", Phenom300.class));

        embraer.addChildren(erjFamily, ejete2Family, lineage1000, phenom300);

        TreeNode cessna = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.cessna), "No", "cessna", null));
        TreeNode latitude = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.citation_latitude), "No", "latitude", CitationLatitude.class));
        TreeNode longitude = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.citation_longitude), "No", "longitude", CitationLongitude.class));
        TreeNode caravan = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.caravan), "No", "caravan", Caravan.class));
        TreeNode skylane = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.skylane), "No", "skylane", Skylane.class));

        cessna.addChildren(skylane, caravan, latitude, longitude);

        TreeNode gulfstream = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.gulfstream), "No", "gulfstream", null));
        TreeNode g280 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.g280), "No", "g280", G280.class));
        TreeNode gulfstreamIV = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.gulfstream_iv), "No", "gulfstreamIV", GulfstreamIV.class));
        TreeNode g650 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.g650), "No", "g650", G650.class));
        gulfstream.addChildren(g280, g650, gulfstreamIV);

        manufacturerRoot.addChildren(airbus, antonov, boeing, bombardier, cessna, embraer, gulfstream);

        root.addChildren(manufacturerRoot);
        root.addChildren(alexaRoot);
        root.addChildren(flightTrackerRoot);
        root.addChildren(firebaseRoot);

        tView = new AndroidTreeView(this, root);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);

        containerView.addView(tView.getView());

    }

    /** TreeView **/

    /** Dark mode **/

    private void toggleDark(String darkEnabled) {
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("DarkMode", darkEnabled);
        editor.apply();
    }

    private String verifyDarkMode() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        if (prefs.getString("DarkMode", "").equals("")) {

            SharedPreferences.Editor editor = prefs.edit();
            int currentNightMode = getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;

            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    editor.putString("DarkMode", "No");
                    editor.apply();
                    break;
                default:
                    editor.putString("DarkMode", "Yes");
                    editor.apply();
                    break;

            }
        }
        enableDark = prefs.getString("DarkMode", "Yes");

        return enableDark;
    }

    /** Dark mode **/

    /** Changing app language **/

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("Language", language);
        editor.apply();
    }

    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        if (prefs.getString("Language", getResources().getConfiguration().locale.toString().substring(0, 2)).equals("")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Language", getResources().getConfiguration().locale.toString().substring(0, 2));
            editor.apply();
        }
        String language = prefs.getString("Language", getResources().getConfiguration().locale.toString().substring(0, 2));
        setLocale(language);
    }

    /** Changing app language **/

}