package com.epicodus.shakeitup.models;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Guest on 4/11/16.
 */
public class ViewHolder {
    ImageView icon;
    TextView text;

    public ImageView getIcon() {
        return icon;
    }

    public TextView getText() {
        return text;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    public void setText(TextView text) {
        this.text = text;
    }
}
