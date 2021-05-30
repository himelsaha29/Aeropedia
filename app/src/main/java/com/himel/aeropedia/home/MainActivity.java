package com.himel.aeropedia.home;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.himel.aeropedia.R;
import com.himel.aeropedia.alexa.AlexaActivity;
import com.himel.aeropedia.manufacturers.Airbus;
import com.himel.aeropedia.manufacturers.ManufacturerMenu;
import com.himel.aeropedia.treeview.TreeView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    Adapter adapter;
    List<Model> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        models = new ArrayList<Model>();
        models.add(new Model(R.drawable.a350_cover, "Airbus", "Airbus family"));
        models.add(new Model(R.drawable.a380_cover, "Amazon Alexa", "Smart voice assistant"));
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
}
