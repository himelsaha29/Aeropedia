package com.himel.aeropedia.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.himel.aeropedia.R;

public class OnBoardAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    boolean dark;

    public OnBoardAdapter (Context context, boolean dark) {
        this.context = context;
        this.dark = dark;
    }

    public int[] slideImages = {
            R.drawable.onboard_1,
            R.drawable.onboard_2,
            R.drawable.onboard_3,
    };

    public String[] slideHeadings = {
            "Page 1",
            "Page 2",
            "Page 3"
    };

    public String[] descriptions = {
            "Page 1 desc",
            "Page 2 desc",
            "Page 3 desc"
    };

    @Override
    public int getCount() {
        return slideHeadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view;
        if(dark == false) {
            view = layoutInflater.inflate(R.layout.onboard_slide_layout_light, container, false);
        } else {
            view = layoutInflater.inflate(R.layout.onboard_slide_layout_dark, container, false);
        }

        ImageView slideImage = view.findViewById(R.id.slide_image);
        TextView slideHeader = view.findViewById(R.id.slide_header);
        TextView slideDesc = view.findViewById(R.id.slide_desc);

        slideImage.setImageResource(slideImages[position]);
        slideHeader.setText(slideHeadings[position]);
        slideDesc.setText(descriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
