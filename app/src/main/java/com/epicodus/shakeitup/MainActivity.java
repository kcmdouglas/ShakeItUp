package com.epicodus.shakeitup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.epicodus.shakeitup.models.Business;
import com.epicodus.shakeitup.services.YelpService;
import com.epicodus.shakeitup.ui.RestaurantChooserFragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ChooserActivity.class);
        startActivity(intent);

        runTest();

    }

    private void runTest() {
        final YelpService yelpService = new YelpService(this);

        yelpService.getYelpData("Portland", YelpService.DRINK, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                yelpService.processResults(response, YelpService.DRINK);

                yelpService.getYelpData(Business.getDrinkList().get(12).getAddress(), YelpService.DINNER, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        yelpService.processResults(response, YelpService.DINNER);
                        Log.d("TESTING", Business.getDrinkList().get(12).getAddress());
                        Log.d("TESTING", Business.getDinnerList().get(0).getAddress());
                    }
                });
            }
        });


    }
}