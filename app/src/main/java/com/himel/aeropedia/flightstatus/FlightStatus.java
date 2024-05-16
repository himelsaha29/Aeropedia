package com.himel.aeropedia.flightstatus;

import static android.view.HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
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
import com.himel.aeropedia.boeing.BoeingGlobemaster;
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
import com.himel.aeropedia.firebase.Firebase;
import com.himel.aeropedia.flightmap.FlightMap;
import com.himel.aeropedia.gulfstream.G280;
import com.himel.aeropedia.gulfstream.G650;
import com.himel.aeropedia.gulfstream.GulfstreamIV;
import com.himel.aeropedia.treeview.IconTreeItemHolder;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import io.alterac.blurkit.BlurLayout;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphImageButton;
import soup.neumorphism.ShapeType;

public class FlightStatus extends AppCompatActivity {

    private RetrieveFlightStatus flightStatus = new RetrieveFlightStatus();
    private TextView departureTime;
    private TextView arrivalTime;
    private TextView fromIATA;
    private TextView toIATA;
    private TextView fromCity;
    private TextView toCity;
    private TextView timeElapsed;
    private TextView duration;
    private TextView timeRemaining;
    private TextView departureTerminalGate;
    private TextView arrivalTerminalGate;
    private EditText input;
    private NeumorphButton submit;
    private CoordinatorLayout mainLayout;
    private boolean isNetworkAvailable = false;
    private FlowingDrawer mDrawer;
    private BlurLayout blur;
    private AndroidTreeView tView;
    private String enableDark;
    private String enableDarkOnCreate;
    private Locale locale;
    private NeumorphButton langToggle;
    private NeumorphImageButton darkToggle;
    private JSONObject airportCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        locale = Locale.getDefault();
        enableDarkOnCreate = verifyDarkMode();
        if (enableDark.equals("No")) {
            setContentView(R.layout.activity_flight_status_light);
            lightStatus();
        } else {
            setContentView(R.layout.activity_flight_status_dark);
            darkStatus();
        }
        languageDarkToggle();
        loadDrawer();

        departureTime = findViewById(R.id.departureTime);
        arrivalTime = findViewById(R.id.arrivalTime);
        fromIATA = findViewById(R.id.fromIATA);
        toIATA = findViewById(R.id.toIATA);
        fromCity = findViewById(R.id.fromCity);
        toCity = findViewById(R.id.toCity);
        timeElapsed = findViewById(R.id.timeElapsed);
        duration = findViewById(R.id.duration);
        timeRemaining = findViewById(R.id.timeRemaining);
        departureTerminalGate = findViewById(R.id.departureTerminalGate);
        arrivalTerminalGate = findViewById(R.id.arrivalTerminalGate);
        input = findViewById(R.id.input);
        submit = findViewById(R.id.save);
        mainLayout = findViewById(R.id.mainLayout);



        loadCitiesJSON();

        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        if (!prefs.getString("FlightNumber", "").equals("")) {
            callGetStatus(false);
        }

