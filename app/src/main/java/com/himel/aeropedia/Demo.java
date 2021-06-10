package com.himel.aeropedia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.opensky.api.OpenSkyApi;
import org.opensky.model.OpenSkyStates;

import java.io.IOException;

public class Demo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demooo);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    OpenSkyStates states = null;
                    OpenSkyApi api = null;
                    OpenSkyStates os = null;
                    try {
                        //states = new OpenSkyApi().getStates(0, new String[1]);
                        api = new OpenSkyApi("Saha", "opensky");
                        os = api.getStates(0, null);
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                    //System.out.println("Number of states: " + states.getStates().size());
                    System.out.println(os.getStates().size());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("EXCEPTION");
                }
            }
        });

        thread.start();

    }
}