package com.epicodus.shakeitup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.shakeitup.models.Business;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    @Bind(R.id.drinkImageView) ImageView mDrinkImageView;
    @Bind(R.id.dinnerImageView) ImageView mDinnerImageView;
    @Bind(R.id.funImageView) ImageView mFunImageView;
    @Bind(R.id.drinkNameTextView) TextView mDrinkNameTextView;
    @Bind(R.id.dinnerNameTextView) TextView mDinnerNameTextView;
    @Bind(R.id.funNameTextView) TextView mFunNameTextView;

    @Bind(R.id.restartButton) Button mRestartButton;
    @Bind(R.id.shareButton) Button mShareButton;

    private GoogleMap mMap;
    private static final int MAP_PADDING = 85;
    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 411;

    private Business mDrink;
    private Business mDinner;
    private Business mFun;
    private ArrayList<Business> mBusinesses = new ArrayList<>();
    private Map<String, Business> mMarkersBusinessesHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        mDrink = Parcels.unwrap(getIntent().getParcelableExtra("drink"));
        mDinner = Parcels.unwrap(getIntent().getParcelableExtra("dinner"));
        mFun = Parcels.unwrap(getIntent().getParcelableExtra("fun"));
        mBusinesses.add(mDrink);
        mBusinesses.add(mDinner);
        mBusinesses.add(mFun);

        initializeCardImages();
        initializeCardText();

        mRestartButton.setOnClickListener(this);
        mShareButton.setOnClickListener(this);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //position the markers and camera separately from getting the map, to avoid the map flashing at 0,0 before moving camera
        if (mapFragment.getView() != null) {
            mapFragment.getView().post(new Runnable() {
                @Override
                public void run() {
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
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < mBusinesses.size(); i++) {
            Business business = mBusinesses.get(i);
            LatLng latLng = business.getLatlng();
            builder.include(latLng);
            switch (i) {
                case 0:
                    Marker drinksMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(180))
                            .title(business.getName()));
                    mMarkersBusinessesHashMap.put(drinksMarker.getId(), business);
                    break;
                case 1:
                    Marker dinnerMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(262))
                            .title(business.getName()));
                    mMarkersBusinessesHashMap.put(dinnerMarker.getId(), business);
                    break;
                case 2:
                    Marker funMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(47))
                            .title(business.getName()));
                    mMarkersBusinessesHashMap.put(funMarker.getId(), business);
                    break;
            }

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), MAP_PADDING));
        setMarkerClickListener();
    }

    public void initializeCardImages() {
        Picasso.with(this).load(mDrink.getImageUrl()).fit().centerCrop().into(mDrinkImageView);
        Picasso.with(this).load(mDinner.getImageUrl()).fit().centerCrop().into(mDinnerImageView);
        Picasso.with(this).load(mFun.getImageUrl()).fit().centerCrop().into(mFunImageView);
    }

    public void initializeCardText() {
        mDrinkNameTextView.setText(mDrink.getName());
        mDinnerNameTextView.setText(mDinner.getName());
        mFunNameTextView.setText(mFun.getName());
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

    public void setMarkerClickListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Business business = mMarkersBusinessesHashMap.get(marker.getId());
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        // don't want custom borders so return null
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View view = getLayoutInflater().inflate(R.layout.custom_info_window_contents_for_map, null);
                        ImageView badge = (ImageView) view.findViewById(R.id.badge);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView snippet = (TextView) view.findViewById(R.id.snippet);

                        Picasso.with(getApplicationContext())
                                .load(business.getImageUrl())
                                .resize(100, 100)
                                .into(badge, new MarkerImageCallback(marker));
                        title.setText(business.getName());
                        snippet.setText(business.getPhone());

                        return view;
                    }
                });

                float bearing = mMap.getCameraPosition().bearing;
                int zoom = (int)mMap.getCameraPosition().zoom;
                if ((bearing >= 0.0 && bearing < 45.0) || (bearing > 305.0 && bearing <= 360.0)) {
                    // i.e. if our map is rotated facing between NW and NE, towards North
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(business.getLatlng().latitude + (double)90/Math.pow(2, zoom), business.getLatlng().longitude), zoom);
                    mMap.animateCamera(cu);
                } else if (bearing > 45.0 && bearing < 135.0) {
                    // i.e. if our map is rotated facing between NE and SE, towards East
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(business.getLatlng().latitude, business.getLatlng().longitude + (double)90/Math.pow(2, zoom)), zoom);
                    mMap.animateCamera(cu);
                } else if (bearing > 135.0 && bearing < 225.0) {
                    // i.e. if our map is rotated facing between SE and SW, towards South
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(business.getLatlng().latitude - (double)90/Math.pow(2, zoom), business.getLatlng().longitude), zoom);
                    mMap.animateCamera(cu);
                } else {
                    // i.e. if our map is rotated facing between SW and NW, towards West
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(business.getLatlng().latitude, business.getLatlng().longitude - (double)90/Math.pow(2, zoom)), zoom);
                    mMap.animateCamera(cu);
                }
                marker.showInfoWindow();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restartButton:
                Intent restartIntent = new Intent(ResultsActivity.this, MainActivity.class);
                startActivity(restartIntent);
                break;
            case R.id.shareButton:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Let's go on a date to: \n" + mDrink.getName() + "\n" + mDinner.getName() + "\n" + mFun.getName());
                shareIntent.setType("text/plain");

                startActivity(Intent.createChooser(shareIntent, "How do you want to share?"));
                break;
        }
    }

    //need this callback to refresh the custom info window after Picasso has completed getting and loading the image
    public class MarkerImageCallback implements Callback {
        Marker marker = null;

        MarkerImageCallback(Marker marker) {
            this.marker = marker;
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
        }

        @Override
        public void onError() {
            Log.e(getClass().getSimpleName(), "Error loading info window image!");
        }
    }

}