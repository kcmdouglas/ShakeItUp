package com.epicodus.shakeitup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity implements OnMapReadyCallback {
    @Bind(R.id.firstPlaceImageView)
    ImageView mFirstPlaceImageView;
    @Bind(R.id.secondPlaceImageView)
    ImageView mSecondPlaceImageView;
    @Bind(R.id.thirdPlaceImageView)
    ImageView mThirdPlaceImageView;

    private GoogleMap mMap;
    private static final int MAP_PADDING = 50;
    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 411;

    private ArrayList<Double> mLatitudes = new ArrayList<>();
    private ArrayList<Double> mLongitudes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        //TODO remove placeholder latlngs
        mLatitudes.addAll(Arrays.asList(-12.1213214, -55.2334525, -22.242453));
        mLongitudes.addAll(Arrays.asList(-23.2453, 20.2323, -44.24142));

        //TODO remove placeholder images
        Picasso.with(this).load(R.drawable.tacos).fit().centerCrop().into(mFirstPlaceImageView);
        Picasso.with(this).load(R.drawable.tacos).fit().centerCrop().into(mSecondPlaceImageView);
        Picasso.with(this).load(R.drawable.tacos).fit().centerCrop().into(mThirdPlaceImageView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mapFragment.getView() != null) {
            mapFragment.getView().post(new Runnable() {
                @Override
                public void run() {
                    //position the markers and camera separately from getting the map, to avoid the map flashing at 0,0 before moving camera
                    initializeMapMarkers();
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(ResultsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_PERMISSION_REQUEST);
        }
    }

    public void initializeMapMarkers() {
        //TODO remove placeholder data, replace with real model data
        ArrayList<LatLng> latLngArrayList = new ArrayList<>();
        for (int i = 0; i < mLatitudes.size(); i++) {
            LatLng tacos = new LatLng(mLatitudes.get(i), mLongitudes.get(i));
            latLngArrayList.add(tacos);
            mMap.addMarker(new MarkerOptions()
                    .position(tacos)
                    .title("Tacos " + i));
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < latLngArrayList.size(); i++) {
            builder.include(latLngArrayList.get(i));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), MAP_PADDING));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    try {
                        mMap.setMyLocationEnabled(true);
                    } catch (SecurityException se) {
                        se.printStackTrace();
                    }
                } else {
                    // permission denied, boo!
                    Toast.makeText(this, "Directions functionality disabled", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}