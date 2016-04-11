package com.epicodus.shakeitup.services;

import android.content.Context;

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

public class YelpService {
    public static final String DINNER = "dinner+restaurant";
    public static final String DRINK = "Cocktail+Bars";
    public static final String FUN = "Arts+Entertainment";
    private Context mContext;

    public YelpService(Context context) {
        this.mContext = context;
    }

    public void getYelpData(String location, String category, Callback callback) {
        final String CONSUMER_KEY = mContext.getString(R.string.consumer_key);
        final String CONSUMER_SECRET = mContext.getString(R.string.consumer_secret);
        final String TOKEN = mContext.getString(R.string.token);
        final String TOKEN_SECRET = mContext.getString(R.string.token_secret);
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        consumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.yelp.com/v2/search?term=" + category).newBuilder();
        urlBuilder.addQueryParameter("location", location);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void processResults (Response response, String category) {
        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject yelpJSON = new JSONObject(jsonData);
                JSONArray businessesJSON = yelpJSON.getJSONArray("businesses");
                for (int i=0; i<businessesJSON.length(); i++) {
                    JSONObject businessJSON = businessesJSON.getJSONObject(i);
                    String rating = businessJSON.getString("rating");
                    String mobileUrl = businessJSON.getString("mobile_url");
                    String reviewCount = businessJSON.getString("review_count");
                    String name = businessJSON.getString("name");
                    String phone = businessJSON.getString("display_phone");
                    String imageURL = businessJSON.getString("image_url");

                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
    }

}
