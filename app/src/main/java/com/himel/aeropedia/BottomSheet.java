package com.himel.aeropedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.himel.aeropedia.flightmap.FlightMap;

public class BottomSheet extends AppCompatActivity implements OnMapReadyCallback {

    private Button button;
    private GoogleMap mMap;
    LatLng latLng1, latLng2, latLng3, latLng4, latLng5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flight_map_bottom_sheet);
        button = findViewById(R.id.button);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                latLng1 = new LatLng(latLng1.latitude + 1, latLng1.longitude - 1);
                latLng2 = new LatLng(latLng2.latitude + 1, latLng2.longitude - 1);
                latLng3 = new LatLng(latLng3.latitude + 1, latLng3.longitude - 1);
                latLng4 = new LatLng(latLng4.latitude + 1, latLng4.longitude - 1);
                latLng5 = new LatLng(latLng5.latitude + 1, latLng5.longitude - 1);
                mMap.addMarker(new MarkerOptions().position(latLng1));
                mMap.addMarker(new MarkerOptions().position(latLng2));
                mMap.addMarker(new MarkerOptions().position(latLng3));
                mMap.addMarker(new MarkerOptions().position(latLng4));
                mMap.addMarker(new MarkerOptions().position(latLng5));
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(BottomSheet.this);

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(4f);

        latLng1 = new LatLng(45.5017, -73.5673);
        latLng2 = new LatLng(50.5017, -69.5673);
        latLng3 = new LatLng(55.5017, -65.5673);
        latLng4 = new LatLng(65.5017, -74.5673);
        latLng5 = new LatLng(60.5017, 45.5673);

        mMap.addMarker(new MarkerOptions().position(latLng1));
        mMap.addMarker(new MarkerOptions().position(latLng2));
        mMap.addMarker(new MarkerOptions().position(latLng3));
        mMap.addMarker(new MarkerOptions().position(latLng4));
        mMap.addMarker(new MarkerOptions().position(latLng5));


    }
}