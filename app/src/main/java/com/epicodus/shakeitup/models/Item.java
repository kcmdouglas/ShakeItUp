package com.epicodus.shakeitup.models;

import android.graphics.drawable.Drawable;

/**
 * Created by Guest on 4/11/16.
 */
//items stored in ListView
public class Item {
    Drawable ItemDrawable;
    String ItemString;

    public Item(Drawable drawable, String t){
        ItemDrawable = drawable;
        ItemString = t;
    }

    public String getItemString() {
        return ItemString;
    }

    public Drawable getItemDrawable() {
        return ItemDrawable;
    }
}
