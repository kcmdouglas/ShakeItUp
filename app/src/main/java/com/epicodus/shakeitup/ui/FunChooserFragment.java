package com.epicodus.shakeitup.ui;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.shakeitup.ChooserActivity;
import com.epicodus.shakeitup.R;
import com.epicodus.shakeitup.adapters.ItemBaseAdapter;
import com.epicodus.shakeitup.adapters.ItemGridAdapter;
import com.epicodus.shakeitup.adapters.ItemListAdapter;
import com.epicodus.shakeitup.models.Business;
import com.epicodus.shakeitup.models.PassObject;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FunChooserFragment extends Fragment {
    List<Business> mFunArray, mSelectedBusinessesArray;
    ListView listView1;
    ItemListAdapter myItemListAdapter1;
    ItemGridAdapter myItemGridAdapter3;
    LinearLayoutAbsListView area1, area3;
    private OnThirdItemDroppedInDropZone mListener;
    Business mDrinkPassed;
    Business mDinnerPassed;
    GridView drinkGridView;
    GridView dinnerGridView;
    GridView funGridView;
    CardView dinnerCardView;
    CardView drinkCardView;
    CardView funCardView;
    TextView funTextView;
    TextView dinnerTextView;
    TextView drinkTextView;
    TextView instructionsText;
    ImageView mDrinkImageView;
    ImageView mDinnerImageView;


    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorEventListener listener;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 1500;
    private long lastShakeTime = 0;
    private MediaPlayer mediaPlayer;


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
        ChooserActivity.loadingDialog.hide();
        listView1 = (ListView) view.findViewById(R.id.listview1);
        funGridView = (GridView) view.findViewById(R.id.funGridView);
        funCardView = (CardView) view.findViewById(R.id.funCardView);
        funTextView = (TextView) view.findViewById(R.id.funTextView);
        dinnerTextView = (TextView) view.findViewById(R.id.dinnerTextView);
        drinkTextView = (TextView) view.findViewById(R.id.drinkTextView);
        dinnerGridView = (GridView) view.findViewById(R.id.dinnerGridView);
        dinnerCardView = (CardView) view.findViewById(R.id.dinnerCardView);
        drinkGridView = (GridView) view.findViewById(R.id.drinkGridView);
        drinkCardView = (CardView) view.findViewById(R.id.drinkCardView);

        instructionsText = (TextView) view.findViewById(R.id.instructionsText);

        mDrinkImageView = (ImageView) view.findViewById(R.id.drinkImageView);
        mDinnerImageView = (ImageView) view.findViewById(R.id.dinnerImageView);
        area1 = (LinearLayoutAbsListView) view.findViewById(R.id.pane1);
        area3 = (LinearLayoutAbsListView) view.findViewById(R.id.pane3);
        area1.setOnDragListener(myOnDragListener);
        area3.setOnDragListener(myOnDragListener);
        area1.setAbsListView(listView1);
        area3.setAbsListView(funGridView);
        initItems();
        myItemListAdapter1 = new ItemListAdapter(getContext(), mFunArray);
        myItemGridAdapter3 = new ItemGridAdapter(getContext(), mSelectedBusinessesArray);
        listView1.setAdapter(myItemListAdapter1);
        funGridView.setAdapter(myItemGridAdapter3);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    long currentTime = System.currentTimeMillis();
                    if ((currentTime - lastUpdate) > 100) {
                        long timeDifference = currentTime - lastUpdate;
                        lastUpdate = currentTime;

                        float speed = Math.abs(x + y + z - last_x - last_y - last_z)/timeDifference * 10000;
                        if (speed > SHAKE_THRESHOLD) {
                            long now = System.currentTimeMillis();
                            if (now - lastShakeTime > 1000) {
                                ChooserActivity activity = (ChooserActivity) getActivity();
                                activity.soundManager("guitar");
                                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                if (vibrator.hasVibrator()) {
                                    vibrator.vibrate(300);
                                }
                                randomizeFun();
                            }

                            lastShakeTime = System.currentTimeMillis();
                        }
                    }

                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        mSensorManager.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        listView1.setOnItemLongClickListener(myOnItemLongClickListener);
        funGridView.setOnItemLongClickListener(myOnItemLongClickListener);

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
                    mSelectedBusinessesArray.clear();
                    PassObject passObj = (PassObject)event.getLocalState();
                    View view = passObj.getView();
                    Business passedItem = passObj.getItem();
                    List<Business> srcList = passObj.getSrcList();
                    AbsListView parent = (AbsListView)view.getParent();
                    ItemBaseAdapter srcAdapter = (ItemBaseAdapter)(parent.getAdapter());
                    LinearLayoutAbsListView newParent = (LinearLayoutAbsListView)v;
                    ItemBaseAdapter destAdapter = (ItemBaseAdapter)(newParent.absListView.getAdapter());
                    List<Business> destList = destAdapter.getList();

                    initializeCard(passedItem, R.id.funImageView);

                    addItemToList(destList, mDrinkPassed);
                    addItemToList(destList, mDinnerPassed);

                    if(removeItemToList(srcList, passedItem)){
                        addItemToList(destList, passedItem);
                    }

                    funGridView.setVisibility(View.GONE);
                    funCardView.setVisibility(View.VISIBLE);

                    srcAdapter.notifyDataSetChanged();
                    destAdapter.notifyDataSetChanged();

                    mListener.onThirdItemDroppedInDropZone(mDrinkPassed, mDinnerPassed, passedItem);

                    mSensorManager.unregisterListener(listener);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                default:
                    break;
            }

            return true;
        }

    };

    private void initializeCard(Business business, int viewId) {
        Picasso.with(getContext()).load(business.getImageUrl()).fit().centerCrop().into((ImageView) getView().findViewById(viewId));
        funTextView.setText(business.getCardText());
    }

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

    private void initItems(){
        mSelectedBusinessesArray = new ArrayList<>();
        Bundle bundle = getArguments();
        mDrinkPassed = Parcels.unwrap(bundle.getParcelable("drink"));
        mDinnerPassed = Parcels.unwrap(bundle.getParcelable("dinner"));

        instructionsText.setText(R.string.funChoiceInstructions);
        instructionsText.setTextColor(getResources().getColor(R.color.colorFunAccent));

        Picasso.with(getContext()).load(mDrinkPassed.getImageUrl()).fit().centerCrop().into(mDrinkImageView);
        Picasso.with(getContext()).load(mDinnerPassed.getImageUrl()).fit().centerCrop().into(mDinnerImageView);

        drinkTextView.setText(mDrinkPassed.getCardText());
        dinnerTextView.setText(mDinnerPassed.getCardText());

        drinkGridView.setVisibility(View.GONE);
        drinkCardView.setVisibility(View.VISIBLE);
        dinnerGridView.setVisibility(View.GONE);
        dinnerCardView.setVisibility(View.VISIBLE);
        funGridView.setVisibility(View.VISIBLE);

        mFunArray = Business.getRandomFun();
    }

    private boolean removeItemToList(List<Business> items, Business item){
        return items.remove(item);
    }

    private boolean addItemToList(List<Business> items, Business item){
        return items.add(item);
    }

//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        Sensor sensor = event.sensor;
//        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//
//            long currentTime = System.currentTimeMillis();
//            if ((currentTime - lastUpdate) > 100) {
//                long timeDifference = currentTime - lastUpdate;
//                lastUpdate = currentTime;
//
//                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/timeDifference * 10000;
//                if (speed > SHAKE_THRESHOLD) {
//                    long now = System.currentTimeMillis();
//                    if (now - lastShakeTime > 1000) {
//                        randomizeFun();
//                    }
//
//                    lastShakeTime = System.currentTimeMillis();
//                }
//            }
//
//            last_x = x;
//            last_y = y;
//            last_z = z;
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }


    public interface OnThirdItemDroppedInDropZone {
        void onThirdItemDroppedInDropZone(Business firstItem, Business secondItem, Business thirdItem);
    }

    private void randomizeFun() {
        mFunArray = Business.getRandomFun();
        myItemListAdapter1.list.clear();
        myItemListAdapter1.list.addAll(mFunArray);
        myItemListAdapter1.notifyDataSetChanged();
    }

}
