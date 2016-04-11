package com.epicodus.shakeitup.models;

import android.view.View;

import java.util.List;

/**
 * Created by Guest on 4/11/16.
 */
//objects passed in Drag and Drop operation
public class PassObject{
    View view;
    Item item;
    List<Item> srcList;

    public PassObject(View v, Item i, List<Item> s){
        view = v;
        item = i;
        srcList = s;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<Item> getSrcList() {
        return srcList;
    }

    public void setSrcList(List<Item> srcList) {
        this.srcList = srcList;
    }
}
