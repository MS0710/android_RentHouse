package com.example.renthouse2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RentHouseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RentHouseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "RentHouseFragment";
    private ListView lv_rentHouse_list;
    private List<House> list;
    private HouseListAdapter houseListAdapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ProgressDialog dialog;
    private TextInputLayout textInput_rentHouse_searchWord_layout;
    private EditText edit_rentHouse_searchWord;
    private CardView cv_rentHouse_search;
    private Button btn_rentHouse_all,btn_rentHouse_wholeFloorHome,btn_rentHouse_nearMRT,btn_rentHouse_nearBusiness,
            btn_rentHouse_elevator,btn_rentHouse_kitchen;
    public RentHouseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RentHouseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RentHouseFragment newInstance(String param1, String param2) {
        RentHouseFragment fragment = new RentHouseFragment();
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
        View view = inflater.inflate(R.layout.fragment_rent_house, container, false);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("HouseInfo");

        textInput_rentHouse_searchWord_layout = (TextInputLayout) view.findViewById(R.id.textInput_rentHouse_searchWord_layout);
        edit_rentHouse_searchWord = (EditText) view.findViewById(R.id.edit_rentHouse_searchWord);
        cv_rentHouse_search = (CardView) view.findViewById(R.id.cv_rentHouse_search);

        btn_rentHouse_all = (Button) view.findViewById(R.id.btn_rentHouse_all);
        btn_rentHouse_wholeFloorHome = (Button) view.findViewById(R.id.btn_rentHouse_wholeFloorHome);
        btn_rentHouse_nearMRT = (Button) view.findViewById(R.id.btn_rentHouse_nearMRT);
        btn_rentHouse_nearBusiness = (Button) view.findViewById(R.id.btn_rentHouse_nearBusiness);
        btn_rentHouse_elevator = (Button) view.findViewById(R.id.btn_rentHouse_elevator);
        btn_rentHouse_kitchen = (Button) view.findViewById(R.id.btn_rentHouse_kitchen);


        cv_rentHouse_search.setOnClickListener(onClick);
        btn_rentHouse_all.setOnClickListener(onClick);
        btn_rentHouse_wholeFloorHome.setOnClickListener(onClick);
        btn_rentHouse_nearMRT.setOnClickListener(onClick);
        btn_rentHouse_nearBusiness.setOnClickListener(onClick);
        btn_rentHouse_elevator.setOnClickListener(onClick);
        btn_rentHouse_kitchen.setOnClickListener(onClick);


        lv_rentHouse_list = (ListView) view.findViewById(R.id.lv_rentHouse_list);
        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("讀取圖片資料中");
        dialog.show();
        list = new ArrayList<>();
        /*for (int i=0 ; i<5 ; i++){
            House house = new House("Jack");
            list.add(house);
        }*/
        houseListAdapter = new HouseListAdapter(getContext(),list,dialog);
        lv_rentHouse_list.setAdapter(houseListAdapter);
        lv_rentHouse_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cv_rentHouse_search){
                Log.d(TAG, "onClick: ");
                if (edit_rentHouse_searchWord.getText().equals("")){
                    readExistingData();
                }else {
                    searchKeyWord(edit_rentHouse_searchWord.getText().toString());
                }
            }else if (v.getId() == R.id.btn_rentHouse_all){
                Log.d(TAG, "onClick: btn_rentHouse_all");
                readExistingData();
            }else if (v.getId() == R.id.btn_rentHouse_wholeFloorHome){
                Log.d(TAG, "onClick: btn_rentHouse_wholeFloorHome");
                searchTag("整層住家");
            }else if (v.getId() == R.id.btn_rentHouse_nearMRT){
                Log.d(TAG, "onClick: btn_rentHouse_nearMRT");
                searchTag("近捷運");
            }else if (v.getId() == R.id.btn_rentHouse_nearBusiness){
                Log.d(TAG, "onClick: btn_rentHouse_nearBusiness");
                searchTag("近商圈");
            }else if (v.getId() == R.id.btn_rentHouse_elevator){
                Log.d(TAG, "onClick: btn_rentHouse_elevator");
                searchTag("有電梯");
            }else if (v.getId() == R.id.btn_rentHouse_kitchen){
                Log.d(TAG, "onClick: btn_rentHouse_kitchen");
                searchTag("可開伙");
            }
        }
    };

    private void searchTag(String tag){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String picture,title,tag1,tag2,tag3,note,price,pattern,meters,floor,type,introduce,key,uploader;
                String picture2,picture3;
                House house = null;
                list.clear();
                //title = dataSnapshot.child("house"+1).child("title").getValue().toString();
                //Log.d(TAG, "onDataChange: title = "+title);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
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
                    if (tag1.contains(tag) || tag2.contains(tag)|| tag3.contains(tag)){
                        house = new House(picture,picture2,picture3,title,tag1,tag2,tag3,note,price,pattern,meters,floor,type,introduce,key,uploader);
                        list.add(house);
                    }
                }
                houseListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchKeyWord(String keyWord){
        Log.d(TAG, "searchKeyWord: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String picture,title,tag1,tag2,tag3,note,price,pattern,meters,floor,type,introduce,key,uploader;
                String picture2,picture3;
                House house = null;
                list.clear();
                //title = dataSnapshot.child("house"+1).child("title").getValue().toString();
                //Log.d(TAG, "onDataChange: title = "+title);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
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
                    if (title.contains(keyWord)){
                        house = new House(picture,picture2,picture3,title,tag1,tag2,tag3,note,price,pattern,meters,floor,type,introduce,key,uploader);
                        list.add(house);
                    }
                }
                houseListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                //title = dataSnapshot.child("house"+1).child("title").getValue().toString();
                //Log.d(TAG, "onDataChange: title = "+title);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}