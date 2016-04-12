package com.epicodus.shakeitup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.epicodus.shakeitup.models.Business;
import com.epicodus.shakeitup.services.YelpService;
import com.epicodus.shakeitup.ui.RestaurantChooserFragment;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.shakeButton) Button shakeButton;
    @Bind(R.id.locationTextView) TextView locationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        shakeButton.setOnClickListener(this);

    }

    private void getDrinkPlaces(String location) {
        final YelpService yelpService = new YelpService(this);

        yelpService.getYelpData(location, YelpService.DRINK, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                yelpService.processResults(response, YelpService.DRINK);
                Intent intent = new Intent(MainActivity.this, ChooserActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onClick(View v) {
        getDrinkPlaces(locationLabel.getText().toString());
    }
}