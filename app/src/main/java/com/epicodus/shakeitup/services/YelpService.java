package com.epicodus.shakeitup.services;

import android.content.Context;
import android.util.Log;
import android.widget.Switch;

import com.epicodus.shakeitup.R;
import com.epicodus.shakeitup.models.Business;
import com.google.android.gms.maps.model.LatLng;

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

public class YelpService {
    private static final String TAG = YelpService.class.getSimpleName();
    public static final String DINNER = "restaurant";
    public static final String DRINK = "drinks";
    public static final String FUN = "entertainment";
    private Context mContext;

    public YelpService(Context context) {
        this.mContext = context;
    }

    public void getYelpData(String location, String category, Callback callback) {
        final String CONSUMER_KEY = mContext.getString(R.string.consumer_key);
        final String CONSUMER_SECRET = mContext.getString(R.string.consumer_secret);
        final String TOKEN = mContext.getString(R.string.token);
        final String TOKEN_SECRET = mContext.getString(R.string.token_secret);
        String radius = "";
        switch (category) {
            case DINNER:
                radius = "1600";
                break;
            case DRINK:
                radius = "8000";
                break;
            case FUN:
                radius = "10000";
                break;
        }
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.yelp.com/v2/search?&term=" + category).newBuilder();
        urlBuilder.addQueryParameter("location", location);
        urlBuilder.addQueryParameter("radius_filter", radius);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    //TODO: make it more awesome!
    public boolean processResults (Response response, String category) {
        try {
            String jsonData = response.body().string();
            Business.clearData(category);
            if (response.isSuccessful()) {
                JSONObject yelpJSON = new JSONObject(jsonData);
                JSONArray businessesListJSON = yelpJSON.getJSONArray("businesses");
                for (int i=0; i<businessesListJSON.length(); i++) {
                    JSONObject businessJSON = businessesListJSON.getJSONObject(i);
                    String rating = businessJSON.getString("rating");
                    String mobileUrl = businessJSON.getString("mobile_url");
                    String reviewCount = businessJSON.getString("review_count");
                    String name = businessJSON.getString("name");
                    JSONArray arrayOfCategories = businessJSON.getJSONArray("categories");
                    String yelpCategory = "";
                    for (int z = 0; z < arrayOfCategories.length(); z++) {
                        JSONArray categoriesArray = arrayOfCategories.getJSONArray(z);
                        yelpCategory += categoriesArray.getString(0);
                        Log.d(TAG, yelpCategory);
                    }
                    String phone = "";
                    try {
                        phone = businessJSON.getString("display_phone");
                    } catch (JSONException jsone) {
                        jsone.printStackTrace();
                        Log.e(TAG, category);
                    }
                    String snippetText = businessJSON.getString("snippet_text");
                    String imageUrl = businessJSON.getString("image_url");
                    JSONObject locationJSON = businessJSON.getJSONObject("location");
                    JSONArray addressArray = locationJSON.getJSONArray("display_address");
                    String address = "";
                    for (int y = 0; y < addressArray.length(); y++) {
                        address += addressArray.getString(y) + " ";
                    }
                    JSONObject coordinateJSON = locationJSON.getJSONObject("coordinate");
                    Double latitude = coordinateJSON.getDouble("latitude");
                    Double longitude = coordinateJSON.getDouble("longitude");
                    LatLng latLng = new LatLng(latitude, longitude);

                    Business.addBusiness(new Business(rating, mobileUrl, reviewCount, name, phone, imageUrl, latLng, address, yelpCategory, snippetText), category);
                }
                return true;
            } else {
                Log.d(TAG, response.toString());
                return false;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
        return false;
    }

}
