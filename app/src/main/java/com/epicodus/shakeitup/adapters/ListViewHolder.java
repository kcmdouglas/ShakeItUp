package com.epicodus.shakeitup.adapters;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Guest on 4/11/16.
 */
public class ListViewHolder {
    String imageUrl;
    TextView text;
    TextView category;
    TextView snippet;
    TextView rating;

    public TextView getRating() {
        return rating;
    }

    public void setRating(TextView rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public TextView getSnippet() {
        return snippet;
    }

    public void setSnippet(TextView snippet) {
        this.snippet = snippet;
    }

    public TextView getCategory() {
        return category;
    }


    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }

    public void setCategory(TextView category) {
        this.category = category;
    }
}


