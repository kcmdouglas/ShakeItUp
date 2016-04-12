package com.epicodus.shakeitup.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.epicodus.shakeitup.R;
import com.epicodus.shakeitup.models.Business;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Guest on 4/11/16.
 */
public class ItemGridAdapter extends ItemBaseAdapter {

    public ItemGridAdapter(Context c, List<Business> l) {
        super(c, l);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridrowView = convertView;

        // reuse views
        if (gridrowView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            gridrowView = inflater.inflate(R.layout.gridrow, null);

            GridViewHolder gridviewHolder = new GridViewHolder();
            Picasso.with(context).load(list.get(position).getImageUrl()).fit().centerCrop().into((ImageView) gridrowView.findViewById(R.id.gridrowImageView));

            gridrowView.setTag(gridviewHolder);
        }

        GridViewHolder holder = (GridViewHolder) gridrowView.getTag();
        Picasso.with(context).load(list.get(position).getImageUrl()).fit().centerCrop().into((ImageView) gridrowView.findViewById(R.id.gridrowImageView));

        gridrowView.setOnDragListener(new ItemOnDragListener(this.context, list.get(position)));

        return gridrowView;
    }

}
