package com.example.renthouse2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CollectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CollectFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "CollectFragment";
    private ListView lv_collect_list;
    private List<House> list;
    private HouseListAdapter houseListAdapter;
    private LinearLayout ll_collect_noInfoTip;
    private ProgressDialog dialog;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userName;
    private String[] tempCollect = new String[50];
    private int collectCunt;

    public CollectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CollectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CollectFragment newInstance(String param1, String param2) {
        CollectFragment fragment = new CollectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collect, container, false);
        for (int i=0 ; i<50 ;i++){
            tempCollect[i] = "-1";
        }
        collectCunt = 0;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("CollectInfo");

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        userName = getPrefs.getString("userName", "");

        lv_collect_list = (ListView)view.findViewById(R.id.lv_collect_list);
        ll_collect_noInfoTip = (LinearLayout) view.findViewById(R.id.ll_collect_noInfoTip);
        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("讀取圖片資料中");
        dialog.show();
        list = new ArrayList<>();

        houseListAdapter = new HouseListAdapter(getContext(),list,dialog);
        lv_collect_list.setAdapter(houseListAdapter);
        lv_collect_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: title = "+list.get(position).getTitle());
                Intent intent = new Intent(getContext(), HouseContentActivity.class);
                intent.putExtra("picture",list.get(position).getPicture());
                intent.putExtra("picture2",list.get(position).getPicture2());
                intent.putExtra("picture3",list.get(position).getPicture3());
                intent.putExtra("title",list.get(position).getTitle());
                intent.putExtra("tag1",list.get(position).getTag1());
                intent.putExtra("tag2",list.get(position).getTag2());
                intent.putExtra("tag3",list.get(position).getTag3());
                intent.putExtra("note",list.get(position).getNote());
                intent.putExtra("price",list.get(position).getPrice());
                intent.putExtra("pattern",list.get(position).getPattern());
                intent.putExtra("meters",list.get(position).getMeters());
                intent.putExtra("floor",list.get(position).getFloor());
                intent.putExtra("type",list.get(position).getType());
                intent.putExtra("introduce",list.get(position).getIntroduce());
                intent.putExtra("localPicture",list.get(position).getIntroduce());
                intent.putExtra("uploader",list.get(position).getUploader());
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        readExistingData();
    }

    private void readExistingData(){
        Log.d(TAG, "readExistingData: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String picture,title,tag1,tag2,tag3,note,price,pattern,meters,floor,type,introduce,key,uploader;
                String picture2,picture3;
                House house = null;
                list.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.child(userName).getChildren()){
                    Log.d(TAG, "onDataChange: "+postSnapshot.toString());
                    picture = postSnapshot.child("picture").getValue().toString();
                    picture2 = postSnapshot.child("picture2").getValue().toString();
                    picture3 = postSnapshot.child("picture3").getValue().toString();
                    title = postSnapshot.child("title").getValue().toString();
                    tag1 = postSnapshot.child("tag1").getValue().toString();
                    tag2 = postSnapshot.child("tag2").getValue().toString();
                    tag3 = postSnapshot.child("tag3").getValue().toString();
                    note = postSnapshot.child("note").getValue().toString();
                    price = postSnapshot.child("price").getValue().toString();
                    pattern = postSnapshot.child("pattern").getValue().toString();
                    meters = postSnapshot.child("meters").getValue().toString();
                    floor = postSnapshot.child("floor").getValue().toString();
                    type = postSnapshot.child("type").getValue().toString();
                    introduce = postSnapshot.child("introduce").getValue().toString();
                    key = postSnapshot.getKey();
                    uploader = postSnapshot.child("uploadUser").getValue().toString();
                    Log.d(TAG, "onDataChange: picture = "+picture);
                    Log.d(TAG, "onDataChange: title = "+title);
                    Log.d(TAG, "onDataChange: tag1 = "+tag1);
                    Log.d(TAG, "onDataChange: tag2 = "+tag2);
                    Log.d(TAG, "onDataChange: tag3 = "+tag3);
                    Log.d(TAG, "onDataChange: note = "+note);
                    Log.d(TAG, "onDataChange: price = "+price);
                    Log.d(TAG, "onDataChange: pattern = "+pattern);
                    Log.d(TAG, "onDataChange: meters = "+meters);
                    Log.d(TAG, "onDataChange: floor = "+floor);
                    Log.d(TAG, "onDataChange: type = "+type);
                    Log.d(TAG, "onDataChange: introduce = "+introduce);
                    Log.d(TAG, "onDataChange: uploader = "+uploader);
                    house = new House(picture,picture2,picture3,title,tag1,tag2,tag3,note,price,pattern,meters,floor,type,introduce,key,uploader);
                    list.add(house);
                }
                houseListAdapter.notifyDataSetChanged();
                if (list.isEmpty()){
                    ll_collect_noInfoTip.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }else {
                    ll_collect_noInfoTip.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}