        isNetworkAvailable = isNetworkAvailable();
        showSnackBar(isNetworkAvailable);

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isNetworkAvailable = isNetworkAvailable();
                showSnackBar(isNetworkAvailable);
                if(input.getText() != null && !input.getText().toString().trim().equalsIgnoreCase("")
                && isNetworkAvailable) {
                    callGetStatus(true);
                }

            }
        });

        langToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().locale.toString().contains("fr")) {
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
    }


    private void callGetStatus(boolean store) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String [][]response;
                if(store) {
                    response = flightStatus.getStatus(input.getText().toString());
                } else {
                    SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                    response = flightStatus.getStatus(prefs.getString("FlightNumber", ""));
                    FlightStatus.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            input.setText(prefs.getString("FlightNumber", ""));
                        }
                    });
                }

                if (response != null && response.length != 0 && response[0].length != 0 &&
                        response[1].length != 0 && response[2].length != 0) {
                    String[] departure = response[0];
                    String[] arrival = response[1];
                    String[] time = response[2];

                    FlightStatus.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            departureTime.setText(checkNull(departure[2]));
                            arrivalTime.setText(checkNull(arrival[2]));
                            fromIATA.setText(checkNull(departure[0]));
                            toIATA.setText(checkNull(arrival[0]));

                            if (!checkNull(departure[1]).equalsIgnoreCase("N/A")
                                    && getResources().getConfiguration().locale.toString().contains("fr")) {
                                try {
                                    fromCity.setText(airportCities.getString(fromIATA.getText().toString()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    fromCity.setText(departure[1]);
                                }
                            } else if(!checkNull(departure[1]).equalsIgnoreCase("N/A")) {
                                fromCity.setText(departure[1]);
                            }

                            if (!checkNull(arrival[1]).equalsIgnoreCase("N/A")
                            && getResources().getConfiguration().locale.toString().contains("fr")) {
                                try {
                                    toCity.setText(airportCities.getString(toIATA.getText().toString()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    toCity.setText(arrival[1]);
                                }
                            } else if(!checkNull(arrival[1]).equalsIgnoreCase("N/A")) {
                                toCity.setText(arrival[1]);
                            }


                            if (!checkNull(time[0]).equalsIgnoreCase("N/A")) {
                                timeElapsed.setText(getString(R.string.airborneFor) + " " + time[0]);
                            }
                            duration.setText(checkNull(time[1]));
                            if (!checkNull(time[1]).equalsIgnoreCase("N/A")) {
                                if(getResources().getConfiguration().locale.toString().contains("fr")) {
                                    timeRemaining.setText(getString(R.string.toGo) + " " + time[2]);
                                } else {
                                    timeRemaining.setText(time[2] + " " + getString(R.string.toGo));
                                }
                            }



                            StringBuilder departureInfo = new StringBuilder();
                            if(!checkNull(departure[3]).equalsIgnoreCase("N/A")){
                                departureInfo.append(getString(R.string.terminal) + " " + departure[3]);
                            }
                            if((departureInfo.toString().contains("Terminal") || departureInfo.toString().contains("Aérogare")) &&
                                    !checkNull(departure[4]).equalsIgnoreCase("N/A")){
                                departureInfo.append(" | " + getString(R.string.gate) + " " + departure[4]);
                            } else if(!(departureInfo.toString().contains("Terminal") || departureInfo.toString().contains("Aérogare")) &&
                                    !checkNull(departure[4]).equalsIgnoreCase("N/A")){
                                departureInfo.append(getString(R.string.gate) + " " + departure[4]);
                            }
                            departureTerminalGate.setText(departureInfo.toString());



                            StringBuilder arrivalInfo = new StringBuilder();
                            if(!checkNull(arrival[3]).equalsIgnoreCase("N/A")){
                                arrivalInfo.append(getString(R.string.terminal) + " " + arrival[3]);
                            }
                            if((arrivalInfo.toString().contains("Terminal") || arrivalInfo.toString().contains("Aérogare"))
                                    && !checkNull(arrival[4]).equalsIgnoreCase("N/A")){
                                arrivalInfo.append(" | " + getString(R.string.gate) + " " + arrival[4]);
                            } else if(!(arrivalInfo.toString().contains("Terminal") || arrivalInfo.toString().contains("Aérogare")) &&
                                    !checkNull(arrival[4]).equalsIgnoreCase("N/A")){
                                arrivalInfo.append(getString(R.string.gate) + " " + arrival[4]);
                            }
                            arrivalTerminalGate.setText(arrivalInfo.toString());

                        }
                    });

                    // store flight number
                    if(store) {
                        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                        editor.putString("FlightNumber", input.getText().toString());
                        editor.apply();
                    }
                }
                else {
                    FlightStatus.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FlightStatus.this, R.string.validFlightNumber, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private String checkNull(String s) {
        if(s == null || s.trim().length() == 0) {
            return "N/A";
        }
        return s;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void showSnackBar(boolean isNetworkAvailable) {
        if (!isNetworkAvailable) {
            System.out.println("Network not available");
            Snackbar snackbar = Snackbar
                    .make(mainLayout, R.string.snackbar, Snackbar.LENGTH_LONG);
            if (enableDark.equals("No")) {
                snackbar.setBackgroundTint(Color.parseColor("#72A8E1"));
                snackbar.setTextColor(Color.BLACK);
            } else {
                snackbar.setBackgroundTint(Color.parseColor("#1b1f1f"));
                snackbar.setTextColor(Color.WHITE);
            }
            snackbar.show();
        }
    }

    private void loadDrawer() {
        mDrawer = findViewById(R.id.drawerlayout);
        blur = findViewById(R.id.blurLayout);

        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_FULLSCREEN);
        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSING) {
                    Log.i("MainActivity", "Drawer STATE_CLOSED");
                    blur.setVisibility(View.GONE);
                    blur.setAlpha(0f);
                } else if (newState == ElasticDrawer.STATE_OPENING) {
                    try {
                        mainLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, FLAG_IGNORE_VIEW_SETTING);
                    } catch (Exception e) {
                        System.out.println("Haptic error" + e.getMessage());
                    }
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

    /**
     * TreeView
     **/

    private void createTreeView() {

        ViewGroup containerView = (ViewGroup) findViewById(R.id.inside);


        TreeNode root = TreeNode.root();
        TreeNode manufacturerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_manufacturer, this.getString(R.string.manufacturers), "No", "manufacturers", null));
        TreeNode alexaRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_alexa, this.getString(R.string.ask_alexa), "No", "ask_alexa", AlexaActivity.class));
        TreeNode flightStatusRoot = null;
        if (verifyDarkMode().equals("Yes")) {
            flightStatusRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_status, this.getString(R.string.flightStatus), "HighlightLight", "flightStatus", null));
        } else {
            flightStatusRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_status, this.getString(R.string.flightStatus), "HighlightDark", "flightStatus", null));
        }
        TreeNode flightTrackerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_radar, this.getString(R.string.flight_tracker), "No", "flightTracker", FlightMap.class));
        TreeNode firebaseRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_firebase, this.getString(R.string.firebase), "No", "firebase", Firebase.class));


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
        TreeNode globemaster = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, "C-17 Globemaster III", "No", "globemaster", BoeingGlobemaster.class));
        boeing.addChildren(b737, b747, b757, b767, b777, b787, globemaster);

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
        root.addChildren(flightTrackerRoot);
        root.addChildren(flightStatusRoot);
        root.addChildren(alexaRoot);
        root.addChildren(firebaseRoot);

        tView = new AndroidTreeView(this, root);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);

        containerView.addView(tView.getView());

    }

    private void darkStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ff141635"));
        }
    }

    private void lightStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#E6f2f4f6"));
        }
    }

    /**
     * Dark mode
     **/

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

    /**
     * Dark mode
     **/

    private void languageDarkToggle() {

        darkToggle = findViewById(R.id.dark_toggle);
        langToggle = findViewById(R.id.lang_toggle);
        // setting NeumorphismButton shape based on state
        if (locale.toString().contains("en")) {
            langToggle.setShapeType(ShapeType.FLAT);
        } else if (locale.toString().contains("fr")) {
            langToggle.setShapeType(ShapeType.PRESSED);
        }

        langToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().locale.toString().contains("fr")) {
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
    }

    /**
     * Changing app language
     **/

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


    private String loadCitiesFrenchJSONFromAsset(Context context) {
        String json = null;
        InputStream is = null;
        try {
            is = context.getAssets().open("AirportCities_fr.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void loadCitiesJSON() {
        try {
            airportCities = new JSONObject(loadCitiesFrenchJSONFromAsset(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}