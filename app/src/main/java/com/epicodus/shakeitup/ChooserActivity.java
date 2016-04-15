package com.epicodus.shakeitup;


import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
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
import java.util.Locale;

public class ChooserActivity extends AppCompatActivity implements DrinkChooserFragment.OnFirstItemDroppedInDropZoneListener, DinnerChooserFragment.OnSecondItemDroppedInDropZone, FunChooserFragment.OnThirdItemDroppedInDropZone {

    private static final String TAG = ChooserActivity.class.getSimpleName();
    private DrinkChooserFragment drinkChooserFragment;
    public static ProgressDialog loadingDialog;
    TextView shakeText;

    ArrayList<Business> mDrinksArray = new ArrayList<>();
    ArrayList<Business> mDinnersArray = new ArrayList<>();
    ArrayList<Business> mFunArray = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        shakeText = (TextView) findViewById(R.id.shakeToShuffle);

        Typeface journal = Typeface.createFromAsset(getAssets(), "fonts/journal.ttf");
        shakeText.setTypeface(journal);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setEnterTransition(new Fade().setDuration(400));
//        }

        if (savedInstanceState == null) {
            DrinkChooserFragment drinkChooserFragment = DrinkChooserFragment.newInstance();
            mDrinksArray = Business.getRandomDrink();
            Bundle args = new Bundle();
            args.putParcelable("drinksArray", Parcels.wrap(mDrinksArray));
            drinkChooserFragment.setArguments(args);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).
                add(R.id.chooser_content_layout, drinkChooserFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().add(R.id.chooser_content_layout, drinkChooserFragment).commit();

            }

            shakeText.animate().alpha(0).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    shakeText.setVisibility(View.GONE);
                }
            });


        }

    }

    @Override
    public void onFirstItemDroppedInDropZone(Business item) {
        DinnerChooserFragment dinnerChooserFragment = DinnerChooserFragment.newInstance();
        Bundle args = new Bundle();
        args.putParcelable("drink", Parcels.wrap(item));
        dinnerChooserFragment.setArguments(args);
//        initializeProgressDialog(item.getName() + "? I've heard good things!", "We need to find a good restaurant to go to next...");
//        loadingDialog.show(); //show progress dialog before make Dinner API request
        getPlaces(item, YelpService.DINNER, dinnerChooserFragment);
    }

    @Override
    public void onSecondItemDroppedInDropZone(Business drinkItem, Business dinnerItem) {
        FunChooserFragment funChooserFragment = FunChooserFragment.newInstance();
        Bundle args = new Bundle();
        args.putParcelable("drink", Parcels.wrap(drinkItem));
        args.putParcelable("dinner", Parcels.wrap(dinnerItem));
        funChooserFragment.setArguments(args);
//        initializeProgressDialog("I've heard great things about " + dinnerItem.getName() + "!", "Now let's plan an activity...");
//        loadingDialog.show(); //show progress dialog before make Dinner API request
        getPlaces(dinnerItem, YelpService.FUN, funChooserFragment);
    }

    @Override
    public void onThirdItemDroppedInDropZone(Business firstItem, Business secondItem, Business thirdItem) {

        initializeProgressDialog("This date is going to be so great!", "Let me put the whole package together...");
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("drink", Parcels.wrap(firstItem));
        intent.putExtra("dinner", Parcels.wrap(secondItem));
        intent.putExtra("fun", Parcels.wrap(thirdItem));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ChooserActivity.this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }

    private void getPlaces(Business business, final String category, final Fragment chooserFragment) {
        final YelpService yelpService = new YelpService(this);
        yelpService.getYelpData(business.getAddress(), category, YelpService.NORMAL_MODE, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if ((chooserFragment instanceof DinnerChooserFragment) || (chooserFragment instanceof FunChooserFragment)) {
                    yelpService.processResults(response, category);
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.chooser_content_layout, chooserFragment).commit();
                } else {
                    Log.e(TAG, "Called fragment not instance of DinnerChooserFragment or FunChooserFragment");
                }

            }
        });
    }

    private void initializeProgressDialog(String title, String message) {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle(title);
        loadingDialog.setMessage(message);
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
