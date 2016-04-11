package com.epicodus.shakeitup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.epicodus.shakeitup.models.Business;
import com.epicodus.shakeitup.services.YelpService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getBusinesses();
    }

    private void getBusinesses() {
        final YelpService yelpService = new YelpService(this);

        yelpService.getYelpData("Portland", YelpService.DINNER, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                yelpService.processResults(response, YelpService.DINNER);
                Log.d("MainActi", "Dinner: " + Integer.toString(Business.getDinnerList().size()));
            }
        });

        yelpService.getYelpData("Portland", YelpService.DRINK, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                yelpService.processResults(response, YelpService.DRINK);
                Log.d("MainActi",  "Drink: " + Integer.toString(Business.getDrinkList().size()));
            }
        });

        yelpService.getYelpData("Portland", YelpService.FUN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                yelpService.processResults(response, YelpService.FUN);
                Log.d("MainActi",  "Fun: " + Integer.toString(Business.getFunList().size()));
            }
        });

    }
}
