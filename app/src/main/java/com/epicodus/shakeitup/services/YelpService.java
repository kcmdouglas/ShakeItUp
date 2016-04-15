package com.epicodus.shakeitup.services;

import android.content.Context;
import android.util.Log;
import android.widget.Switch;

import com.epicodus.shakeitup.Constants;
import com.epicodus.shakeitup.R;
import com.epicodus.shakeitup.models.Business;
import com.google.android.gms.common.api.BooleanResult;
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
    public static final Boolean NORMAL_MODE = true;
    public static final Boolean EXPANDED_MODE = false;
    private Context mContext;

    public YelpService(Context context) {
        this.mContext = context;
    }

    public void getYelpData(String location, String category, Boolean mode, Callback callback) {
        final String CONSUMER_KEY = Constants.YELP_CONSUMER_KEY;
        final String CONSUMER_SECRET = Constants.YELP_CONSUMER_SECRET;
        final String TOKEN = Constants.YELP_TOKEN;
        final String TOKEN_SECRET = Constants.YELP_TOKEN_SECRET;
        String radius = "";
        if (mode) {
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
        } else {
            switch (category) {
                case DINNER: radius = "1600";
                    break;
                case DRINK: radius = "40000";
                    break;
                case FUN: radius = "10000";
                    break;
            }
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
                    int iterator = 0;
                    if (arrayOfCategories.length() < 2) {
                        iterator = arrayOfCategories.length();
                    } else {
                        iterator = 2;
                    }
                    for (int z = 0; z < iterator; z++) {
                        JSONArray categoriesArray = arrayOfCategories.getJSONArray(z);
                        yelpCategory += categoriesArray.getString(0) + ", ";
                    }
                    yelpCategory = yelpCategory.substring(0, yelpCategory.length() - 2);
                    String phone = "";
                    try {
                        phone = businessJSON.getString("display_phone");
                    } catch (JSONException jsone) {
                        jsone.printStackTrace();
                    }
                    String imageUrl="";
                    String snippetText = businessJSON.getString("snippet_text");
                    try {
                        imageUrl = businessJSON.getString("image_url");
                    } catch (JSONException jsone) {
                        jsone.printStackTrace();
                        imageUrl="https://images.unsplash.com/photo-1452415005154-c06158558480?ixlib=rb-0.3.5\\u0026q=80\\u0026fm=jpg\\u0026crop=entropy\\u0026w=200\\u0026fit=max\\u0026s=3805a9eb7264e9ef610f4200ddf0352d";
                    }
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
