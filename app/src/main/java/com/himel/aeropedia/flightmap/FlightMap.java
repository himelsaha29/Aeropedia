package com.himel.aeropedia.flightmap;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.himel.aeropedia.R;
import com.himel.aeropedia.alexa.Global;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import soup.neumorphism.NeumorphButton;

public class FlightMap extends AppCompatActivity implements OnMapReadyCallback {

    private static final String BASE_URL = "https://opensky-network.org/api";

    private List<PatternItem> pattern = Arrays.asList(
            new Dot(), new Gap(20), new Dash(30), new Gap(20));

    private List<JSONArray> responseArray = new ArrayList<>();
    private HashMap<String, String[]> hashMap = new HashMap<>();
    private HashMap<String, Marker> markerTracker = new HashMap<>();
    private GoogleMap mMap;
    private String enableDark;
    private Locale locale;
    private String enableDarkOnCreate;
    private Dialog dialog;
    private NeumorphButton retry;
    private TextView loadingText;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView icaoTV;
    private TextView origin;
    private TextView destination;
    private TextView aircraft;
    private TextView callsignTV;
    private TextView country;
    private TextView lamitude;
    private TextView lomgitude;
    private TextView airline;
    private TextView engineType;
    private TextView baroAltitudeTV;
    private TextView geoAltitudeTV;
    private TextView onGroundTv;
    private TextView velocityTV;
    private TextView verticalRateTV;
    private TextView trackTV;
    private TextView squawkTV;
    private TextView spiTV;
    private TextView positionSourceTV;

    private BitmapDescriptor markerPlaneBlack;
    private BitmapDescriptor markerPlaneRed;
    private Marker markerSelected;
    private Button liveButton;
    private ProgressBar progressBar;

