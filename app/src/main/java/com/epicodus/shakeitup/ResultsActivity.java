package com.epicodus.shakeitup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.shakeitup.models.Business;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    private ArrayList<Business> mBusinesses = new ArrayList<>();
    private Map<String, Business> mMarkersBusinessesHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        mBusinesses = Parcels.unwrap(getIntent().getParcelableExtra("businesses"));

        //TODO remove placeholder images
        Picasso.with(this).load(R.drawable.tacos).fit().centerCrop().into(mFirstPlaceImageView);
        Picasso.with(this).load(R.drawable.tacos).fit().centerCrop().into(mSecondPlaceImageView);
        Picasso.with(this).load(R.drawable.tacos).fit().centerCrop().into(mThirdPlaceImageView);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Business business = mMarkersBusinessesHashMap.get(marker.getId());
                //TODO verify that other info windows close and that same adapter is not applied to all info windows
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View view = getLayoutInflater().inflate(R.layout.custom_info_window_contents_for_map, null);

                        ImageView badge = (ImageView) view.findViewById(R.id.badge);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView snippet = (TextView) view.findViewById(R.id.snippet);

                        Picasso.with(ResultsActivity.this).load(business.getImageUrl()).fit().centerCrop().into(badge);
                        title.setText(business.getName());
                        snippet.setText(business.getPhone());

                        return view;
                    }
                });
                return true;
            }
        });
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
        ArrayList<LatLng> latLngsForBounds = new ArrayList<>();
        for (int i = 0; i < mBusinesses.size(); i++) {
            Business business = mBusinesses.get(i);
            LatLng latLng = business.getLatlng();
            latLngsForBounds.add(latLng);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(business.getName()));
            mMarkersBusinessesHashMap.put(marker.getId(), business);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < latLngsForBounds.size(); i++) {
            builder.include(latLngsForBounds.get(i));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), MAP_PADDING));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    try {
                        mMap.setMyLocationEnabled(true);
                    } catch (SecurityException se) {
                        se.printStackTrace();
                    }
                } else {
                    // permission denied
                    Toast.makeText(this, "Directions functionality disabled", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}