package com.epicodus.shakeitup.models;

import android.util.Log;

import com.epicodus.shakeitup.services.YelpService;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Guest on 4/11/16.
 */
@Parcel
public class Business {
    String rating;
    String mobileUrl;
    String reviewCount;
    String name;
    String phone;
    String imageUrl;
    LatLng latlng;
    static ArrayList<Business> dinnerList = new ArrayList<>();
    static ArrayList<Business> drinkList = new ArrayList<>();
    static ArrayList<Business> funList = new ArrayList<>();

    public Business() {
    }

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

    public static ArrayList<Business> getRandomDinner() {
        return getRandomBusinesses(dinnerList);
    }

    public static ArrayList<Business> getRandomDrink() {
        return getRandomBusinesses(drinkList);
    }

    public static ArrayList<Business> getRandomFun() {
        return getRandomBusinesses(funList);
    }

    private static ArrayList<Business> getRandomBusinesses(ArrayList<Business> arrayList) {
        ArrayList<Business> randomBusinesses = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            int randomIndex = -1;
            boolean notRepeated = true;
            while (notRepeated) {
                notRepeated = false;
                randomIndex = (int)((Math.random() * (getDinnerList().size() - 1)) + 1);
                for(int y = 0; y < randomBusinesses.size(); y++) {
                    if (arrayList.get(randomIndex).equals(randomBusinesses.get(y))) {
                        notRepeated = true;
                        break;
                    }
                }
            }
            randomBusinesses.add(arrayList.get(randomIndex));
        }
        return randomBusinesses;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Business)) {
            return false;
        } else {
            Business otherObject = (Business) object;
            return this.getName().equals(otherObject.getName()) && this.getPhone().equals(otherObject.getPhone()) && this.getImageUrl().equals(otherObject.getImageUrl());
        }
    }
}
