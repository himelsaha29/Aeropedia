package com.himel.aeropedia.manufacturers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;

import com.google.firebase.analytics.FirebaseAnalytics;
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
import com.himel.aeropedia.airbus.AirbusA380;
import com.himel.aeropedia.airbus.AirbusBeluga;
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
import com.himel.aeropedia.R;
import com.himel.aeropedia.airbus.AirbusA350;
import com.himel.aeropedia.alexa.AlexaActivity;
import com.himel.aeropedia.flightmap.FlightMap;
import com.himel.aeropedia.gulfstream.G280;
import com.himel.aeropedia.gulfstream.G650;
import com.himel.aeropedia.gulfstream.GulfstreamIV;
import com.himel.aeropedia.treeview.IconTreeItemHolder;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingMenuLayout;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.Locale;

import io.alterac.blurkit.BlurLayout;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphImageButton;
import soup.neumorphism.ShapeType;

public class ManufacturerMenu extends AppCompatActivity {

    private CardView airbusCard;
    private CardView boeingCard;
    private CardView bombardierCard;
    private CardView antonovCard;
    private CardView embraerCard;
    private CardView cessnaCard;
    private CardView gulfstreamCard;
    private boolean flag = false;
    private Animation translate = null;
    private ScrollView scrollView;
    private Locale locale;
    private NeumorphButton langToggle;
    private NeumorphImageButton darkToggle;
    private String enableDark;
    private String enableDarkOnCreate;
    private FlowingDrawer mDrawer;
    private FlowingMenuLayout flowingMenuLayout;
    private BlurLayout blur;
    private AndroidTreeView tView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private int id = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        locale = Locale.getDefault();
        enableDarkOnCreate = verifyDarkMode();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        try {
            Bundle bundle = new Bundle();
            bundle.putString("gallery", "gallery");
            mFirebaseAnalytics.logEvent("gallery", bundle);
        } catch (Exception e) {}
        if(enableDark.equals("No")) {
            setContentView(R.layout.activity_manufacturer_menu_light);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#FF92B7D4"));
            }
        } else {
            setContentView(R.layout.activity_manufacturer_menu_dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#FF060525"));
            }
        }
        darkToggle = findViewById(R.id.dark_toggle);
        scrollView = findViewById(R.id.main_scroll);
        airbusCard = findViewById(R.id.airbusCard);
        boeingCard = findViewById(R.id.boeingCard);
        bombardierCard = findViewById(R.id.bombardierCard);
        embraerCard = findViewById(R.id.embraerCard);
        antonovCard = findViewById(R.id.antonovCard);
        cessnaCard = findViewById(R.id.cessnaCard);
        gulfstreamCard = findViewById(R.id.gulfstreamCard);
        langToggle = findViewById(R.id.lang_toggle);
        mDrawer = findViewById(R.id.drawerlayout);
        flowingMenuLayout = findViewById(R.id.menulayout);
        blur = findViewById(R.id.blurLayout);


        airbusCard.getBackground().setAlpha(65);
        boeingCard.getBackground().setAlpha(65);
        bombardierCard.getBackground().setAlpha(65);
        embraerCard.getBackground().setAlpha(65);
        antonovCard.getBackground().setAlpha(65);
        cessnaCard.getBackground().setAlpha(65);
        gulfstreamCard.getBackground().setAlpha(65);


        // setting NeumorphismButton shape based on state
        if (locale.toString().contains("en")) {
            langToggle.setShapeType(ShapeType.FLAT);
        } else if (locale.toString().contains("fr")) {
            langToggle.setShapeType(ShapeType.PRESSED);
        }

        animateCards();

        airbusCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Airbus.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        antonovCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent showContent = new Intent(getApplicationContext(), Antonov.class);
                startActivity(showContent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        boeingCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent showContent = new Intent(getApplicationContext(), Boeing.class);
                startActivity(showContent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        bombardierCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent showContent = new Intent(getApplicationContext(), Bombardier.class);
                startActivity(showContent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        embraerCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent showContent = new Intent(getApplicationContext(), Embraer.class);
                startActivity(showContent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        cessnaCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent showContent = new Intent(getApplicationContext(), Cessna.class);
                startActivity(showContent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        gulfstreamCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent showContent = new Intent(getApplicationContext(), Gulfstream.class);
                startActivity(showContent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                }
                else if (newState == ElasticDrawer.STATE_OPENING) {
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

        // resizing drawer
//        int width = (int) (getResources().getDisplayMetrics().widthPixels/1.42);
//        FlowingMenuLayout.LayoutParams params = (FlowingMenuLayout.LayoutParams) flowingMenuLayout.getLayoutParams();
//        params.width = width;
//        params.leftMargin = -10;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            params.setMarginStart(-10);
//        }
//        flowingMenuLayout.setLayoutParams(params);

        createTreeView();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            Bundle bundle = new Bundle();
            bundle.putString("return_to_gallery", String.valueOf(id));
            mFirebaseAnalytics.logEvent("return_to_gallery", bundle);
            id++;
        } catch(Exception e) {}

        if(!enableDarkOnCreate.equals(verifyDarkMode()) && !locale.equals(Locale.getDefault())) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            //createTreeView();
        }
        else if (!locale.equals(Locale.getDefault())) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        else if (!enableDarkOnCreate.equals(verifyDarkMode())) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            //createTreeView();
        }

        if(mDrawer.getDrawerState() == ElasticDrawer.STATE_OPEN) {
            mDrawer.closeMenu(false);
            blur.setVisibility(View.GONE);
            blur.setAlpha(0f);
        }

    }

    private void animateCards() {
        translate = AnimationUtils.loadAnimation(this, R.anim.animation);
        airbusCard.setAnimation(translate);
        boeingCard.setAnimation(translate);
        bombardierCard.setAnimation(translate);
        embraerCard.setAnimation(translate);
        antonovCard.setAnimation(translate);
        cessnaCard.setAnimation(translate);
        gulfstreamCard.setAnimation(translate);
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


    /** TreeView **/

    private void createTreeView() {

        ViewGroup containerView = (ViewGroup) findViewById(R.id.inside);


        TreeNode root = TreeNode.root();
        TreeNode manufacturerRoot = null;
        if (verifyDarkMode().equals("Yes")) {
            manufacturerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_manufacturer, this.getString(R.string.manufacturers), "HighlightLight", "manufacturers", null));
        } else {
            manufacturerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_manufacturer, this.getString(R.string.manufacturers), "HighlightDark", "manufacturers", null));
        }
        TreeNode alexaRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_alexa, this.getString(R.string.alexa_menu), "No", "Alexa", AlexaActivity.class));
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
        root.addChildren(alexaRoot);
        root.addChildren(flightTrackerRoot);
        root.addChildren(firebaseRoot);
        manufacturerRoot.setExpanded(true);

        tView = new AndroidTreeView(this, root);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);

        containerView.addView(tView.getView());

    }

    /** TreeView **/


    @Override
    public void onBackPressed() {
        if (mDrawer.getDrawerState() == ElasticDrawer.STATE_CLOSING || mDrawer.getDrawerState() == ElasticDrawer.STATE_OPEN) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
