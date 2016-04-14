package com.epicodus.shakeitup.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.epicodus.shakeitup.R;
import com.epicodus.shakeitup.models.Business;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * Created by Guest on 4/13/16.
 */
public class GeolocationService {
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    public String currentCity;

    public GeolocationService(Context context) {
        this.mContext = context;
    }

    public void getCurrentAddress(String latLng, Callback callback) {
        final String API_KEY = mContext.getString(R.string.google_maps_key);
        mSharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng).newBuilder();
//        urlBuilder.addQueryParameter("key", API_KEY);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void processResults(Response response) {
        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject geolocationJSON = new JSONObject(jsonData);
                JSONArray results = geolocationJSON.getJSONArray("results");
                JSONObject resultsObject = results.getJSONObject(0);
                String address = resultsObject.getString("formatted_address");
                JSONObject components = results.getJSONObject(1);
                JSONArray addressComponents = components.getJSONArray("address_components");
                JSONObject stateObject = addressComponents.getJSONObject(2);
                String stateName = stateObject.getString("short_name");
                addToSharedPreferences(address);
                currentCity = stateName;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
    }

    private void addToSharedPreferences(String location) {
        mEditor.putString("location", location).commit();
    }

    public String getCurrentCity() {
        return currentCity;
    }

}
