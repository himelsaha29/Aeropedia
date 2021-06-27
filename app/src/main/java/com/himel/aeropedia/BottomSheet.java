package com.himel.aeropedia;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.himel.aeropedia.alexa.Global;
import com.himel.aeropedia.flightmap.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.ColorInt;
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

public class BottomSheet extends AppCompatActivity implements OnMapReadyCallback {

    private static final String BASE_URL = "https://opensky-network.org/api";

    private List<JSONArray> responseArray = new ArrayList<>();
    private List<MapMarker> coordList = new ArrayList<>();
    private HashMap<String, Marker> markerHashMap = new HashMap<>();
    private HashMap<String, String[]> hashMap = new HashMap<>();
    private GoogleMap mMap;
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

    Route route = new Route();
    String[] flightRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getCoordinates();
        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_maps_light);

        dialog = new Dialog(BottomSheet.this);
        dialog.setContentView(R.layout.activity_map_loading_dialog_light);
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

        markerPlaneBlack = vectorToBitmap(R.drawable.marker_plane_black, Color.BLACK);
        markerPlaneRed = vectorToBitmap(R.drawable.marker_plane_red_light, Color.RED);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(3.5f);

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
//                LatLng latLng = new LatLng(latitude, longitude);
//                mMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f,0.5f)
//                        .rotation(true_track).icon(markerPlaneBlack).snippet(icao));

                coordList.add(new MapMarker(icao, latitude, longitude, true_track));

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
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        /**
         *  Marker click listener
         */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (markerSelected != null) {
                    markerSelected.setIcon(markerPlaneBlack);
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

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        flightRoute = route.getRoute(markerInfo[0]);
                    }
                });

                thread.start();

                // wait until the thread is done, then join with main thread
                try {
                    thread.join();
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
                System.out.println("sQUAWK = " + markerInfo[7]);
                spiTV.setText(markerInfo[8]);
                positionSourceTV.setText(markerInfo[9]);

                return true;
            }
        });



        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {
                putMarkers(coordList);
            }
        });

        List<PatternItem> pattern = Arrays.asList(
                new Dot(), new Gap(20), new Dash(30), new Gap(20));


        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(45.5017, -73.5673), new LatLng(25.2854, 51.5310))
                .width(7)
                .pattern(pattern)
                .color(Color.BLUE)
                .geodesic(true));


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


                        BottomSheet.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.setContentView(R.layout.activity_rest_api_failed_dialog_light);
                                retry = dialog.findViewById(R.id.retry_rest);
                                retry.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.setContentView(R.layout.activity_map_loading_dialog_light);
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

                            BottomSheet.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.map);
                                    mapFragment.getMapAsync(BottomSheet.this);


                                }
                            });

                        }
                        dialog.dismiss();
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


    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        //DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void putMarkers(List<MapMarker> list) {
        if(this.mMap != null) {
            //This is the current user-viewable region of the map
            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

            //Loop through all the items that are available to be placed on the map
            for(MapMarker item : list) {

                //If the item is within the the bounds of the screen
                if (bounds.contains(new LatLng(item.getLatitude(), item.getLongitude()))) {
                    //If the item isn't already being displayed
                    if(!markerHashMap.containsKey(item.getIcao())) {
                        //Add the Marker to the Map and keep track of it with the HashMap
                        //getMarkerForItem just returns a MarkerOptions object
                        markerHashMap.put(item.getIcao(), mMap.addMarker(new MarkerOptions().position(new LatLng(item.getLatitude(), item.getLongitude())).anchor(0.5f,0.5f)
                                .rotation((float)item.getTrueTrack()).icon(markerPlaneBlack).snippet(item.getIcao())));


                    }
                } else {

                    //If the course was previously on screen
                    if(markerHashMap.containsKey(item.getIcao())) {
                        //1. Remove the Marker from the GoogleMap
                        markerHashMap.get(item.getIcao()).remove();

                        //2. Remove the reference to the Marker from the HashMap
                        markerHashMap.remove(item.getIcao());
                    }
                }
            }
        }
    }



}