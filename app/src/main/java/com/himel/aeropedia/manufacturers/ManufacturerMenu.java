package com.himel.aeropedia.manufacturers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.himel.aeropedia.Demo;
import com.himel.aeropedia.DemoMap;
import com.himel.aeropedia.FromMapbox;
import com.himel.aeropedia.R;
import com.himel.aeropedia.alexa.AlexaActivity;
import com.himel.aeropedia.treeview.IconTreeItemHolder;
import com.himel.aeropedia.treeview.TreeView;
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

    public final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        locale = Locale.getDefault();
        enableDarkOnCreate = verifyDarkMode();
        if(enableDark.equals("No")) {
            setContentView(R.layout.activity_manufacturer_menu_light);
        } else {
            setContentView(R.layout.activity_manufacturer_menu_dark);
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
            langToggle.setShapeType(ShapeType.BASIN);
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
                Intent showContent = new Intent(getApplicationContext(), TreeView.class);
                startActivity(showContent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        boeingCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent showContent = new Intent(getApplicationContext(), FromMapbox.class);
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

        if(!enableDarkOnCreate.equals(verifyDarkMode()) && !locale.equals(Locale.getDefault())) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            createTreeView();
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
            createTreeView();
        }

        scrollView = findViewById(R.id.main_scroll);
        scrollView.scrollTo(0, scrollView.getTop());
        animateCards();
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
            manufacturerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Manufacturers", "HighlightLight", "Manufacturers"));
        } else {
            manufacturerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Manufacturers", "HighlightDark", "Manufacturers"));
        }
        TreeNode amazonRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ic_amazon_alexa, "Amazon Alexa", "No", "Alexa"));
        TreeNode firebaseRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Firebase", "No", "firebase"));



        TreeNode airbus = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, this.getString(R.string.airbus), "No", "airbus"));
        TreeNode a220Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a220), "No", "a220"));
        TreeNode a319Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a319), "No", "a319"));
        TreeNode a320Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a320), "No", "a320"));
        TreeNode a321Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a321), "No", "a321"));

        TreeNode a330Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_alexa, this.getString(R.string.a330), "No", "a330"));
        TreeNode a340Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a340), "No", "a340"));
        TreeNode a350Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a350), "No", "a350"));
        TreeNode a380Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a380), "No", "a380"));


        airbus.addChildren(a220Node, a319Node, a320Node, a321Node, a330Node, a340Node, a350Node, a380Node);


        TreeNode boeing = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo_library, this.getString(R.string.boeing), "No", "boeing"));
        TreeNode b777 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "B777", "No", "b777"));
        TreeNode b787 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "B787", "No", "b787"));
        boeing.addChildren(b777, b787);

        manufacturerRoot.addChildren(airbus, boeing);


        root.addChildren(manufacturerRoot);
        root.addChildren(amazonRoot);
        root.addChildren(firebaseRoot);
        manufacturerRoot.setExpanded(true);


        tView = new AndroidTreeView(this, root);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);

        containerView.addView(tView.getView());

//        if (savedInstanceState != null) {
//            String state = savedInstanceState.getString("tState");
//            if (!TextUtils.isEmpty(state)) {
//                tView.restoreState(state);
//            }
//        }

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