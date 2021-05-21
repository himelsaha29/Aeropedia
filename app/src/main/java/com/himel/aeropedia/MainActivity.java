package com.himel.aeropedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.himel.aeropedia.airbus.AirbusA350;
import com.himel.aeropedia.home.Home;
import com.himel.aeropedia.manufacturers.Airbus;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CardView airbusCard;
    private CardView boeingCard;
    private CardView bombardierCard;
    private CardView antonovCard;
    private CardView embraerCard;
    private CardView cessnaCard;
    private boolean flag = false;
    private Animation translate = null;
    private ScrollView scrollView;
    private Locale locale;
    private Button langToggle;
    private ImageButton darkToggle;
    private String enableDark;
    private FlowingDrawer drawer;

    int i = 3;

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        locale = Locale.getDefault();
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.main_scroll);
        loadDarkSettings();

        airbusCard = findViewById(R.id.airbusCard);
        boeingCard = findViewById(R.id.boeingCard);
        bombardierCard = findViewById(R.id.bombardierCard);
        embraerCard = findViewById(R.id.embraerCard);
        antonovCard = findViewById(R.id.antonovCard);
        cessnaCard = findViewById(R.id.cessnaCard);
        scrollView = findViewById(R.id.main_scroll);
        langToggle = findViewById(R.id.lang_toggle);
        darkToggle = findViewById(R.id.dark_toggle);
        drawer = findViewById(R.id.drawerlayout);


        airbusCard.getBackground().setAlpha(65);
        boeingCard.getBackground().setAlpha(65);
        bombardierCard.getBackground().setAlpha(65);
        embraerCard.getBackground().setAlpha(65);
        antonovCard.getBackground().setAlpha(65);
        cessnaCard.getBackground().setAlpha(65);

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
                Intent intent = new Intent(getBaseContext(), AirbusA350.class);
                startActivity(intent);
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
                    darkToggle.setImageResource(R.drawable.ic_bulb_black_lit);
                    //darkToggle.setBackgroundResource(R.drawable.ic_bulb_black_lit);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else if (prefs.getString("DarkMode", "").equals("No")) {
                    toggleDark("Yes");
                    darkToggle.setImageResource(R.drawable.ic_bulb_black);
                    //darkToggle.setBackgroundResource(R.drawable.ic_bulb_black);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

            }
        });


        FlowingDrawer mDrawer = null;
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_FULLSCREEN);
        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSED) {
                    Log.i("MainActivity", "Drawer STATE_CLOSED");
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
            }

        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (!locale.equals(Locale.getDefault())) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        scrollView = findViewById(R.id.main_scroll);
        scrollView.scrollTo(0, scrollView.getTop());
        animateCards();
    }

    @Override
    public void onBackPressed() {
        if (!flag) {

            if (getResources().getConfiguration().locale.toString().contains("fr")) {
                Toast.makeText(MainActivity.this, "Appuyez à nouveau pour quitter", Toast.LENGTH_LONG).show();
            } else if (getResources().getConfiguration().toString().contains("en")) {
                Toast.makeText(MainActivity.this, "Press again to exit", Toast.LENGTH_LONG).show();
            }


            flag = true;
            new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // no function
                }

                public void onFinish() {
                    flag = false;
                }
            }.start();
        } else {
            this.finishAffinity();
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


    /**
     * Dark mode
     **/

    private void toggleDark(String darkEnabled) {
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("DarkMode", darkEnabled);
        editor.apply();
        loadDarkSettings();
    }


    private void loadDarkSettings() {
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

        if (enableDark.equals("Yes")) {
            scrollView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_two_dark, null));
        } else {
            scrollView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_one_light, null));
        }

    }

    /** Dark mode **/


}