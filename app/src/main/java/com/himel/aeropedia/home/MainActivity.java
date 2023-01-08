package com.himel.aeropedia.home;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.himel.aeropedia.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    Adapter adapter;
    List<Model> models;
    private Locale locale;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private boolean flag = false;
    private SharedPreferences.Editor editor;
    private FirebaseAnalytics mFirebaseAnalytics;
    private int id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        locale = Locale.getDefault();
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        try {
            Bundle bundle = new Bundle();
            bundle.putString("home", "home");
            mFirebaseAnalytics.logEvent("home", bundle);
        } catch (Exception e) {}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        models = new ArrayList<Model>();
        models.add(new Model(R.drawable.aircraft_gallery_cover, getString(R.string.aircraft_gallery)));
        models.add(new Model(R.drawable.real_time_flight_tracker_cover, getString(R.string.tracker_menu)));
        models.add(new Model(R.drawable.cover_alexa, getString(R.string.alexa_menu)));
        models.add(new Model(R.drawable.community_cover, getString(R.string.community)));

        adapter = new Adapter(models, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(110, 0, 130, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.back_color1),
                getResources().getColor(R.color.back_color3),
                getResources().getColor(R.color.back_color2),
                getResources().getColor(R.color.back_color4)
        };

        colors = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapter.getCount() -1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                }

                else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }


            @Override
            public void onPageSelected(int position) {

            }



            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        modifyAlexaRestart();

    }

    @Override
    public void onBackPressed() {
        if (!flag) {

            SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            if (prefs.getString("Language", getResources().getConfiguration().locale.toString().substring(0, 2)).contains("fr")) {
                Toast.makeText(MainActivity.this, "Appuyez à nouveau pour quitter", Toast.LENGTH_LONG).show();
            } else if (prefs.getString("Language", getResources().getConfiguration().locale.toString().substring(0, 2)).contains("en")) {
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
            //this.finishAffinity();
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        modifyAlexaRestart();

        try {
            Bundle bundle = new Bundle();
            bundle.putString("return_to_home", String.valueOf(id));
            mFirebaseAnalytics.logEvent("return_to_home", bundle);
            id++;
        } catch(Exception e) {}

        if (!locale.equals(Locale.getDefault())) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    private void modifyAlexaRestart() {
        editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("AlexaRestart", "Yes");
        editor.apply();
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


}
