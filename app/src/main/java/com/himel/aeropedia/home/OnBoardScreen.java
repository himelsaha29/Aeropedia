package com.himel.aeropedia.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.himel.aeropedia.R;

import java.util.Locale;

import soup.neumorphism.NeumorphButton;

public class OnBoardScreen extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dots;
    private TextView[] dotsArray;
    private OnBoardAdapter onBoardAdapter;
    private Locale locale;
    private String enableDark;
    private String enableDarkOnCreate;
    private NeumorphButton nextButton;
    private NeumorphButton prevButton;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        locale = Locale.getDefault();
        enableDarkOnCreate = verifyDarkMode();
        if(enableDark.equals("No")) {
            setContentView(R.layout.activity_on_board_screen_light);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#E6f2f4f6"));
            }
        } else {
            setContentView(R.layout.activity_on_board_screen_dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#ff141635"));
            }
        }


        viewPager = findViewById(R.id.viewPager);
        dots = findViewById(R.id.dots);

        prevButton = findViewById(R.id.back);
        nextButton = findViewById(R.id.next);

        if(enableDark.equals("No")) {
            onBoardAdapter = new OnBoardAdapter(this, false);
        } else {
            onBoardAdapter = new OnBoardAdapter(this, true);
        }

        viewPager.setAdapter(onBoardAdapter);

        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextButton.getText().toString().equalsIgnoreCase("Finish")) {
                    SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                    editor.putString("OnBoardDone", "Yes");
                    editor.apply();
                    startActivity(new Intent(OnBoardScreen.this, MainActivity.class));
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_in);
                    finish();
                } else {
                    viewPager.setCurrentItem(currentPage + 1);
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(currentPage - 1);
            }
        });

    }

    private void addDotsIndicator(int position){
        dotsArray = new TextView[3];
        dots.removeAllViews();
        for(int i = 0; i < dotsArray.length; i++) {
            dotsArray[i] = new TextView(this);
            dotsArray[i].setText(Html.fromHtml("&#8226;"));
            dotsArray[i].setTextSize(35);
            dotsArray[i].setTextColor(Color.parseColor("#cccccc"));
            dotsArray[i].setGravity(Gravity.CENTER);
            dots.addView(dotsArray[i]);
        }

        if(dotsArray.length > 0){
            dotsArray[position].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage = position;

            if(position == 0) {
                nextButton.setEnabled(true);
                prevButton.setEnabled(false);
                prevButton.setVisibility(View.INVISIBLE);
                nextButton.setText("Next");
            } else if (position == dotsArray.length - 1) {
                nextButton.setEnabled(true);
                prevButton.setEnabled(true);
                prevButton.setVisibility(View.VISIBLE);
                nextButton.setText("Finish");
                prevButton.setText("Back");
            } else {
                nextButton.setEnabled(true);
                prevButton.setEnabled(true);
                prevButton.setVisibility(View.VISIBLE);
                nextButton.setText("Next");
                prevButton.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

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



}