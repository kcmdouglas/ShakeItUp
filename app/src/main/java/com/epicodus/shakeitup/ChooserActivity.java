package com.epicodus.shakeitup;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.epicodus.shakeitup.models.Business;
import com.epicodus.shakeitup.models.Item;
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

public class ChooserActivity extends AppCompatActivity implements DrinkChooserFragment.OnFirstItemDroppedInDropZoneListener, RestaurantChooserFragment.OnSecondItemDroppedInDropZone, FunChooserFragment.OnThirdItemDroppedInDropZone {

    private static final String TAG = ChooserActivity.class.getSimpleName();
    private DrinkChooserFragment drinkChooserFragment;
    private static ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        initializeProgressDialog();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.chooser_content_layout, new DrinkChooserFragment()).commit();
        }

    }

    private void initializeProgressDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("loading...");
        loadingDialog.setMessage("Preparing data...");
        loadingDialog.setCancelable(false);
    }

    @Override
    public void onFirstItemDroppedInDropZone(Item item) {
        RestaurantChooserFragment restaurantChooserFragment = RestaurantChooserFragment.newInstance();
        Bundle args = new Bundle();
        args.putParcelable("drink", Parcels.wrap(item));
        restaurantChooserFragment.setArguments(args);
        //TODO: add transition animation, here or in the fragment onCreateView?
        loadingDialog.show();  //show progress dialog before make Dinner API request
        getDinnerPlaces(Business.getDrinkList().get(0), restaurantChooserFragment);
    }

    @Override
    public void onSecondItemDroppedInDropZone(Item firstItem, Item secondItem) {
        FunChooserFragment funChooserFragment = FunChooserFragment.newInstance();
        Bundle args = new Bundle();
        args.putParcelable("drink", Parcels.wrap(firstItem));
        args.putParcelable("restaurant", Parcels.wrap(secondItem));
        funChooserFragment.setArguments(args);
        //TODO: add transition animation
        getSupportFragmentManager().beginTransaction().replace(R.id.chooser_content_layout, funChooserFragment).commit();
    }

    @Override
    public void onThirdItemDroppedInDropZone(Item firstItem, Item secondItem, Item thirdItem) {
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
