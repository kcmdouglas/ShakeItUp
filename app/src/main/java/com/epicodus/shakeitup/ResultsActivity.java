package com.epicodus.shakeitup;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity implements OnMapReadyCallback {
    @Bind(R.id.firstPlaceImageView) ImageView mFirstPlaceImageView;
    @Bind(R.id.secondPlaceImageView) ImageView mSecondPlaceImageView;
    @Bind(R.id.thirdPlaceImageView) ImageView mThirdPlaceImageView;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        Picasso.with(this).load(R.drawable.tacos).fit().centerCrop().into(mFirstPlaceImageView);
        Picasso.with(this).load(R.drawable.tacos).fit().centerCrop().into(mSecondPlaceImageView);
        Picasso.with(this).load(R.drawable.tacos).fit().centerCrop().into(mThirdPlaceImageView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
