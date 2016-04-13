package com.epicodus.shakeitup.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.shakeitup.R;
import com.epicodus.shakeitup.models.Business;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Guest on 4/11/16.
 */
public class ItemListAdapter extends ItemBaseAdapter {

    public ItemListAdapter(Context c, List<Business> l) {
        super(c, l);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.row, null);

            ListViewHolder listViewHolder = new ListViewHolder();
            Picasso.with(context).load(list.get(position).getImageUrl()).fit().centerCrop().into((ImageView) rowView.findViewById(R.id.rowImageView));
            listViewHolder.setText((TextView) rowView.findViewById(R.id.rowTextView));

            rowView.setTag(listViewHolder);
        }

        ListViewHolder holder = (ListViewHolder) rowView.getTag();
        Picasso.with(context).load(list.get(position).getImageUrl()).fit().centerCrop().into((ImageView) rowView.findViewById(R.id.rowImageView));
        holder.getText().setText(list.get(position).getName());

        rowView.setOnDragListener(new ItemOnDragListener(this.context, list.get(position)));

        return rowView;
    }

}
