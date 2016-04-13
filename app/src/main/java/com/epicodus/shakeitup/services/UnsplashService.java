package com.epicodus.shakeitup.services;

import android.content.Context;

import com.epicodus.shakeitup.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Guest on 4/12/16.
 */
public class UnsplashService {
    private final static String DEFAULT = "drinks";
    private final Context mContext;

    public UnsplashService(Context context) {
        this.mContext = context;
    }

    public void getUnsplashData(String query, Callback callback) {
        final String API_KEY = mContext.getString(R.string.unsplash_api);

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.unsplash.com/photos/random/?client_id=" + API_KEY + "&w=1000&h=1000").newBuilder();
        urlBuilder.addQueryParameter("queary", query);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
        .url(url)
                .build();

    }
}
