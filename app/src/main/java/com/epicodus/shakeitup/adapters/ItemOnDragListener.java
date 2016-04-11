package com.epicodus.shakeitup.adapters;

import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.widget.AbsListView;

import com.epicodus.shakeitup.models.Item;
import com.epicodus.shakeitup.models.PassObject;

import java.util.List;

/**
 * Created by Guest on 4/11/16.
 */
class ItemOnDragListener implements View.OnDragListener {

    int resumeColor;

    Item me;

    ItemOnDragListener(Context context, Item i){
        me = i;
        resumeColor = context.getResources().getColor(android.R.color.background_light);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundColor(0x30000000);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundColor(resumeColor);
                break;
            case DragEvent.ACTION_DROP:

                PassObject passObj = (PassObject)event.getLocalState();
                View view = passObj.getView();
                Item passedItem = passObj.getItem();
                List<Item> srcList = passObj.getSrcList();
                AbsListView oldParent = (AbsListView)view.getParent();
                ItemBaseAdapter srcAdapter = (ItemBaseAdapter)(oldParent.getAdapter());

                AbsListView newParent = (AbsListView)v.getParent();
                ItemBaseAdapter destAdapter = (ItemBaseAdapter)(newParent.getAdapter());
                List<Item> destList = destAdapter.getList();

                int removeLocation = srcList.indexOf(passedItem);
                int insertLocation = destList.indexOf(me);
				/*
				 * If drag and drop on the same list, same position,
				 * ignore
				 */
                if(srcList != destList || removeLocation != insertLocation){
                    if(removeItemToList(srcList, passedItem)){
                        destList.add(insertLocation, passedItem);
                    }

                    srcAdapter.notifyDataSetChanged();
                    destAdapter.notifyDataSetChanged();
                }

                v.setBackgroundColor(resumeColor);

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundColor(resumeColor);
            default:
                break;
        }

        return true;
    }

    private boolean removeItemToList(List<Item> l, Item it){
        boolean result = l.remove(it);
        return result;
    }

}