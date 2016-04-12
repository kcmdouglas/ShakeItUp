package com.epicodus.shakeitup.ui;

import android.app.Activity;
import android.content.ClipData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.epicodus.shakeitup.R;
import com.epicodus.shakeitup.adapters.ItemBaseAdapter;
import com.epicodus.shakeitup.adapters.ItemGridAdapter;
import com.epicodus.shakeitup.adapters.ItemListAdapter;
import com.epicodus.shakeitup.models.Business;
import com.epicodus.shakeitup.models.PassObject;

import org.parceler.Parcels;

import java.util.List;


public class FunChooserFragment extends Fragment {
    List<Business> mFunArray, items3;
    ListView listView1;
    GridView gridView3;
    ItemListAdapter myItemListAdapter1;
    ItemGridAdapter myItemGridAdapter3;
    LinearLayoutAbsListView area1, area3;
    private OnThirdItemDroppedInDropZone mListener;
    Business mDrinkPassed;
    Business mRestaurantPassed;

    public FunChooserFragment() {
    }

    public static FunChooserFragment newInstance() {
        return new FunChooserFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //this line reuses fragment layout from before
        View view = inflater.inflate(R.layout.fragment_chooser, container, false);
        listView1 = (ListView) view.findViewById(R.id.listview1);
        gridView3 = (GridView) view.findViewById(R.id.gridview3);
        area1 = (LinearLayoutAbsListView) view.findViewById(R.id.pane1);
        area3 = (LinearLayoutAbsListView) view.findViewById(R.id.pane3);
        area1.setOnDragListener(myOnDragListener);
        area3.setOnDragListener(myOnDragListener);
        area1.setAbsListView(listView1);
        area3.setAbsListView(gridView3);
        initItems();
        myItemListAdapter1 = new ItemListAdapter(getContext(), mFunArray);
        myItemGridAdapter3 = new ItemGridAdapter(getContext(), items3);
        listView1.setAdapter(myItemListAdapter1);
        gridView3.setAdapter(myItemGridAdapter3);

        listView1.setOnItemClickListener(listOnItemClickListener);
        gridView3.setOnItemClickListener(listOnItemClickListener);

        listView1.setOnItemLongClickListener(myOnItemLongClickListener);
        gridView3.setOnItemLongClickListener(myOnItemLongClickListener);

        return view;

    }

    AdapterView.OnItemLongClickListener myOnItemLongClickListener = new AdapterView.OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            Business selectedItem = (Business) (parent.getItemAtPosition(position));

            ItemBaseAdapter associatedAdapter = (ItemBaseAdapter)(parent.getAdapter());
            List<Business> associatedList = associatedAdapter.getList();

            PassObject passObj = new PassObject(view, selectedItem, associatedList);

            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, passObj, 0);

            return true;
        }

    };

    View.OnDragListener myOnDragListener = new View.OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            String area;
            if(v == area1){
                area = "area1";
            }else if(v == area3){
                area = "area3";
            }else{
                area = "unknown";
            }

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:

                    PassObject passObj = (PassObject)event.getLocalState();
                    View view = passObj.getView();
                    Business passedItem = passObj.getItem();
                    List<Business> srcList = passObj.getSrcList();
                    AbsListView parent = (AbsListView)view.getParent();
                    ItemBaseAdapter srcAdapter = (ItemBaseAdapter)(parent.getAdapter());

                    LinearLayoutAbsListView newParent = (LinearLayoutAbsListView)v;
                    ItemBaseAdapter destAdapter = (ItemBaseAdapter)(newParent.absListView.getAdapter());
                    List<Business> destList = destAdapter.getList();

                    addItemToList(destList, mDrinkPassed);
                    addItemToList(destList, mRestaurantPassed);

                    if(removeItemToList(srcList, passedItem)){
                        addItemToList(destList, passedItem);
                    }

                    srcAdapter.notifyDataSetChanged();
                    destAdapter.notifyDataSetChanged();
                    if (mListener != null) {
                        mListener.onThirdItemDroppedInDropZone(mDrinkPassed, mRestaurantPassed, passedItem);
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                default:
                    break;
            }

            return true;
        }

    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnThirdItemDroppedInDropZone) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemDroppedInZoneListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    AdapterView.OnItemClickListener listOnItemClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Toast.makeText(getContext(),
                    ((Business)(parent.getItemAtPosition(position))).getName(),
                    Toast.LENGTH_SHORT).show();
        }

    };

    private void initItems(){
        Bundle bundle = getArguments();
        mDrinkPassed = Parcels.unwrap(bundle.getParcelable("drink"));
        mRestaurantPassed = Parcels.unwrap(bundle.getParcelable("restaurant"));
        mFunArray = Parcels.unwrap(bundle.getParcelable("funArray"));
    }

    private boolean removeItemToList(List<Business> items, Business item){
        return items.remove(item);
    }

    private boolean addItemToList(List<Business> items, Business item){
        return items.add(item);
    }


    public interface OnThirdItemDroppedInDropZone {
        void onThirdItemDroppedInDropZone(Business firstItem, Business secondItem, Business thirdItem);
    }
}
