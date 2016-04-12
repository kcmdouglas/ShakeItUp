package com.epicodus.shakeitup.models;

import android.view.View;

import java.util.List;

/**
 * Created by Guest on 4/11/16.
 */
//objects passed in Drag and Drop operation
public class PassObject{
    View view;
    Business item;
    List<Business> srcList;

    public PassObject(View v, Business i, List<Business> s){
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

    public Business getItem() {
        return item;
    }

    public void setItem(Business item) {
        this.item = item;
    }

    public List<Business> getSrcList() {
        return srcList;
    }

    public void setSrcList(List<Business> srcList) {
        this.srcList = srcList;
    }
}
