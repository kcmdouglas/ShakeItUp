package com.epicodus.shakeitup.adapters;

/**
 * Created by Guest on 4/11/16.
 */
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.epicodus.shakeitup.models.Business;

import java.util.List;


/**
 * Created by Guest on 4/11/16.
 */
public class ItemBaseAdapter extends BaseAdapter {

    public Context context;
    public List<Business> list;

    public ItemBaseAdapter(Context c, List<Business> l){
        context = c;
        list = l;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Business> getList(){
        return list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }

}
