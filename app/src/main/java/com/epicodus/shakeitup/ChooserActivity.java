package com.epicodus.shakeitup;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.epicodus.shakeitup.models.Business;
import com.epicodus.shakeitup.services.YelpService;
import com.epicodus.shakeitup.ui.DrinkChooserFragment;
import com.epicodus.shakeitup.ui.FunChooserFragment;
import com.epicodus.shakeitup.ui.RestaurantChooserFragment;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcels;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.util.ArrayList;

public class ChooserActivity extends AppCompatActivity implements DrinkChooserFragment.OnFirstItemDroppedInDropZoneListener, RestaurantChooserFragment.OnSecondItemDroppedInDropZone, FunChooserFragment.OnThirdItemDroppedInDropZone {

    private static final String TAG = ChooserActivity.class.getSimpleName();
    private DrinkChooserFragment drinkChooserFragment;
    private static ProgressDialog loadingDialog;

    ArrayList<Business> mDrinksArray = new ArrayList<>();
    ArrayList<Business> mRestaurantsArray = new ArrayList<>();
    ArrayList<Business> mFunArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        initializeProgressDialog();

        if (savedInstanceState == null) {
            DrinkChooserFragment drinkChooserFragment = DrinkChooserFragment.newInstance();
            mDrinksArray = Business.getRandomDrink();
            Bundle args = new Bundle();
            args.putParcelable("drinksArray", Parcels.wrap(mDrinksArray));
            drinkChooserFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().add(R.id.chooser_content_layout, drinkChooserFragment).commit();
        }

    }

    private void initializeProgressDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("loading...");
        loadingDialog.setMessage("Preparing data...");
        loadingDialog.setCancelable(false);
    }

    @Override
    public void onFirstItemDroppedInDropZone(Business item) {
        RestaurantChooserFragment restaurantChooserFragment = RestaurantChooserFragment.newInstance();
        mRestaurantsArray = Business.getRandomDinner();
        Bundle args = new Bundle();
        args.putParcelable("drink", Parcels.wrap(item));
        args.putParcelable("restaurantsArray", Parcels.wrap(mRestaurantsArray));
        restaurantChooserFragment.setArguments(args);
        //TODO: add transition animation, here or in the fragment onCreateView?
        loadingDialog.show();  //show progress dialog before make Dinner API request
        getDinnerPlaces(Business.getDrinkList().get(0), restaurantChooserFragment);
    }

    @Override
    public void onSecondItemDroppedInDropZone(Business firstItem, Business secondItem) {
        FunChooserFragment funChooserFragment = FunChooserFragment.newInstance();
        Bundle args = new Bundle();
        args.putParcelable("drink", Parcels.wrap(firstItem));
        args.putParcelable("restaurant", Parcels.wrap(secondItem));
        args.putParcelable("funArray", Parcels.wrap(mFunArray));
        funChooserFragment.setArguments(args);
        //TODO: add transition animation
        getSupportFragmentManager().beginTransaction().replace(R.id.chooser_content_layout, funChooserFragment).commit();
    }

    @Override
    public void onThirdItemDroppedInDropZone(Business firstItem, Business secondItem, Business thirdItem) {
        //TODO: add transition animation
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("drink", Parcels.wrap(firstItem));
        intent.putExtra("restaurant", Parcels.wrap(secondItem));
        intent.putExtra("fun", Parcels.wrap(thirdItem));
        Toast.makeText(this, "Hurray!", Toast.LENGTH_LONG).show();
        startActivity(intent);

    }

    private void getDinnerPlaces(Business business, final Fragment chooserFragment) {
        final YelpService yelpService = new YelpService(this);
        yelpService.getYelpData("fake address", YelpService.DINNER, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if ((chooserFragment instanceof RestaurantChooserFragment) || (chooserFragment instanceof FunChooserFragment)) {
                    yelpService.processResults(response, YelpService.DINNER);
                    getSupportFragmentManager().beginTransaction().replace(R.id.chooser_content_layout, chooserFragment).commit();
                    loadingDialog.hide();
                } else {
                    Log.e(TAG, "Called fragment not instance of RestaurantChooserFragment or FunChooserFragment");
                }
            }
        });
    }
}
