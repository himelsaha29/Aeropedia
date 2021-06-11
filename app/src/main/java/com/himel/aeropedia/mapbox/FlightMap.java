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
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

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

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

/**
 * Display {@link SymbolLayer} icons on the map and determine the appropriate icon
 * based on a property in each {@link Feature}.
 */
public class FlightMap extends AppCompatActivity implements
        OnMapReadyCallback, MapboxMap.OnMapClickListener {

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String RED_ICON_ID = "RED_ICON_ID";
    private static final String YELLOW_ICON_ID = "YELLOW_ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private static final String ICON_PROPERTY = "ICON_PROPERTY";

    private static final String BASE_URL = "https://opensky-network.org/api";

    private MapView mapView;
    private MapboxMap mapboxMap;
    private List<StateVector> sv = new ArrayList<>();

    private GeoJsonSource geoJsonSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCoordinates();
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_from_mapbox);

        mapView = findViewById(R.id.mapView);
        try {
            Thread.sleep(3000);
        } catch (Exception e ){
            System.out.println("PAUSED");
        }
        System.out.println("PAUSED FINISHED");
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {


        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                style.addImage(("marker_icon"), BitmapUtils.getBitmapFromDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.marker, null)));
                geoJsonSource = new GeoJsonSource("source-id",
                        FeatureCollection.fromFeatures(initCoordinateData()));
                style.addSource(geoJsonSource);

                style.addLayer(new SymbolLayer("layer-id", "source-id")
                        .withProperties(
                                PropertyFactory.iconImage("marker_icon"),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconAllowOverlap(true)
                        ));


                System.out.println(sv.size() + " SV SIZE");


            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return handleClickIcon(mapboxMap.getProjection().toScreenLocation(point));
    }

    /**
     * This method handles click events for SymbolLayer symbols.
     *
     * @param screenPoint the point on screen clicked
     */
    private boolean handleClickIcon(PointF screenPoint) {
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, LAYER_ID);
        if (!features.isEmpty()) {
            // Show the Feature in the TextView to show that the icon is based on the ICON_PROPERTY key/value
            TextView featureInfoTextView = findViewById(R.id.feature_info);
            //featureInfoTextView.setText(features.get(0).toJson());
            return true;
        } else {
            return false;
        }
    }

    private List<Feature> initCoordinateData() {
        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();


        for (StateVector s : sv) {
            System.out.println("SV =  " + s.getLatitude());
            double latitude = 0.0;
            double longitude = 0.0;
            String x = null;
            try {
                latitude = s.getLatitude();
                longitude = s.getLongitude();
                x = s.toString();
            } catch (Exception e) {
                System.out.println("CAUGHT : " + e.getMessage());
            }
            //System.out.println("PRINTS = " + x);
            symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(longitude, latitude)));
        }


        return symbolLayerIconFeatureList;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    private void getCoordinates() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
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
                    for(StateVector s : os.getStates()) {
                        sv.add(s);
                    }

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
                        e.printStackTrace();
                        System.out.println("OKHTTP : EXCEPTION");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()) {
                            String responseData = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                System.out.println(jsonObject);
                                JSONArray jsonArray = jsonObject.getJSONArray("states");
                                System.out.println(jsonArray.length());
                                System.out.println("jsonARRAY CONTENT : " + jsonArray.toString());
                            } catch (JSONException e) {
                                System.out.println("JSONARRAY EXCEPTION: === " + e.getMessage());
                                e.printStackTrace();
                            }
                            System.out.println(" RESPONSE : === " + response.body());
                        }
                    }
                });
            }

        });



        thread.start();
    }
}