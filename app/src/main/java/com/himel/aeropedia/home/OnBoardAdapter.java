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

    public OnBoardAdapter (Context context) {
        this.context = context;
    }

    public int[] slideImages = {
            R.drawable.airbus_beluga_cover,
            R.drawable.bombardier_learjet75_cover,
            R.drawable.b787_cover
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
        View view = layoutInflater.inflate(R.layout.onboard_slide_layout, container, false);

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
