package com.epicodus.shakeitup.services;

import android.content.Context;
import android.util.Log;

import com.epicodus.shakeitup.Constants;
import com.epicodus.shakeitup.R;

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

/**
 * Created by Guest on 4/13/16.
 */
public class UnsplashService {
    private static final String TAG = UnsplashService.class.getSimpleName();
    private Context mContext;
    public static String imageUrl;
    private String color;

    public UnsplashService(Context context) {
        this.mContext = context;
    }

    public void getUnsplashData(Callback callback) {
        long randomNumber = Math.round(Math.random());
        Log.d(TAG, "unsplash random number " + randomNumber);
        String currentKey = Constants.UNSPLASH_KEY;

        if (randomNumber == 0) {
            currentKey = mContext.getString(R.string.unsplash_id_one);
        } else {
            currentKey = mContext.getString(R.string.unsplash_id_two);
        }

        final String APPLICATION_ID = currentKey;

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.unsplash.com/photos/random?&query=night").newBuilder();
        urlBuilder.addQueryParameter("client_id", APPLICATION_ID);
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
            Log.d(TAG, response.toString());
            if (response.isSuccessful()) {
                JSONObject bodyObject = new JSONObject(jsonData);
                JSONObject urlsObject = bodyObject.getJSONObject("urls");
                String regularImgUrl = urlsObject.getString("regular");
                color = bodyObject.getString("color");
                imageUrl = regularImgUrl;
                Log.d("unsplashlog", response.toString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getColor() {
        return color;
    }
}