    Route route = new Route();
    String[] flightRoute;
    String[] flightTrack;
    private Polyline polyline;
    private boolean bottomSheetIsExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        enableDarkOnCreate = verifyDarkMode();
        loadLocale();
        locale = Locale.getDefault();
        dialog = new Dialog(FlightMap.this);
        getCoordinates();
        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_maps);


        if(enableDarkOnCreate.equals("No")) {
            dialog.setContentView(R.layout.activity_map_loading_dialog_light);
        } else {
            dialog.setContentView(R.layout.activity_map_loading_dialog_dark);
        }
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.95), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                    finish();

                    return true;
                }
                return false;
            }
        });
        dynamicDialog();

        if(enableDarkOnCreate.equals("No")) {
            markerPlaneBlack = vectorToBitmap(R.drawable.marker_plane_black);
            markerPlaneRed = vectorToBitmap(R.drawable.marker_plane_red);
        } else {
            markerPlaneBlack = vectorToBitmap(R.drawable.marker_plane_white);
            markerPlaneRed = vectorToBitmap(R.drawable.marker_plane_red_dark);
        }


        liveButton = findViewById(R.id.button);
        if (enableDarkOnCreate.equals("Yes")) {
            liveButton.setTextColor(Color.WHITE);
        }
        progressBar = findViewById(R.id.progress_bar);
        liveButton.getBackground().setAlpha(45);

        liveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateRequest();
                if(polyline != null) {
                    polyline.remove();
                }
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(3.5f);
        if(enableDarkOnCreate.equals("Yes")) {
            MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_dark_mode);
            mMap.setMapStyle(mapStyleOptions);
        }

        icaoTV = findViewById(R.id.icao);
        origin = findViewById(R.id.origin_airport);
        destination = findViewById(R.id.destination_airport);
        aircraft = findViewById(R.id.aircraft);
        callsignTV = findViewById(R.id.callsign);
        country = findViewById(R.id.country_of_reg);
        lamitude = findViewById(R.id.latitude);
        lomgitude = findViewById(R.id.longitude);
        airline = findViewById(R.id.airline);
        engineType = findViewById(R.id.engine_type);
        baroAltitudeTV = findViewById(R.id.barometric_altitude);
        geoAltitudeTV = findViewById(R.id.geometric_altitude);
        onGroundTv = findViewById(R.id.on_ground);
        velocityTV = findViewById(R.id.velocity);
        verticalRateTV = findViewById(R.id.vertical_rate);
        trackTV = findViewById(R.id.track);
        squawkTV = findViewById(R.id.squawk);
        spiTV = findViewById(R.id.spi);
        positionSourceTV = findViewById(R.id.position_source);




        double latitude = 0.0;
        double longitude = 0.0;
        float true_track = 0.0f;
        String icao = null;
        String callsign = null;
        String countryOfReg = null;
        float baro_altitude = 0.0f;
        float geo_altitude = 0.0f;
        boolean onGround;
        float velocity = 0.0f;
        float verticalRate = 0.0f;
        String squawk = null;
        boolean spi;
        int positionSource = 0;


        // adding Markers

        for(int i = 0; i < responseArray.size(); i++) {
            try {
                latitude = (double) responseArray.get(i).getDouble(6);
                longitude = (double) responseArray.get(i).getDouble(5);
                true_track = (float) responseArray.get(i).getDouble(10);
                icao = responseArray.get(i).getString(0);

                if (icao == null) System.out.println("ICAO24 IS NULL");
                LatLng latLng = new LatLng(latitude, longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f,0.5f)
                        .rotation(true_track).icon(markerPlaneBlack).snippet(icao).flat(true));

                if (!markerTracker.containsKey(icao)) {
                    markerTracker.put(icao, marker);
                }

                try {
                    callsign = responseArray.get(i).getString(1);
                    if(callsign == null || callsign.equalsIgnoreCase("null")) {
                        callsign = "N/A";
                    } else {
                        callsign = callsign.trim();
                    }
                } catch (Exception e) {
                    callsign = "N/A";
                }
                try {
                    countryOfReg = responseArray.get(i).getString(2);
                } catch (Exception e) {
                    countryOfReg = "N/A";
                }
                try {
                    baro_altitude = (float) responseArray.get(i).getDouble(7);
                } catch (Exception e) {
                    baro_altitude = -0.10169f;
                }
                try {
                    geo_altitude = (float) responseArray.get(i).getDouble(13);
                } catch (Exception e) {
                    geo_altitude = -0.10169f;
                }
                try {
                    onGround = responseArray.get(i).getBoolean(8);
                } catch (Exception e) {
                    onGround = false;
                }
                try {
                    velocity = (float) responseArray.get(i).getDouble(9);
                } catch (Exception e) {
                    velocity = -0.10169f;
                }
                try {
                    verticalRate = (float) responseArray.get(i).getDouble(11);
                } catch (Exception e) {
                    verticalRate = -0.10169f;
                }
                try {
                    squawk = responseArray.get(i).getString(14);
                    if(squawk == null || squawk.equalsIgnoreCase("null")) {
                        squawk = "N/A";
                    } else {
                        squawk = squawk.trim();
                    }
                } catch (Exception e) {
                    squawk = "N/A";
                }
                try {
                    spi = responseArray.get(i).getBoolean(15);
                } catch (Exception e) {
                    spi = false;
                }
                try {
                    positionSource = responseArray.get(i).getInt(16);
                } catch (Exception e) {
                    positionSource = -1;
                }

                String[] storeInMap = new String[10];
                storeInMap[0] = callsign;
                storeInMap[1] = countryOfReg;
                if(baro_altitude == -0.10169f) {
                    storeInMap[2] = "N/A";
                } else {
                    storeInMap[2] = String.valueOf(baro_altitude) + " m";
                }
                if(geo_altitude == -0.10169f) {
                    storeInMap[3] = "N/A";
                } else {
                    storeInMap[3] = String.valueOf(geo_altitude) + " m";
                }
                String tempOnGround = String.valueOf(onGround);
                storeInMap[4] = tempOnGround.substring(0, 1).toUpperCase() + tempOnGround.substring(1, tempOnGround.trim().length());
                if(velocity == -0.10169f) {
                    storeInMap[5] = "N/A";
                } else {
                    storeInMap[5] = String.valueOf(velocity) + " m/s";
                }
                if(verticalRate == -0.10169f) {
                    storeInMap[6] = "N/A";
                } else {
                    storeInMap[6] = String.valueOf(verticalRate) + " m/s";
                }
                storeInMap[7] = squawk;

                String tempSpi = String.valueOf(spi);
                storeInMap[8] = tempSpi.substring(0, 1).toUpperCase() + tempSpi.substring(1, tempSpi.trim().length());
                if(positionSource == 0) {
                    storeInMap[9] = "ADS-B";
                } else if (positionSource == 1) {
                    storeInMap[9] = "ASTERIX";
                } else if (positionSource == 2) {
                    storeInMap[9] = "MLAT";
                } else {
                    storeInMap[9] = "N/A";
                }

                hashMap.put(icao, storeInMap);


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Marker add EXCEPTION");
                e.printStackTrace();
                e.getMessage();
            }
        }

        FrameLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setPeekHeight(0, true);
                    if (markerSelected != null) {
                        markerSelected.setIcon(markerPlaneBlack);
                    }
                    if(polyline != null) {
                        polyline.remove();
                    }
                    bottomSheetIsExpanded = false;
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetIsExpanded = true;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (markerSelected != null) {
                    markerSelected.setIcon(markerPlaneBlack);
                }
                if(polyline != null) {
                    polyline.remove();
                }
                bottomSheetBehavior.setPeekHeight(120);
                String snippet = marker.getSnippet();
                // ======== LOADING ========
                icaoTV.setText(R.string.loading);
                origin.setText(R.string.loading);
                destination.setText(R.string.loading);
                aircraft.setText(R.string.loading);
                callsignTV.setText(R.string.loading);
                country.setText(R.string.loading);
                lamitude.setText(R.string.loading);
                lomgitude.setText(R.string.loading);
                airline.setText(R.string.loading);
                engineType.setText(R.string.loading);
                baroAltitudeTV.setText(R.string.loading);
                geoAltitudeTV.setText(R.string.loading);
                onGroundTv.setText(R.string.loading);
                velocityTV.setText(R.string.loading);
                verticalRateTV.setText(R.string.loading);
                squawkTV.setText(R.string.loading);
                spiTV.setText(R.string.loading);
                positionSourceTV.setText(R.string.loading);
                // =========================

                String[] markerInfo = hashMap.get(marker.getSnippet());

                Thread threadRoute = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        flightRoute = route.getRoute(markerInfo[0]);
                    }
                });

                threadRoute.start();

                // wait until the thread is done, then join with main thread
                try {
                    threadRoute.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                markerSelected = marker;
                marker.setIcon(markerPlaneRed);

                icaoTV.setText(marker.getSnippet());
                origin.setText(flightRoute[0]);
                destination.setText(flightRoute[1]);

                if((flightRoute[2] + " " + flightRoute[3]).contains("N/A")) {
                    aircraft.setText("N/A");
                } else {
                    aircraft.setText(flightRoute[2] + " " + flightRoute[3]);
                }

                callsignTV.setText(markerInfo[0]);
                country.setText(markerInfo[1]);
                lamitude.setText(String.valueOf(marker.getPosition().latitude));
                lomgitude.setText(String.valueOf(marker.getPosition().longitude));
                airline.setText(flightRoute[5]);
                engineType.setText(flightRoute[4]);
                baroAltitudeTV.setText(markerInfo[2]);
                geoAltitudeTV.setText(markerInfo[3]);
                onGroundTv.setText(markerInfo[4]);
                velocityTV.setText(markerInfo[5]);
                verticalRateTV.setText(markerInfo[6]);
                trackTV.setText(String.valueOf(marker.getRotation()) + "°");
                squawkTV.setText(markerInfo[7]);
                spiTV.setText(markerInfo[8]);
                positionSourceTV.setText(markerInfo[9]);

                if(!flightRoute[1].equalsIgnoreCase("N/A")) {


                    Thread threadTrack = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            flightTrack = route.getTrack(flightRoute[1]);
                        }
                    });

                    threadTrack.start();

                    // wait until the thread is done, then join with main thread
                    try {
                        threadTrack.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    if(flightTrack[0].equalsIgnoreCase("true")) {
                        Float destinationAirportLat = Float.valueOf(flightTrack[1]);
                        Float destinationAirportLong = Float.valueOf(flightTrack[2]);

                        if(enableDarkOnCreate.equals("No")) {
                            polyline = mMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), new LatLng(destinationAirportLat, destinationAirportLong))
                                    .width(7)
                                    .pattern(pattern)
                                    .color(Color.BLUE)
                                    .geodesic(true));
                        } else {
                            polyline = mMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), new LatLng(destinationAirportLat, destinationAirportLong))
                                    .width(7)
                                    .pattern(pattern)
                                    .color(Color.YELLOW)
                                    .geodesic(true));
                        }
                    }
                }

                return true;
            }
        });
    }



    private void getCoordinates() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient.Builder builder = new OkHttpClient.Builder();

                builder.connectTimeout(30, TimeUnit.SECONDS);
                builder.readTimeout(30, TimeUnit.SECONDS);
                builder.writeTimeout(30, TimeUnit.SECONDS);
                String credential = Credentials.basic(Global.username, Global.password);
                OkHttpClient client = builder.build();

                String url = BASE_URL + "/states/all";
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", credential)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {


                        FlightMap.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(enableDarkOnCreate.equals("No")) {
                                    dialog.setContentView(R.layout.activity_rest_api_failed_dialog_light);
                                } else {
                                    dialog.setContentView(R.layout.activity_rest_api_failed_dialog_dark);
                                }
                                retry = dialog.findViewById(R.id.retry_rest);
                                retry.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(enableDarkOnCreate.equals("No")) {
                                            dialog.setContentView(R.layout.activity_map_loading_dialog_light);
                                        } else {
                                            dialog.setContentView(R.layout.activity_map_loading_dialog_dark);
                                        }
                                        dynamicDialog();
                                    }
                                });
                                getCoordinates();
                            }
                        });

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
                                int time = jsonObject.getInt("time");
                                System.out.println("TIME === " +  time);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    responseArray.add(jsonArray.getJSONArray(i));
                                }
                                System.out.println((jsonArray.get(0)).getClass());
                                System.out.println("SV size after filling = " + responseArray.size());
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

                            if(dialog != null) {
                                dialog.dismiss();
                            }

                        }

                    }
                });
            }

        });


        thread.start();
    }

    private void dynamicDialog() {
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if((millisUntilFinished - 2000) >= 0 && (millisUntilFinished - 2000) <= 1000 ) {
                    loadingText = dialog.findViewById(R.id.loading_text);
                    loadingText.setText(R.string.map_loading2);
                }
            }
            public void onFinish() {
                loadingText = dialog.findViewById(R.id.loading_text);
                loadingText.setText(R.string.map_loading3);

            }
        }.start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        //DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private void updateRequest(){
        System.out.println("Updating...");
        progressBar.setVisibility(View.VISIBLE);
        liveButton.setVisibility(View.GONE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient.Builder builder = new OkHttpClient.Builder();

                builder.connectTimeout(30, TimeUnit.SECONDS);
                builder.readTimeout(30, TimeUnit.SECONDS);
                builder.writeTimeout(30, TimeUnit.SECONDS);
                String credential = Credentials.basic(Global.username, Global.password);
                OkHttpClient client = builder.build();

                String url = BASE_URL + "/states/all";
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", credential)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        updateRequest();

                        e.printStackTrace();
                        System.out.println("OKHTTP UPDATE : FAILED");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                System.out.println(jsonObject);
                                JSONArray jsonArray = jsonObject.getJSONArray("states");
                                int time = jsonObject.getInt("time");
                                System.out.println("TIME === " +  time);

                                try {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        responseArray.add(jsonArray.getJSONArray(i));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                System.out.println((jsonArray.get(0)).getClass());
                                System.out.println("SV size after filling = " + responseArray.size());
                                System.out.println(jsonArray.length());
                                System.out.println("jsonARRAY CONTENT : " + jsonArray.getJSONArray(0));
                            } catch (JSONException e) {
                                System.out.println("JSONARRAY EXCEPTION: === " + e.getMessage());
                                e.printStackTrace();
                            }


                            FlightMap.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parseJSONResponse();
                                }
                            });




                        }
                    }
                });
            }

        });


        thread.start();
    }

    private void parseJSONResponse() {

        double latitude = 0.0;
        double longitude = 0.0;
        float true_track = 0.0f;
        String icao = null;
        String callsign = null;
        String countryOfReg = null;
        float baro_altitude = 0.0f;
        float geo_altitude = 0.0f;
        boolean onGround;
        float velocity = 0.0f;
        float verticalRate = 0.0f;
        String squawk = null;
        boolean spi;
        int positionSource = 0;

        for(int i = 0; i < responseArray.size(); i++) {
            try {
                latitude = (double) responseArray.get(i).getDouble(6);
                longitude = (double) responseArray.get(i).getDouble(5);
                true_track = (float) responseArray.get(i).getDouble(10);
                icao = responseArray.get(i).getString(0);

                if (icao == null) System.out.println("ICAO24 IS NULL");


                if (!markerTracker.containsKey(icao)) {
                    LatLng latLng = new LatLng(latitude, longitude);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f,0.5f)
                            .rotation(true_track).icon(markerPlaneBlack).snippet(icao).flat(true));

                    markerTracker.put(icao, marker);
                } else {
                    LatLng latLngNew = new LatLng(latitude, longitude);
                    Marker m = markerTracker.get(icao);
                    m.setPosition(latLngNew);
                    m.setRotation(true_track);
                }

                try {
                    callsign = responseArray.get(i).getString(1);
                    if(callsign == null || callsign.equalsIgnoreCase("null")) {
                        callsign = "N/A";
                    } else {
                        callsign = callsign.trim();
                    }
                } catch (Exception e) {
                    callsign = "N/A";
                }
                try {
                    countryOfReg = responseArray.get(i).getString(2);
                } catch (Exception e) {
                    countryOfReg = "N/A";
                }
                try {
                    baro_altitude = (float) responseArray.get(i).getDouble(7);
                } catch (Exception e) {
                    baro_altitude = -0.10169f;
                }
                try {
                    geo_altitude = (float) responseArray.get(i).getDouble(13);
                } catch (Exception e) {
                    geo_altitude = -0.10169f;
                }
                try {
                    onGround = responseArray.get(i).getBoolean(8);
                } catch (Exception e) {
                    onGround = false;
                }
                try {
                    velocity = (float) responseArray.get(i).getDouble(9);
                } catch (Exception e) {
                    velocity = -0.10169f;
                }
                try {
                    verticalRate = (float) responseArray.get(i).getDouble(11);
                } catch (Exception e) {
                    verticalRate = -0.10169f;
                }
                try {
                    squawk = responseArray.get(i).getString(14);
                    if(squawk == null || squawk.equalsIgnoreCase("null")) {
                        squawk = "N/A";
                    } else {
                        squawk = squawk.trim();
                    }
                } catch (Exception e) {
                    squawk = "N/A";
                }
                try {
                    spi = responseArray.get(i).getBoolean(15);
                } catch (Exception e) {
                    spi = false;
                }
                try {
                    positionSource = responseArray.get(i).getInt(16);
                } catch (Exception e) {
                    positionSource = -1;
                }

                String[] storeInMap = new String[10];
                storeInMap[0] = callsign;
                storeInMap[1] = countryOfReg;
                if(baro_altitude == -0.10169f) {
                    storeInMap[2] = "N/A";
                } else {
                    storeInMap[2] = String.valueOf(baro_altitude);
                }
                if(geo_altitude == -0.10169f) {
                    storeInMap[3] = "N/A";
                } else {
                    storeInMap[3] = String.valueOf(geo_altitude);
                }
                String tempOnGround = String.valueOf(onGround);
                storeInMap[4] = tempOnGround.substring(0, 1).toUpperCase() + tempOnGround.substring(1, tempOnGround.trim().length());
                if(velocity == -0.10169f) {
                    storeInMap[5] = "N/A";
                } else {
                    storeInMap[5] = String.valueOf(velocity);
                }
                if(verticalRate == -0.10169f) {
                    storeInMap[6] = "N/A";
                } else {
                    storeInMap[6] = String.valueOf(verticalRate);
                }
                storeInMap[7] = squawk;

                String tempSpi = String.valueOf(spi);
                storeInMap[8] = tempSpi.substring(0, 1).toUpperCase() + tempSpi.substring(1, tempSpi.trim().length());
                if(positionSource == 0) {
                    storeInMap[9] = "ADS-B";
                } else if (positionSource == 1) {
                    storeInMap[9] = "ASTERIX";
                } else if (positionSource == 2) {
                    storeInMap[9] = "MLAT";
                } else {
                    storeInMap[9] = "N/A";
                }

                hashMap.put(icao, storeInMap);


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Marker add EXCEPTION");
                e.printStackTrace();
                e.getMessage();
            }
        }
        progressBar.setVisibility(View.GONE);
        liveButton.setVisibility(View.VISIBLE);
    }


    /** Dark mode **/

    private void toggleDark(String darkEnabled) {
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("DarkMode", darkEnabled);
        editor.apply();
    }

    private String verifyDarkMode() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        if (prefs.getString("DarkMode", "").equals("")) {

            SharedPreferences.Editor editor = prefs.edit();
            int currentNightMode = getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;

            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    editor.putString("DarkMode", "No");
                    editor.apply();
                    break;
                default:
                    editor.putString("DarkMode", "Yes");
                    editor.apply();
                    break;

            }
        }
        enableDark = prefs.getString("DarkMode", "Yes");

        return enableDark;
    }

    /** Dark mode **/


    /**
     * Changing app language
     **/

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("Language", language);
        editor.apply();
    }

    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        if (prefs.getString("Language", getResources().getConfiguration().locale.toString().substring(0, 2)).equals("")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Language", getResources().getConfiguration().locale.toString().substring(0, 2));
            editor.apply();
        }
        String language = prefs.getString("Language", getResources().getConfiguration().locale.toString().substring(0, 2));
        setLocale(language);
    }

    /** Changing app language **/


    @Override
    public void onBackPressed() {
        if(bottomSheetIsExpanded) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}