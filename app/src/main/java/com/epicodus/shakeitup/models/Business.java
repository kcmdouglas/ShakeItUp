package com.epicodus.shakeitup.models;

import com.epicodus.shakeitup.services.YelpService;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Guest on 4/11/16.
 */
public class Business {
    private String rating;
    private String mobileUrl;
    private String reviewCount;
    private String name;
    private String phone;
    private String imageUrl;
    private LatLng latlng;
    private static ArrayList<Business> dinnerList = new ArrayList<>();
    private static ArrayList<Business> drinkList = new ArrayList<>();
    private static ArrayList<Business> funList = new ArrayList<>();

    public Business(String rating, String mobileUrl, String reviewCount, String name, String phone, String imageUrl, LatLng latlng) {
        this.rating = rating;
        this.mobileUrl = mobileUrl;
        this.reviewCount = reviewCount;
        this.name = name;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.latlng = latlng;
    }

    public String getRating() {
        return rating;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public static ArrayList<Business> getDinnerList() {
        return dinnerList;
    }

    public static ArrayList<Business> getDrinkList() {
        return drinkList;
    }

    public static ArrayList<Business> getFunList() {
        return funList;
    }

    public static void addBusiness (Business business, String category) {
        switch (category) {
            case YelpService.DINNER: dinnerList.add(business);
                break;
            case YelpService.DRINK: drinkList.add(business);
                break;
            case YelpService.FUN: funList.add(business);
                break;
        }
    }
}
