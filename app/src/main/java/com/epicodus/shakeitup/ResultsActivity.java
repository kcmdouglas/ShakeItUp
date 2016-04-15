package com.epicodus.shakeitup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
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
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener {
    @Bind(R.id.drinkImageView) ImageView mDrinkImageView;
    @Bind(R.id.dinnerImageView) ImageView mDinnerImageView;
    @Bind(R.id.funImageView) ImageView mFunImageView;
    @Bind(R.id.drinkNameTextView) TextView mDrinkNameTextView;
    @Bind(R.id.dinnerNameTextView) TextView mDinnerNameTextView;
    @Bind(R.id.funNameTextView) TextView mFunNameTextView;

    @Bind(R.id.drinkCardView) CardView mDrinkCardView;
    @Bind(R.id.dinnerCardView) CardView mDinnerCardView;
    @Bind(R.id.funCardView) CardView mFunCardView;

    @Bind(R.id.restartButton) Button mRestartButton;
    @Bind(R.id.shareButton) Button mShareButton;
    @Bind(R.id.directionsButton) Button mDirectionsButton;

    private GoogleMap mMap;
    private static final int MAP_PADDING = 85;
    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 411;

    private Business mDrink;
    private Business mDinner;
    private Business mFun;
    private ArrayList<Business> mBusinesses = new ArrayList<>();
    private Map<String, Business> mMarkersBusinessesHashMap = new HashMap<>();
    private boolean drinkDirectionsReceived = false;
    private boolean dinnerDirectionsReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide(Gravity.RIGHT).setStartDelay(400).setDuration(400));
        }

        mDrink = Parcels.unwrap(getIntent().getParcelableExtra("drink"));
        mDinner = Parcels.unwrap(getIntent().getParcelableExtra("dinner"));
        mFun = Parcels.unwrap(getIntent().getParcelableExtra("fun"));
        mBusinesses.add(mDrink);
        mBusinesses.add(mDinner);
        mBusinesses.add(mFun);

        initializeCardImages();
        initializeCardText();

        mDrinkCardView.setOnClickListener(this);
        mDinnerCardView.setOnClickListener(this);
        mFunCardView.setOnClickListener(this);

        mRestartButton.setOnClickListener(this);
        mShareButton.setOnClickListener(this);
        mDirectionsButton.setOnClickListener(this);

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
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
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
        mMap.setOnInfoWindowClickListener(this);
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
                            .icon(BitmapDescriptorFactory.defaultMarker(265))
                            .title(business.getName()));
                    mMarkersBusinessesHashMap.put(drinksMarker.getId(), business);
                    break;
                case 1:
                    Marker dinnerMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(141))
                            .title(business.getName()));
                    mMarkersBusinessesHashMap.put(dinnerMarker.getId(), business);
                    break;
                case 2:
                    Marker funMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(20))
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
        if (mDrink.getName().length() > 25) {
            mDrinkNameTextView.setText(String.format(Locale.US, getString(R.string.cutoff_name), mDrink.getName().substring(0, 24)));
        } else {
            mDrinkNameTextView.setText(mDrink.getName());
        }

        if (mDinner.getName().length() > 25) {
            mDinnerNameTextView.setText(String.format(Locale.US, getString(R.string.cutoff_name), mDinner.getName().substring(0, 24)));
        } else {
            mDinnerNameTextView.setText(mDinner.getName());
        }

        if (mFun.getName().length() > 25) {
            mFunNameTextView.setText(String.format(Locale.US, getString(R.string.cutoff_name), mFun.getName().substring(0, 24)));
        } else {
            mFunNameTextView.setText(mFun.getName());
        }
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
                        Log.d("Marker ID: ", marker.getId());

                        if (marker.getId().equals("m0")) {
                            snippet.setTextColor(Color.parseColor("#9A83BA"));
                        } else if (marker.getId().equals("m1")) {
                            snippet.setTextColor(Color.parseColor("#5ED186"));
                        } else if (marker.getId().equals("m2")) {
                            snippet.setTextColor(Color.parseColor("#F29D72"));
                        }

                        return view;
                    }
                });
                return false;
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Business business = mMarkersBusinessesHashMap.get(marker.getId());
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + business.getPhone()));
        startActivity(callIntent);
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
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Let's shake it up at: \n" + mDrink.getName() + "\n" + mDinner.getName() + "\n" + mFun.getName());
                shareIntent.setType("text/plain");

                startActivity(Intent.createChooser(shareIntent, "How do you want to share?"));
                break;
            case R.id.drinkCardView:
                Intent yelpDrinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDrink.getMobileUrl()));
                startActivity(yelpDrinkIntent);
                break;
            case R.id.dinnerCardView:
                Intent yelpDinnerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDinner.getMobileUrl()));
                startActivity(yelpDinnerIntent);
                break;
            case R.id.funCardView:
                Intent yelpFunIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mFun.getMobileUrl()));
                startActivity(yelpFunIntent);
                break;
            case R.id.directionsButton:
                //progressive navigation through each destination in turn
                if (!drinkDirectionsReceived) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + mDrink.getAddress()));
                    intent.setPackage("com.google.android.apps.maps");
                    drinkDirectionsReceived = true;
                    mDirectionsButton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorDinnerAccent));
                    startActivity(intent);
                } else if (!dinnerDirectionsReceived) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + mDinner.getAddress()));
                    intent.setPackage("com.google.android.apps.maps");
                    dinnerDirectionsReceived = true;
                    mDirectionsButton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorFunAccent));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + mFun.getAddress()));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
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