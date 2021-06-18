package com.himel.aeropedia.flightmap;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import soup.neumorphism.NeumorphButton;

public class FlightMap extends AppCompatActivity implements OnMapReadyCallback {

    private static final String BASE_URL = "https://opensky-network.org/api";

    private List<JSONArray> sv = new ArrayList<>();

    private GoogleMap mMap;
    private Dialog dialog;
    private NeumorphButton retry;
    private TextView loadingText;
    private BottomSheetBehavior bottomSheetBehavior;
    //private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCoordinates();
        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_maps);
        //button = findViewById(R.id.button);

        dialog = new Dialog(FlightMap.this);
        dialog.setContentView(R.layout.activity_map_loading_dialog);
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





//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomSheetBehavior.setPeekHeight(100);
//            }
//        });


        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        double latitude = 0.0;
        double longitude = 0.0;
        float true_track = 0.0f;
        String icao24 = null;

        for(int i = 0; i < sv.size(); i++) {
            try {
                latitude = (double) sv.get(i).getDouble(6);
                longitude = (double) sv.get(i).getDouble(5);
                true_track = (float) sv.get(i).getDouble(10);
                icao24 = sv.get(i).getString(0);
                if (icao24 == null) System.out.println("ICAO24 IS NULL");
                LatLng latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f,0.5f)
                        .rotation(true_track).icon(BitmapDescriptorFactory.fromResource(R.drawable.plane)).snippet(icao24));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("ICAO24 EXCEPTION");
                e.printStackTrace();
            }
        }

        FrameLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setPeekHeight(0, true);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                System.out.println("ICAO24  ===  " + marker.getSnippet());
                return true;
            }
        });
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


//                builder.authenticator(new Authenticator() {
//                    @Override
//                    public Request authenticate(Route route, Response response) throws IOException {
//                        if (responseCount(response) >= 3) {
//                            return null;
//                        }
//                        String credential = Credentials.basic(Global.username, Global.password);
//                        return response.request().newBuilder().header("Authorization", credential).build();
//                    }
//                });

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
                                dialog.setContentView(R.layout.activity_rest_api_failed_dialogue);
                                retry = dialog.findViewById(R.id.retry_rest);
                                retry.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.setContentView(R.layout.activity_map_loading_dialog);
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
                        dialog.dismiss();
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

//    private int responseCount(Response response) {
//        int result = 1;
//        while ((response = response.priorResponse()) != null) {
//            result++;
//        }
//        return result;
//    }

}