package com.himel.aeropedia.home;

import android.animation.ArgbEvaluator;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.himel.aeropedia.R;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    ViewPager viewPager;
    Adapter adapter;
    List<Model> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        models = new ArrayList<Model>();
        models.add(new Model(R.drawable.a350_cover, "A350", "A350"));
        models.add(new Model(R.drawable.a380_cover, "A380", "A280"));
        models.add(new Model(R.drawable.bombardier_cover, "BOMBARDIER", "BOMBARDIER"));
        models.add(new Model(R.drawable.boeing_cover, "BOEING", "787"));

        adapter = new Adapter(models, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(110, 0, 130, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.back_color1),
                getResources().getColor(R.color.back_color2),
                getResources().getColor(R.color.back_color3),
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

    }
}