package com.himel.aeropedia.boeing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.CollapsingToolbarLayout;
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
import com.himel.aeropedia.bombardier.Challenger650;
import com.himel.aeropedia.bombardier.Learjet75;
import com.himel.aeropedia.firebase.Firebase;
import com.himel.aeropedia.flightmap.FlightMap;
import com.himel.aeropedia.treeview.IconTreeItemHolder;
import com.himel.aeropedia.util.SliderAdapter;
import com.himel.aeropedia.util.SliderItem;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.alterac.blurkit.BlurLayout;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphImageButton;
import soup.neumorphism.ShapeType;

public class Boeing787 extends AppCompatActivity {

    private NeumorphButton langToggle;
    private Locale locale;
    private NeumorphImageButton darkToggle;
    private String enableDark;
    private String enableDarkOnCreate;
    private FlowingDrawer mDrawer;
    private BlurLayout blur;
    private AndroidTreeView tView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private SliderView sliderView;
    private SliderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        locale = Locale.getDefault();
        enableDarkOnCreate = verifyDarkMode();
        if(enableDark.equals("No")) {
            setContentView(R.layout.activity_boeing_787_light);
        } else {
            setContentView(R.layout.activity_boeing_787_dark);
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


        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/maven_pro_medium.ttf");
        collapsingToolbarLayout = findViewById(R.id.collapsing_bar);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);

        slideView();


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


        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
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


    @Override
    protected void onRestart() {
        super.onRestart();

        if(!enableDarkOnCreate.equals(verifyDarkMode()) && !locale.equals(Locale.getDefault())) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
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
        }

        if(mDrawer.getDrawerState() == ElasticDrawer.STATE_OPEN) {
            mDrawer.closeMenu(false);
            blur.setVisibility(View.GONE);
            blur.setAlpha(0f);
        }
    }


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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

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
        TreeNode manufacturerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_manufacturer, this.getString(R.string.manufacturers), "No", "Manufacturers", null));
        TreeNode alexaRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_alexa, this.getString(R.string.ask_alexa), "No", "Alexa", AlexaActivity.class));
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
        TreeNode an225Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.an225), "No", "an225", AntonovAn225Mriya.class));

        antonov.addChildren(an22Node, an72Node, an124Node,an225Node);

        TreeNode boeing = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.boeing), "No", "boeing", null));
        TreeNode b737 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b737), "No", "b737", Boeing737.class));
        TreeNode b757 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b757), "No", "b757", Boeing757.class));
        TreeNode b747 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b747), "No", "b747", Boeing747.class));
        TreeNode b767 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b767), "No", "b767", Boeing777.class));
        TreeNode b777 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b777), "No", "b777", Boeing777.class));
        TreeNode b787 = null;
        if (verifyDarkMode().equals("Yes")) {
            b787 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b787), "HighlightLight", "b787", null));
        } else {
            b787 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b787), "HighlightDark", "b787", null));
        }
        boeing.addChildren(b737, b747, b757, b767, b777, b787);

        TreeNode bombardier = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.bombardier), "No", "bombardier", null));
        TreeNode learjet75 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.learjet75), "No", "b767", Learjet75.class));
        TreeNode challenger650 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.challenger650), "No", "challenger650", Challenger650.class));

        bombardier.addChildren(challenger650, learjet75);
        manufacturerRoot.addChildren(airbus, antonov, boeing, bombardier);

        root.addChildren(manufacturerRoot);
        root.addChildren(alexaRoot);
        root.addChildren(flightTrackerRoot);
        root.addChildren(firebaseRoot);
        manufacturerRoot.setExpanded(true);
        boeing.setExpanded(true);

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

    /** SlideView **/

    private void slideView() {

        sliderView = findViewById(R.id.imageSlider);

        adapter = new SliderAdapter(this);
        sliderView.setSliderAdapter(adapter, false);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(false);
        renewItems(sliderView);


        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                Log.i("GGG", "onIndicatorClicked: " + sliderView.getCurrentPagePosition());
            }
        });
    }

    private void renewItems(View view) {
        List<SliderItem> sliderItemList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            SliderItem sliderItem = new SliderItem();
            if (i == 0) {
                sliderItem.setImageLocation(R.drawable.boeing_787_slider01);
            } else if (i == 1) {
                sliderItem.setImageLocation(R.drawable.boeing_787_slider02);
            } else if (i == 2) {
                sliderItem.setImageLocation(R.drawable.boeing_787_slider03);
            } else if (i == 3) {
                sliderItem.setImageLocation(R.drawable.boeing_787_slider04);
            }

            sliderItemList.add(sliderItem);
        }
        adapter.renewItems(sliderItemList);
    }

    private void removeLastItem(View view) {
        if (adapter.getCount() - 1 >= 0)
            adapter.deleteItem(adapter.getCount() - 1);
    }

    private void addNewItem(View view) {
        SliderItem sliderItem = new SliderItem();
        //sliderItem.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
        adapter.addItem(sliderItem);
    }

    /** SlideView **/

}