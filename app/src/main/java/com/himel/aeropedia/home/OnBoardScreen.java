package com.himel.aeropedia.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.himel.aeropedia.R;

public class OnBoardScreen extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dots;
    private TextView[] dotsArray;
    private OnBoardAdapter onBoardAdapter;

    private Button nextButton;
    private Button prevButton;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board_screen);

        viewPager = findViewById(R.id.viewPager);
        dots = findViewById(R.id.dots);

        prevButton = findViewById(R.id.back);
        nextButton = findViewById(R.id.next);

        onBoardAdapter = new OnBoardAdapter(this);
        viewPager.setAdapter(onBoardAdapter);

        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(currentPage + 1);
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
            dotsArray[position].setTextColor(Color.parseColor("#ffffff"));
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

}