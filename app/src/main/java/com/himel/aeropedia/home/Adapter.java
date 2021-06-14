package com.himel.aeropedia.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.himel.aeropedia.MapsActivity;
import com.himel.aeropedia.R;
import com.himel.aeropedia.airbus.AirbusA350;
import com.himel.aeropedia.alexa.AlexaActivity;
import com.himel.aeropedia.manufacturers.ManufacturerMenu;
import com.himel.aeropedia.mapbox.FlightMap;

import java.util.List;

public class Adapter extends PagerAdapter {

    private List<Model> models;
    private LayoutInflater layoutInflater;
    private Context context;
    private CardView cards;

    public Adapter(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.viewpager_items, container, false);

        ImageView imageView;
        TextView title, desc;

        imageView = view.findViewById(R.id.image);
        title = view.findViewById(R.id.title);

        imageView.setImageResource(models.get(position).getImage());
        title.setText(models.get(position).getTitle());
        cards = view.findViewById(R.id.viewpage_card);

        cards.getBackground().setAlpha(65);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == 0) {
                    Intent showContent = new Intent(context, ManufacturerMenu.class);
                    context.startActivity(showContent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else if (position == 1) {
                    Intent showContent = new Intent(context, AlexaActivity.class);
                    context.startActivity(showContent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else if (position == 2) {
                    Intent showContent = new Intent(context, MapsActivity.class);
                    context.startActivity(showContent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        cards.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (position == 0) {
                    Intent showContent = new Intent(context, ManufacturerMenu.class);
                    context.startActivity(showContent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else if (position == 1) {
                    Intent showContent = new Intent(context, AlexaActivity.class);
                    context.startActivity(showContent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else if (position == 2) {
                    Intent showContent = new Intent(context, MapsActivity.class);
                    context.startActivity(showContent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
