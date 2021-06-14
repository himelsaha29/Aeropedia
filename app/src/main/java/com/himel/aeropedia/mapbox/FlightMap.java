package com.himel.aeropedia.mapbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.himel.aeropedia.R;
import com.himel.aeropedia.alexa.Global;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.opensky.api.OpenSkyApi;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FlightMap extends AppCompatActivity {

    private static final String BASE_URL = "https://opensky-network.org/api";

    List<JSONArray> sv = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_from_mapbox);

    }






    private void getCoordinates() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OpenSkyApi api = null;
                    OpenSkyStates os = null;
                    try {
                        //states = new OpenSkyApi().getStates(0, new String[1]);
                        api = new OpenSkyApi(Global.username, Global.password);
                        os = api.getStates(0, null);
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                    System.out.println("Number of states: " + os.getStates().size());
                    System.out.println(os.getStates().size());
//                    for(StateVector s : os.getStates()) {
//                        sv.add(s);
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("EXCEPTION");
                }


                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(30, TimeUnit.SECONDS);
                builder.readTimeout(30, TimeUnit.SECONDS);
                builder.writeTimeout(30, TimeUnit.SECONDS);
                OkHttpClient client = builder.build();

                String url = BASE_URL + "/states/all";
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        //mapView.getMapAsync(FlightMap.this);
                        e.printStackTrace();
                        System.out.println("OKHTTP : FAILED");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                System.out.println(jsonObject);
                                JSONArray jsonArray = jsonObject.getJSONArray("states");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    sv.add(jsonArray.getJSONArray(i));
                                }
                                System.out.println((jsonArray.get(0)).getClass());
                                System.out.println("SV size after filling = " + sv.size());
                                System.out.println(jsonArray.length());
                                System.out.println("jsonARRAY CONTENT : " + jsonArray.getJSONArray(0));
                            } catch (JSONException e) {
                                System.out.println("JSONARRAY EXCEPTION: === " + e.getMessage());
                                e.printStackTrace();
                            }

                            FlightMap.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });

                        }
                    }
                });
            }

        });


        thread.start();
    }
}