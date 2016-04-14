package com.epicodus.shakeitup;


import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.epicodus.shakeitup.models.Business;
import com.epicodus.shakeitup.services.YelpService;
import com.epicodus.shakeitup.ui.DinnerChooserFragment;
import com.epicodus.shakeitup.ui.DrinkChooserFragment;
import com.epicodus.shakeitup.ui.FunChooserFragment;

import org.parceler.Parcels;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.util.ArrayList;

public class ChooserActivity extends AppCompatActivity implements DrinkChooserFragment.OnFirstItemDroppedInDropZoneListener, DinnerChooserFragment.OnSecondItemDroppedInDropZone, FunChooserFragment.OnThirdItemDroppedInDropZone {

    private static final String TAG = ChooserActivity.class.getSimpleName();
    private DrinkChooserFragment drinkChooserFragment;
    public static ProgressDialog loadingDialog;

    ArrayList<Business> mDrinksArray = new ArrayList<>();
    ArrayList<Business> mDinnersArray = new ArrayList<>();
    ArrayList<Business> mFunArray = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        MainActivity.loadingDialog.hide();
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

    @Override
    public void onFirstItemDroppedInDropZone(Business item) {
        DinnerChooserFragment dinnerChooserFragment = DinnerChooserFragment.newInstance();
        Bundle args = new Bundle();
        args.putParcelable("drink", Parcels.wrap(item));
        dinnerChooserFragment.setArguments(args);
        loadingDialog.show(); //show progress dialog before make Dinner API request
        getPlaces(item, YelpService.DINNER, dinnerChooserFragment);
    }

    @Override
    public void onSecondItemDroppedInDropZone(Business drinkItem, Business dinnerItem) {
        FunChooserFragment funChooserFragment = FunChooserFragment.newInstance();
        Bundle args = new Bundle();
        args.putParcelable("drink", Parcels.wrap(drinkItem));
        args.putParcelable("dinner", Parcels.wrap(dinnerItem));
        funChooserFragment.setArguments(args);
        loadingDialog.show(); //show progress dialog before make Dinner API request
        getPlaces(dinnerItem, YelpService.FUN, funChooserFragment);
    }

    @Override
    public void onThirdItemDroppedInDropZone(Business firstItem, Business secondItem, Business thirdItem) {
        Toast.makeText(this, "Hurray!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("drink", Parcels.wrap(firstItem));
        intent.putExtra("dinner", Parcels.wrap(secondItem));
        intent.putExtra("fun", Parcels.wrap(thirdItem));
        startActivity(intent);

    }

    private void getPlaces(Business business, final String category, final Fragment chooserFragment) {
        final YelpService yelpService = new YelpService(this);
        yelpService.getYelpData(business.getAddress(), category, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if ((chooserFragment instanceof DinnerChooserFragment) || (chooserFragment instanceof FunChooserFragment)) {
                    yelpService.processResults(response, category);
                    getSupportFragmentManager().beginTransaction().replace(R.id.chooser_content_layout, chooserFragment).commit();
                } else {
                    Log.e(TAG, "Called fragment not instance of DinnerChooserFragment or FunChooserFragment");
                }

            }
        });
    }

    private void initializeProgressDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("loading...");
        loadingDialog.setMessage("Preparing data...");
        loadingDialog.setCancelable(false);
    }

    public void soundManager(String soundName) {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
        }

        if (soundName.equals("ice")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.ice_glass);
            mediaPlayer.start();
        } else if (soundName.equals("plate")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.plate);
            mediaPlayer.start();
        } else if (soundName.equals("guitar")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.guitar);
            mediaPlayer.start();
        }
    }
}
