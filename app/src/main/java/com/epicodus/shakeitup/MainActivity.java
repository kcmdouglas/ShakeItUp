package com.epicodus.shakeitup;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.shakeitup.models.Business;
import com.epicodus.shakeitup.services.GeolocationService;
import com.epicodus.shakeitup.services.UnsplashService;
import com.epicodus.shakeitup.services.YelpService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.shakeButton) Button shakeButton;
    @Bind(R.id.locationTextView) TextView locationLabel;
    @Bind(R.id.backgroundImageView) ImageView backgroundImageView;
    @Bind(R.id.backgroundImageView2) ImageView backgroundImageView2;
    public static ProgressDialog loadingDialog;
    public static GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;
    public static String mCurrentLocation;
    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 411;
    private SharedPreferences mSharedPreferences;
    private String mFormattedAddress;
    private String mBackgroundImgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeProgressDialog();
        initializeUnsplashBackground();

//        TODO: hide keyboard on

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        shakeButton.setOnClickListener(this);

       }

    private void getDrinkPlaces(final String location) {
        final YelpService yelpService = new YelpService(this);

        yelpService.getYelpData(location, YelpService.DRINK, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (yelpService.processResults(response, YelpService.DRINK)) {
                    Intent intent = new Intent(MainActivity.this, ChooserActivity.class);
                    startActivity(intent);
                } else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.hide();
                            Toast.makeText(MainActivity.this, "Oops, that address doesn't work!", Toast.LENGTH_LONG).show();
                            locationLabel.setText("");
                            locationLabel.setHint("Please try again!");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onClick(View v) {
//        Hiding keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        loadingDialog.show();
        Log.d(TAG, locationLabel.getText().length() + "");
        if (locationLabel.getText().length() == 0) {
            getDrinkForCurrentLocation();
        } else {
            getDrinkPlaces(locationLabel.getText().toString());
        }
    }

    private void getDrinkForCurrentLocation() {
        final GeolocationService geolocationService = new GeolocationService(this);

        geolocationService.getCurrentAddress(mCurrentLocation, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                geolocationService.processResults(response);
                mSharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                mFormattedAddress = mSharedPreferences.getString("location", null);
                getDrinkPlaces(mFormattedAddress);
            }
        });
    }

    private void initializeProgressDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("loading...");
        loadingDialog.setMessage("Preparing data...");
        loadingDialog.setCancelable(false);
    }

    private void initializeUnsplashBackground() {
        final UnsplashService unsplashService = new UnsplashService(this);

        unsplashService.getUnsplashData(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                unsplashService.processResults(response);
                mBackgroundImgUrl = unsplashService.getImageUrl();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBackgroundImgUrl != null) {
                            Picasso.with(MainActivity.this)
                                    .load(mBackgroundImgUrl)
                                    .resize(400, 400)
                                    .centerCrop()
                                    .into(backgroundImageView, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {

                                            Animation fadeOut = new AlphaAnimation(1, 0);
                                            fadeOut.setInterpolator(new AccelerateInterpolator());
                                            fadeOut.setStartOffset(1000);
                                            fadeOut.setDuration(1000);

                                            AnimationSet animation = new AnimationSet(false);
                                            animation.addAnimation(fadeOut);
                                            backgroundImageView2.setAnimation(animation);
                                            animation.setAnimationListener(new Animation.AnimationListener() {
                                                @Override
                                                public void onAnimationStart(Animation animation) {
                                                }

                                                @Override
                                                public void onAnimationEnd(Animation animation) {
                                                    backgroundImageView2.setImageAlpha(0);
                                                }

                                                @Override
                                                public void onAnimationRepeat(Animation animation) {

                                                }
                                            });
//
                                        }

                                        @Override
                                        public void onError() {
                                            Log.e(TAG, "Picasso image loading has failed");
                                        }
                                    });
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mCurrentLocation = (mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted

                    try {

                    } catch (SecurityException se) {
                        se.printStackTrace();
                    }
                } else {
                    // permission denied
                    Toast.makeText(this, "Current location search disabled", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}