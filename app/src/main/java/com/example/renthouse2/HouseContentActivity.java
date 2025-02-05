package com.example.renthouse2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class HouseContentActivity extends AppCompatActivity {

    private String TAG = "HouseContentActivity";
    private String title,tag1,tag2,tag3,note,price,pattern,meters,floor,type,introduce,uploader;
    private TextView txt_houseContent_title,txt_houseContent_tag1,txt_houseContent_tag2,txt_houseContent_tag3,
            txt_houseContent_price,txt_houseContent_note,txt_houseContent_pattern,txt_houseContent_meters,
            txt_houseContent_floor,txt_houseContent_type,txt_houseContent_introduce;
    private ImageView img_houseContent_picture,img_houseContent_star,img_houseContent_picture1,
            img_houseContent_picture2,img_houseContent_picture3;
    private Button btn_houseContent_contact;
    private StorageReference storageReference,pic_storage;
    private ProgressDialog dialog;
    private boolean starFlag;
    private String userName;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private int collectCunt;
    private boolean removeFlag;
    private File file;
    private File[] fileTemp = new File[3];
    private String[] picture = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_content);

        picture[0] = getIntent().getStringExtra("picture");
        picture[1] = getIntent().getStringExtra("picture2");
        picture[2] = getIntent().getStringExtra("picture3");
        title = getIntent().getStringExtra("title");
        tag1 = getIntent().getStringExtra("tag1");
        tag2 = getIntent().getStringExtra("tag2");
        tag3 = getIntent().getStringExtra("tag3");
        note = getIntent().getStringExtra("note");
        price = getIntent().getStringExtra("price");
        pattern = getIntent().getStringExtra("pattern");
        meters = getIntent().getStringExtra("meters");
        floor = getIntent().getStringExtra("floor");
        type = getIntent().getStringExtra("type");
        introduce = getIntent().getStringExtra("introduce");
        uploader = getIntent().getStringExtra("uploader");
        initView();
    }

    private void initView(){
        starFlag = false;
        collectCunt = 0;
        removeFlag = false;

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        userName = getPrefs.getString("userName", "");

        storageReference = FirebaseStorage.getInstance().getReference();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("CollectInfo");

        img_houseContent_picture = (ImageView) findViewById(R.id.img_houseContent_picture);
        img_houseContent_picture1 = (ImageView) findViewById(R.id.img_houseContent_picture1);
        img_houseContent_picture2 = (ImageView) findViewById(R.id.img_houseContent_picture2);
        img_houseContent_picture3 = (ImageView) findViewById(R.id.img_houseContent_picture3);
        img_houseContent_star = (ImageView) findViewById(R.id.img_houseContent_star);
        txt_houseContent_title = (TextView) findViewById(R.id.txt_houseContent_title);
        txt_houseContent_tag1 = (TextView) findViewById(R.id.txt_houseContent_tag1);
        txt_houseContent_tag2 = (TextView) findViewById(R.id.txt_houseContent_tag2);
        txt_houseContent_tag3 = (TextView) findViewById(R.id.txt_houseContent_tag3);
        txt_houseContent_price = (TextView) findViewById(R.id.txt_houseContent_price);
        txt_houseContent_note = (TextView) findViewById(R.id.txt_houseContent_note);
        txt_houseContent_pattern = (TextView) findViewById(R.id.txt_houseContent_pattern);
        txt_houseContent_meters = (TextView) findViewById(R.id.txt_houseContent_meters);
        txt_houseContent_floor = (TextView) findViewById(R.id.txt_houseContent_floor);
        txt_houseContent_type = (TextView) findViewById(R.id.txt_houseContent_type);
        txt_houseContent_introduce = (TextView) findViewById(R.id.txt_houseContent_introduce);
        btn_houseContent_contact = (Button)findViewById(R.id.btn_houseContent_contact);
        img_houseContent_star.setOnClickListener(onClick);
        btn_houseContent_contact.setOnClickListener(onClick);
        img_houseContent_picture1.setOnClickListener(onClick);
        img_houseContent_picture2.setOnClickListener(onClick);
        img_houseContent_picture3.setOnClickListener(onClick);

        for (int i=0; i<3 ;i++){
            readPicture(i);
        }

        txt_houseContent_title.setText(title);
        txt_houseContent_tag1.setText(tag1);
        txt_houseContent_tag2.setText(tag2);
        txt_houseContent_tag3.setText(tag3);
        txt_houseContent_price.setText(price+"/月");
        txt_houseContent_note.setText(note);
        txt_houseContent_pattern.setText(pattern);
        txt_houseContent_meters.setText(meters);
        txt_houseContent_floor.setText(floor);
        txt_houseContent_type.setText(type);
        txt_houseContent_introduce.setText(introduce);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.img_houseContent_star){
                Log.d(TAG, "onClick: img_houseContent_star");
                if (starFlag){
                    img_houseContent_star.setImageResource(R.drawable.star_gray);
                    starFlag = false;
                    removeFlag = true;
                    readAllDataAndRemove();
                }else {
                    img_houseContent_star.setImageResource(R.drawable.star_gold);
                    starFlag = true;
                    removeFlag = false;
                    //myRef.child(userName).child("collect"+collectCunt).setValue(title);
                    UploadHouse uploadHouse = new UploadHouse(picture[0],picture[1],picture[2],title, tag1,tag2,tag3,note,price,pattern,
                            meters,floor,type,introduce,userName);
                    myRef.child(userName).child("collect"+collectCunt).setValue(uploadHouse);
                }
            }else if (v.getId() == R.id.btn_houseContent_contact){
                Log.d(TAG, "onClick: btn_houseContent_contact");
                if (userName.equals(uploader)){
                    Toast.makeText(getApplicationContext(),"您為業主",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getApplicationContext(), ChatWindowActivity.class);
                    intent.putExtra("otherName",uploader);
                    startActivity(intent);
                }
            }else if (v.getId() == R.id.img_houseContent_picture1){
                Log.d(TAG, "onClick: img_houseContent_picture1");
                img_houseContent_picture.setImageURI(Uri.fromFile(fileTemp[0]));
            }else if (v.getId() == R.id.img_houseContent_picture2){
                Log.d(TAG, "onClick: img_houseContent_picture2");
                img_houseContent_picture.setImageURI(Uri.fromFile(fileTemp[1]));
            }else if (v.getId() == R.id.img_houseContent_picture3){
                Log.d(TAG, "onClick: img_houseContent_picture3");
                img_houseContent_picture.setImageURI(Uri.fromFile(fileTemp[2]));
            }
        }
    };

    private void readPicture(int position){
        pic_storage = storageReference.child(picture[position]);
        //final File file;
        try {
            fileTemp[position] = File.createTempFile("images","png");
            pic_storage.getFile(fileTemp[position]).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    switch (position){
                        case 0:
                            img_houseContent_picture.setImageURI(Uri.fromFile(fileTemp[position]));
                            img_houseContent_picture1.setImageURI(Uri.fromFile(fileTemp[position]));
                            break;
                        case 1:
                            img_houseContent_picture2.setImageURI(Uri.fromFile(fileTemp[position]));
                            break;
                        case 2:
                            img_houseContent_picture3.setImageURI(Uri.fromFile(fileTemp[position]));
                            break;
                    }

                    Log.d(TAG, "onSuccess: ");
                    //dialog.dismiss();
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

    @Override
    public void onStart() {
        super.onStart();
        readExistingData();
    }

    private void readAllDataAndRemove(){
        Log.d(TAG, "readAllDataAndRemove: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //title = dataSnapshot.child("house"+1).child("title").getValue().toString();
                //Log.d(TAG, "onDataChange: title = "+title);
                for (DataSnapshot postSnapshot : dataSnapshot.child(userName).getChildren()){
                    Log.d(TAG, "onDataChange: "+postSnapshot.toString());
                    Log.d(TAG, "onDataChange: postSnapshot.getValue() = "+postSnapshot.getValue());
                    Log.d(TAG, "onDataChange: postSnapshot.getKey() = "+postSnapshot.getKey());

                    if (removeFlag && postSnapshot.child("title").getValue().toString().equals(title)){
                        myRef.child(userName).child(postSnapshot.getKey()).removeValue();
                        removeFlag = false;
                    }
                }
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
                //title = dataSnapshot.child("house"+1).child("title").getValue().toString();
                //Log.d(TAG, "onDataChange: title = "+title);
                for (DataSnapshot postSnapshot : dataSnapshot.child(userName).getChildren()){
                    Log.d(TAG, "onDataChange: "+postSnapshot.toString());
                    if (postSnapshot.child("title").getValue().toString().equals(title)){
                        img_houseContent_star.setImageResource(R.drawable.star_gold);
                        starFlag = true;
                    }
                    collectCunt++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}