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

import java.util.Locale;

public class OnBoardAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    boolean dark;
    String locale;

    public OnBoardAdapter (Context context, boolean dark, String locale) {
        this.context = context;
        this.dark = dark;
        this.locale = locale;
    }

    public int[] slideImagesDarkEn = {
            R.drawable.main_logo_foreground,
            R.drawable.onboard_1,
            R.drawable.onboard_2,
            R.drawable.onboard_3_en,
            R.drawable.onboard_4
    };


    public int[] slideImagesDarkFr = {
            R.drawable.main_logo_foreground,
            R.drawable.onboard_1,
            R.drawable.onboard_2,
            R.drawable.onboard_3_fr,
            R.drawable.onboard_4
    };

    public String[] slideHeadingsEn = {
            "Welcome Aboard",
            "Swipe from the left bezel to open the drawer for easy navigation, light/dark mode and language toggles",
            "On the interactive flight map, tap on an aircraft for flight route and more information about the flight/aircraft",
            "Tap LIVE to get the latest flight data",
            "To interact with Amazon Alexa, tap the recorder to start recording and tap again when you are done or wait for it to respond automatically"
    };

    public String[] slideHeadingsFr = {
            "Bienvenue",
            "Faites glisser depuis le cadre gauche pour ouvrir le tiroir pour une navigation facile, des bascules de mode clair/foncé et de langue",
            "Sur la carte de vol interactive, appuyez sur un avion pour l\'itinéraire de vol et plus d\'informations sur le vol/l\'avion",
            "Appuyez sur EN DIRECT pour obtenir les données de vol les plus récentes",
            "Pour interagir avec Amazon Alexa, appuyez sur l\'enregistreur pour démarrer l\'enregistrement et appuyez à nouveau lorsque vous avez terminé ou attendez qu\'il réponde automatiquement"
    };

    public String[] descriptions = {
            "",
            "",
            "",
            "",
            ""
    };

    @Override
    public int getCount() {
        return slideHeadingsEn.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboard_slide_layout_light, container, false);

        ImageView slideImage = view.findViewById(R.id.slide_image);
        TextView slideHeader = view.findViewById(R.id.slide_header);
        TextView slideDesc = view.findViewById(R.id.slide_desc);



        if(this.locale.equalsIgnoreCase("fr")) {
            slideImage.setImageResource(slideImagesDarkFr[position]);
        } else if(this.locale.equalsIgnoreCase("en")) {
            slideImage.setImageResource(slideImagesDarkEn[position]);
        }


        if(this.locale.equalsIgnoreCase("fr")) {
            slideHeader.setText(slideHeadingsFr[position]);
        } else if(this.locale.equalsIgnoreCase("en")) {
            slideHeader.setText(slideHeadingsEn[position]);
        }
        slideDesc.setText(descriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

}
