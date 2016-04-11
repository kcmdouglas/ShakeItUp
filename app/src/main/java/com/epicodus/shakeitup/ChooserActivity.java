package com.epicodus.shakeitup;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.epicodus.shakeitup.models.Item;
import com.epicodus.shakeitup.ui.RestaurantChooserFragment;

public class ChooserActivity extends AppCompatActivity implements RestaurantChooserFragment.OnFirstItemDroppedInDropZoneListener {

    private RestaurantChooserFragment restaurantChooserFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.chooser_content_layout, new RestaurantChooserFragment()).commit();
        }

    }

    @Override
    public void onFirstItemDroppedInDropZone(Item item) {
        //TODO: Push item into array then pass to the next fragment
        //TODO: Remove ability to drag items out of the dropzone.
        Toast.makeText(ChooserActivity.this, item.getItemString() + "", Toast.LENGTH_SHORT).show();
    }
}
