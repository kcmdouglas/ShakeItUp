package com.epicodus.shakeitup.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.shakeitup.R;
import com.epicodus.shakeitup.models.Item;
import com.epicodus.shakeitup.models.ViewHolder;

import java.util.List;

/**
 * Created by Guest on 4/11/16.
 */
public class ItemListAdapter extends ItemBaseAdapter {

    public ItemListAdapter(Context c, List<Item> l) {
        super(c, l);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.row, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.setIcon((ImageView) rowView.findViewById(R.id.rowImageView));
            viewHolder.setText((TextView) rowView.findViewById(R.id.rowTextView));
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.getIcon().setImageDrawable(list.get(position).getItemDrawable());
        holder.getText().setText(list.get(position).getItemString());

        rowView.setOnDragListener(new ItemOnDragListener(this.context, list.get(position)));

        return rowView;
    }

}
