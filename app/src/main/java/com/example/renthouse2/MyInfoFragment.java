package com.example.renthouse2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
 * Use the {@link MyInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "MyInfoFragment";
    private TextView txt_myInfo_userName,txt_myInfo_userAccount;
    private ListView lv_myInfo_list;
    private List<House> list;
    private HouseListAdapter houseListAdapter;
    private Button btn_myInfo_Add;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userName,userAccount,userPicture;
    private ProgressDialog dialog;
    private StorageReference storageReference,pic_storage;
    private ImageView img_myInfo_people,img_myInfo_logOut;

    public MyInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyInfoFragment newInstance(String param1, String param2) {
        MyInfoFragment fragment = new MyInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_info, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("HouseInfo");

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        userName = getPrefs.getString("userName", "");
        userAccount = getPrefs.getString("userAccount", "");
        userPicture = getPrefs.getString("userPicture", "");
        Log.d(TAG, "onCreateView: userPicture = "+userPicture);

        txt_myInfo_userName = (TextView) view.findViewById(R.id.txt_myInfo_userName);
        txt_myInfo_userAccount = (TextView) view.findViewById(R.id.txt_myInfo_userAccount);
        img_myInfo_people = (ImageView) view.findViewById(R.id.img_myInfo_people);
        img_myInfo_logOut = (ImageView) view.findViewById(R.id.img_myInfo_logOut);
        txt_myInfo_userName.setText(userName);
        txt_myInfo_userAccount.setText(userAccount);
        img_myInfo_logOut.setOnClickListener(onClick);

        btn_myInfo_Add = (Button) view.findViewById(R.id.btn_myInfo_Add);
        btn_myInfo_Add.setOnClickListener(onClick);
        lv_myInfo_list = (ListView) view.findViewById(R.id.lv_myInfo_list);
        list = new ArrayList<>();
        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("讀取圖片資料中");
        dialog.show();
        houseListAdapter = new HouseListAdapter(getContext(),list,dialog);
        lv_myInfo_list.setAdapter(houseListAdapter);
        lv_myInfo_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: title = "+list.get(position).getTitle());
                Intent intent = new Intent(getContext(), ModifyItemActivity.class);
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
                intent.putExtra("key",list.get(position).getKey());
                startActivity(intent);
            }
        });
        return view;
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_myInfo_Add){
                Log.d(TAG, "onClick: btn_myInfo_Add");
                //addNew();
                Intent intent = new Intent(getContext(), CreateItemActivity.class);
                startActivity(intent);
            }else if (v.getId() == R.id.img_myInfo_logOut){
                Log.d(TAG, "onClick: img_myInfo_logOut");
                AlertDialog.Builder alertDialog =
                        new AlertDialog.Builder(getContext());
                alertDialog.setTitle("登出");
                alertDialog.setMessage("請確認是否登出");
                alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),"登出帳號",Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                });
                alertDialog.setNeutralButton("取消",(dialog, which) -> {
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        readExistingData();
        loadUserPicture();
    }

    private void loadUserPicture(){
        pic_storage = storageReference.child(userPicture);
        final File file;
        try {
            file = File.createTempFile("images","png");
            pic_storage.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: file = "+file);
                    img_myInfo_people.setImageURI(Uri.fromFile(file));
                    Log.d(TAG, "onSuccess: ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: ");
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void readExistingData(){
        Log.d(TAG, "readExistingData: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String picture,title,tag1,tag2,tag3,note,price,pattern,meters,floor,type,introduce,uploader;
                String picture2,picture3;
                String key;
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
                    Log.d(TAG, "onDataChange: key = "+key);
                    Log.d(TAG, "onDataChange: uploader = "+uploader);
                    if (uploader.equals(userName)){
                        house = new House(picture,picture2,picture3,title,tag1,tag2,tag3,note,price,pattern,meters,floor,type,introduce,key,uploader);
                        list.add(house);
                    }
                }
                houseListAdapter.notifyDataSetChanged();
                if (list.isEmpty()){
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNew(){
        UploadHouse uploadHouse = new UploadHouse("house1.png","house1_2.png","house1_3.png",
                "海洋都心景觀四房雙車免仲介費可入戶籍補助",
                "整層住家","近捷運","有電梯","達欣海都 NO.3 淡水區新市三路二段",
                "32258","4房2廳2衛","69.42坪","10F/28F","電梯大樓",getString(R.string.house1),userName);
        myRef.child("house1").setValue(uploadHouse);

        UploadHouse uploadHouse2 = new UploadHouse("house2.png","house2_2.png","house2_3.png",
                "忠孝復興兩房/附傢俱/可以申請租補",
                "整層住家","有電梯","可開伙","中山區復興南路一段",
                "42000","2房2廳1衛","12.7坪","11F/12F","電梯大樓",getString(R.string.house2),userName);
        myRef.child("house2").setValue(uploadHouse2);

        UploadHouse uploadHouse3 = new UploadHouse("house3.png","house3_2.png","house3_3.png",
                "到站就到家❗️信義區美妝兩房/中信唐宇",
                "整層住家","近捷運","可開伙","信義區虎林街132巷",
                "32000","2房1廳1衛","15坪","4F/4F","公寓",getString(R.string.house3),userName);
        myRef.child("house3").setValue(uploadHouse3);
    }
}