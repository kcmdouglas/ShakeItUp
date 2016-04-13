package com.epicodus.shakeitup.adapters;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Guest on 4/11/16.
 */
public class ListViewHolder {
    String imageUrl;
    TextView text;

    public String getIcon() {
        return imageUrl;
    }

    public TextView getText() {
        return text;
    }

    public void setIcon(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setText(TextView text) {
        this.text = text;
    }
}


