package com.epicodus.shakeitup;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.epicodus.shakeitup.ui.RestaurantChooserFragment;

public class ChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        RestaurantChooserFragment chooser = RestaurantChooserFragment.newInstance();
        FragmentTransaction ft = ((FragmentActivity) this).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.chooser_content_layout, chooser);
        ft.commit();


    }
}
