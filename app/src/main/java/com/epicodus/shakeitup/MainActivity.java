package com.epicodus.shakeitup;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
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
import android.widget.RelativeLayout;
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
//    @Bind(R.id.backgroundImageView2) ImageView backgroundImageView2;
    @Bind(R.id.jumbotron) RelativeLayout jumbotron;
    @Bind(R.id.titleTextView) TextView mTitleTextView;
    public static ProgressDialog loadingDialog;
    public static GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;
    public static String mCurrentLocation;
    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 411;
    private SharedPreferences mSharedPreferences;
    private String mFormattedAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeProgressDialog();
        backgroundImageView.setAlpha(1.0f);
//        backgroundImageView2.setAlpha(0.0f);
        initializeUnsplashBackground();

//        TODO: hide keyboard on

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Typeface journal = Typeface.createFromAsset(getAssets(), "fonts/journal.ttf");
        mTitleTextView.setTypeface(journal);

        shakeButton.setOnClickListener(this);

       }

    private void getDrinkPlaces(final String location) {
        getData(location);
    }

    public void getData (final String location) {
        final YelpService yelpService = new YelpService(this);

        yelpService.getYelpData(location, YelpService.DRINK, YelpService.NORMAL_MODE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (yelpService.processResults(response, YelpService.DRINK)) {
                    if (Business.getDrinkList().size() < 3) {
                        getDataExpanded(location);
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.hide();
                                Intent intent = new Intent(MainActivity.this, ChooserActivity.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                                    startActivity(intent, options.toBundle());
                                } else {
                                    startActivity(intent);
                                }
                            }
                        });
                    }
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

    public void getDataExpanded (final String location) {
        final YelpService yelpService = new YelpService(this);

        yelpService.getYelpData(location, YelpService.DRINK, YelpService.EXPANDED_MODE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (yelpService.processResults(response, YelpService.DRINK)) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.hide();
                            Intent intent = new Intent(MainActivity.this, ChooserActivity.class);
                            Log.d(TAG, Business.getDrinkList().size()+" AMOUNT OF DRINK PLACES");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                                startActivity(intent, options.toBundle());
                            } else {
                                startActivity(intent);
                            }
                        }
                    });
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
        loadingDialog.setTitle("Hold on...");
        loadingDialog.setMessage("We're finding some great places for you...");
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
                final String mBackgroundImgUrl = unsplashService.getImageUrl();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBackgroundImgUrl != null) {
                            final ImageView backgoundImageToShow;
                            final ImageView backgoundImageToHide;
//                            if (backgroundImageView.getAlpha() != 0.0f) {
//                                backgoundImageToShow = backgroundImageView;
//                                backgoundImageToHide = backgroundImageView2;
//                            } else {
//                                backgoundImageToShow = backgroundImageView2;
//                                backgoundImageToHide = backgroundImageView;
//                            }
                            backgoundImageToShow = backgroundImageView;
                            Picasso.with(MainActivity.this)
                                    .load(mBackgroundImgUrl)
                                    .resize(400, 400)
                                    .centerCrop()
                                    .into(backgoundImageToShow, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {

                                            Log.d(TAG, "Image = " + backgroundImageView.getAlpha());

//                                            backgoundImageToShow.setVisibility(View.VISIBLE);
//                                            backgoundImageToHide.setVisibility(View.INVISIBLE);
                                            backgoundImageToShow.setAlpha(1.0f);
                                            imageAnimationShow(backgoundImageToShow, unsplashService.getColor());
//                                            imageAnimationHide(backgoundImageToHide, backgoundImageToShow, unsplashService.getColor());
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

    public void imageAnimationShow (final ImageView imageViewShow, final String color) {
//        imageViewHide.setVisibility(View.INVISIBLE);
//        imageViewShow.setVisibility(View.VISIBLE);

        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setStartOffset(0);
        fadeIn.setDuration(800);

        AnimationSet animationShow = new AnimationSet(false);
        animationShow.addAnimation(fadeIn);
        imageViewShow.startAnimation(animationShow);
        animationShow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                imageViewShow.setVisibility(View.VISIBLE);
                Log.d(TAG, "FADE IN starts     " + imageViewShow.getVisibility());
                if (color != null) {
                    StringBuilder str = new StringBuilder(color);
                    str.insert(1, "79");
                    jumbotron.setBackgroundColor(Color.parseColor(str.toString()));
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "FADE IN ends");
//                imageViewShow.setAlpha(1.0f);
                imageAnimationHide(imageViewShow);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void imageAnimationHide (final ImageView imageViewHide) {
        Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(15000);
        fadeOut.setDuration(2000);


        AnimationSet animationHide = new AnimationSet(false);
        animationHide.addAnimation(fadeOut);
        imageViewHide.startAnimation(animationHide);
        animationHide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "FADE OUT starts    " + imageViewHide.getVisibility());

//                                                    backgoundImageToShow.setAlpha(1.0f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                initializeUnsplashBackground();
                Log.d(TAG, "FADE OUT ends");
                imageViewHide.setAlpha(0.0f);
//                imageViewHide.setVisibility(View.INVISIBLE);

//                                                try {
//                                                    Thread.sleep(90000);
//                                                } catch (InterruptedException e) {
//                                                    e.printStackTrace();
//                                                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

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