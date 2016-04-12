package com.epicodus.shakeitup;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.epicodus.shakeitup.models.Item;
import com.epicodus.shakeitup.ui.DrinkChooserFragment;
import com.epicodus.shakeitup.ui.FunChooserFragment;
import com.epicodus.shakeitup.ui.RestaurantChooserFragment;

import org.parceler.Parcels;

public class ChooserActivity extends AppCompatActivity implements DrinkChooserFragment.OnFirstItemDroppedInDropZoneListener, RestaurantChooserFragment.OnSecondItemDroppedInDropZone, FunChooserFragment.OnThirdItemDroppedInDropZone {

    private DrinkChooserFragment drinkChooserFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.chooser_content_layout, new DrinkChooserFragment()).commit();
        }

    }

    @Override
    public void onFirstItemDroppedInDropZone(Item item) {
        RestaurantChooserFragment restaurantChooserFragment = RestaurantChooserFragment.newInstance();
        Bundle args = new Bundle();
        args.putParcelable("drink", Parcels.wrap(item));
        restaurantChooserFragment.setArguments(args);
        //TODO: add transition animation, here or in the fragment onCreateView?
        getSupportFragmentManager().beginTransaction().replace(R.id.chooser_content_layout, restaurantChooserFragment).commit();

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
}
