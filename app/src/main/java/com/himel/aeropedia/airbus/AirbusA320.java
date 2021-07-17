package com.himel.aeropedia.airbus;

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
import com.himel.aeropedia.alexa.AlexaActivity;
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

public class AirbusA320 extends AppCompatActivity {

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
            setContentView(R.layout.activity_airbus_a320_light);
        } else {
            setContentView(R.layout.activity_airbus_a320_dark);
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
        TreeNode manufacturerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Manufacturers", "No", "Manufacturers", null));
        TreeNode amazonRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ic_amazon_alexa, "Amazon Alexa", "No", "Alexa", AlexaActivity.class));
        TreeNode firebaseRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Firebase", "No", "firebase", null));



        TreeNode airbus = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, this.getString(R.string.airbus), "No", "airbus", null));
        TreeNode a220Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a220), "No", "a220", null));
        TreeNode a319Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a319), "No", "a319", null));
        TreeNode a320NeoFamilyNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a320neofamily), "No", "a320neoFamily", null));
        TreeNode a321Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a321), "No", "a321", null));

        TreeNode a330Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_alexa, this.getString(R.string.a330), "No", "a330", null));
        TreeNode a340Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a340), "No", "a340", null));
        TreeNode a320Node = null;
        if (verifyDarkMode().equals("Yes")) {
            a320Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a320), "HighlightLight", "a320", null));
        } else {
            a320Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a320), "HighlightDark", "a320", null));
        }
        TreeNode a350Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a350), "No", "a350", null));
        TreeNode a380Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a380), "No", "a380", null));


        airbus.addChildren(a220Node, a319Node, a320Node, a321Node, a320NeoFamilyNode, a330Node, a340Node, a350Node, a380Node);


        TreeNode boeing = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo_library, this.getString(R.string.boeing), "No", "boeing", null));
        TreeNode b777 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "B777", "No", "b777", null));
        TreeNode b787 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "B787", "No", "b787", null));
        boeing.addChildren(b777, b787);

        manufacturerRoot.addChildren(airbus, boeing);


        root.addChildren(manufacturerRoot);
        root.addChildren(amazonRoot);
        root.addChildren(firebaseRoot);
        manufacturerRoot.setExpanded(true);
        airbus.setExpanded(true);


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
        for (int i = 0; i < 3; i++) {
            SliderItem sliderItem = new SliderItem();
            if (i == 0) {
                sliderItem.setImageLocation(R.drawable.a320_slider1);
            } else if (i == 1) {
                sliderItem.setImageLocation(R.drawable.a320_slider2);
            } else if (i == 2) {
                sliderItem.setImageLocation(R.drawable.a320_slider3);
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