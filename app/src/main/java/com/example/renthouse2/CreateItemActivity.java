package com.example.renthouse2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;

public class CreateItemActivity extends AppCompatActivity {
    private String TAG = "CreateItemActivity";
    private ImageView img_createItem_picture,img_createItem_picture2,img_createItem_picture3;
    private String picturePath,userName,data_list;
    private int IMAGE_REQUEST_CODE = 1;
    private EditText edit_createItem_title,edit_createItem_price,edit_createItem_note,edit_createItem_pattern,
            edit_createItem_meters,edit_createItem_floor,edit_createItem_type,edit_createItem_introduce;
    private Button btn_createItem_add;
    private String title,tag1,tag2,tag3,note,price,pattern,meters,floor,type,introduce,localPicture;
    private Spinner spin_createItem_tag1,spin_createItem_tag2,spin_createItem_tag3;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private int houseCunt;
    private StorageReference storageReference,pic_storage;
    private Uri[] uri = new Uri[3];
    private String[] picture = new String[3];
    private int imgFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        initView();
    }
    
    private void initView(){
        imgFlag = -1;
        houseCunt = 1;
        tag1 = "整層住家";
        tag2 = "整層住家";
        tag2 = "整層住家";

        storageReference = FirebaseStorage.getInstance().getReference();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("HouseInfo");

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        userName = getPrefs.getString("userName", "");

        img_createItem_picture = (ImageView) findViewById(R.id.img_createItem_picture);
        img_createItem_picture2 = (ImageView) findViewById(R.id.img_createItem_picture2);
        img_createItem_picture3 = (ImageView) findViewById(R.id.img_createItem_picture3);
        btn_createItem_add = (Button)findViewById(R.id.btn_createItem_add);
        img_createItem_picture.setOnClickListener(onClick);
        img_createItem_picture2.setOnClickListener(onClick);
        img_createItem_picture3.setOnClickListener(onClick);
        btn_createItem_add.setOnClickListener(onClick);

        edit_createItem_title = (EditText) findViewById(R.id.edit_createItem_title);
        edit_createItem_price = (EditText) findViewById(R.id.edit_createItem_price);
        edit_createItem_note = (EditText) findViewById(R.id.edit_createItem_note);
        edit_createItem_pattern = (EditText) findViewById(R.id.edit_createItem_pattern);
        edit_createItem_meters = (EditText) findViewById(R.id.edit_createItem_meters);
        edit_createItem_floor = (EditText) findViewById(R.id.edit_createItem_floor);
        edit_createItem_type = (EditText) findViewById(R.id.edit_createItem_type);
        edit_createItem_introduce = (EditText) findViewById(R.id.edit_createItem_introduce);
        readExistingData();

        spin_createItem_tag1 = (Spinner) findViewById(R.id.spin_createItem_tag1);
        List<String> tagItem1 = Arrays.asList("整層住家","近捷運","近商圈","有電梯","可開伙");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.spinner_layout , tagItem1);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spin_createItem_tag1.setAdapter(adapter);
        spin_createItem_tag1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //tag1_position = position;
                tag1 = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: tag1 = "+tag1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spin_createItem_tag2 = (Spinner) findViewById(R.id.spin_createItem_tag2);
        List<String> tagItem2 = Arrays.asList("整層住家","近捷運","近商圈","有電梯","可開伙");
        ArrayAdapter adapter2 = new ArrayAdapter(getApplicationContext(), R.layout.spinner_layout , tagItem2);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spin_createItem_tag2.setAdapter(adapter2);
        spin_createItem_tag2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //tag1_position = position;
                tag2 = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: tag1 = "+tag2);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spin_createItem_tag3 = (Spinner) findViewById(R.id.spin_createItem_tag3);
        List<String> tagItem3 = Arrays.asList("整層住家","近捷運","近商圈","有電梯","可開伙");
        ArrayAdapter adapter3 = new ArrayAdapter(getApplicationContext(), R.layout.spinner_layout , tagItem3);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spin_createItem_tag3.setAdapter(adapter3);
        spin_createItem_tag3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //tag1_position = position;
                tag3 = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: tag1 = "+tag3);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.img_createItem_picture){
                Log.d(TAG, "onClick: img_createItem_picture");
                imgFlag = 0;
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }else if(v.getId() == R.id.btn_createItem_add){
                Log.d(TAG, "onClick: btn_createItem_add");
                addNew();
            }else if(v.getId() == R.id.img_createItem_picture2){
                Log.d(TAG, "onClick: img_createItem_picture2");
                imgFlag = 1;
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }else if(v.getId() == R.id.img_createItem_picture3){
                Log.d(TAG, "onClick: img_createItem_picture3");
                imgFlag = 2;
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }

        }
    };

    private void uploadPicture(int position){
        if(uri[position] != null){
            picture[position] = "picture"+position+"_"+houseCunt+".png";
            pic_storage = storageReference.child(picture[position]);
            pic_storage.putFile(uri[position]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(),"上傳PASS",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            picture[position]= "0.png";
        }
    }

    private void addNew(){

        for (int i=0; i<3;i++){
            uploadPicture(i);
        }
        title = edit_createItem_title.getText().toString();
        note = edit_createItem_note.getText().toString();
        price = edit_createItem_price.getText().toString();
        pattern = edit_createItem_pattern.getText().toString();
        meters = edit_createItem_meters.getText().toString();
        floor = edit_createItem_floor.getText().toString();
        type = edit_createItem_type.getText().toString();
        introduce = edit_createItem_introduce.getText().toString();


        UploadHouse uploadHouse = new UploadHouse(picture[0],picture[1],picture[2],title, tag1,tag2,tag3,note,price,pattern,
                meters,floor,type,introduce,userName);
        myRef.child("house"+houseCunt).setValue(uploadHouse);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                uri[imgFlag] = selectedImage;
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex); //获取照片路径
                Log.d(TAG, "onActivityResult: path = "+picturePath);
                cursor.close();
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                switch (imgFlag){
                    case 0:
                        img_createItem_picture.setImageBitmap(bitmap);
                        break;
                    case 1:
                        img_createItem_picture2.setImageBitmap(bitmap);
                        break;
                    case 2:
                        img_createItem_picture3.setImageBitmap(bitmap);
                        break;
                }
                /////////
                ContentResolver contentResolver = getContentResolver();
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                data_list = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri[imgFlag]));
                Log.d(TAG, "onActivityResult: data_list = "+data_list);
                //////////
            } catch (Exception e) {
                // TODO Auto-generatedcatch block
                e.printStackTrace();
            }
        }
    }

    private void readExistingData(){
        Log.d(TAG, "readExistingData: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String picture,title,tag1,tag2,tag3,note,price,pattern,meters,floor,type,introduce;
                House house = null;
                //title = dataSnapshot.child("house"+1).child("title").getValue().toString();
                //Log.d(TAG, "onDataChange: title = "+title);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+postSnapshot.toString());
                    picture = postSnapshot.child("picture").getValue().toString();
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
                    houseCunt++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}