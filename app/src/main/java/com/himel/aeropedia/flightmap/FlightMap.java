package com.himel.aeropedia.flightmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.himel.aeropedia.R;
import com.himel.aeropedia.alexa.Global;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.opensky.api.OpenSkyApi;
import org.opensky.model.OpenSkyStates;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FlightMap extends AppCompatActivity implements OnMapReadyCallback {

    private static final String BASE_URL = "https://opensky-network.org/api";

    private List<JSONArray> sv = new ArrayList<>();

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCoordinates();
        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_maps);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        double latitude = 0.0;
        double longitude = 0.0;
        float true_track = 0.0f;

        for(int i = 0; i < sv.size(); i++) {
            try {
                latitude = (double) sv.get(i).getDouble(6);
                longitude = (double) sv.get(i).getDouble(5);
                true_track = (float) sv.get(i).getDouble(10);
                LatLng sydney = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(sydney).anchor(0.5f,0.5f)
                        .rotation(true_track).icon(BitmapDescriptorFactory.fromResource(R.drawable.plain)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


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
                                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.map);
                                    mapFragment.getMapAsync(FlightMap.this);
                                }
                            });

                        }
                    }
                });
            }

        });


        thread.start();
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_marker);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}