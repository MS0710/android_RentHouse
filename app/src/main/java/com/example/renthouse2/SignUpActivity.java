package com.example.renthouse2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {
    private String TAG = "SignUpActivity";
    private EditText edit_signUp_account,edit_signUp_password,edit_signUp_checkPassword,edit_signUp_name;
    private CardView cv_signUp_people;
    private ImageView img_signUp_people;
    private int IMAGE_REQUEST_CODE = 1;
    private TextView txt_signUp_tip;
    private Button btn_signUp_ok;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference storageReference,pic_storage;
    private String account,password,name;
    private int userCunt;
    private boolean flag_ok;
    private String[] accountBuf = new String[100];
    private String[] nameBuf = new String[100];
    private Uri uri;
    private String picturePath,data_list,picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
    }

    private void initView(){
        userCunt = 0;
        for (int i=0; i<100;i++){
            accountBuf[i] = "";
        }
        flag_ok = true;

        storageReference = FirebaseStorage.getInstance().getReference();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserInfo");

        edit_signUp_account = (EditText) findViewById(R.id.edit_signUp_account);
        edit_signUp_password = (EditText) findViewById(R.id.edit_signUp_password);
        edit_signUp_checkPassword = (EditText) findViewById(R.id.edit_signUp_checkPassword);
        edit_signUp_name = (EditText) findViewById(R.id.edit_signUp_name);
        txt_signUp_tip = (TextView) findViewById(R.id.txt_signUp_tip);
        btn_signUp_ok = (Button) findViewById(R.id.btn_signUp_ok);
        cv_signUp_people = (CardView)findViewById(R.id.cv_signUp_people);
        img_signUp_people = (ImageView) findViewById(R.id.img_signUp_people);
        btn_signUp_ok.setOnClickListener(onClick);
        cv_signUp_people.setOnClickListener(onClick);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        flag_ok = true;
        userCunt = 0;
        readExistingData();
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_signUp_ok){
                Log.d(TAG, "onClick: btn_signUp_ok");
                account = edit_signUp_account.getText().toString();
                password = edit_signUp_password.getText().toString();
                name = edit_signUp_name.getText().toString();
                if (checkAccount()){
                    txt_signUp_tip.setText("帳號已被使用");
                }else {
                    if (checkName()){
                        txt_signUp_tip.setText("名稱已被使用");
                    }else {
                        txt_signUp_tip.setText("");
                        if(password.equals(edit_signUp_checkPassword.getText().toString())){
                            txt_signUp_tip.setText("");
                            addNewUser();
                            finish();
                        }else {
                            txt_signUp_tip.setText("請檢查輸入的兩次密碼");
                        }
                    }
                }
            }else if(v.getId() == R.id.cv_signUp_people){
                Log.d(TAG, "onClick: cv_signUp_people");
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        }
    };

    private boolean checkAccount(){
        for (int i=0; i<userCunt ; i++){
            if (accountBuf[i].equals(account)){
                return true;
            }
        }
        return false;
    }

    private boolean checkName(){
        for (int i=0; i<userCunt ; i++){
            if (nameBuf[i].equals(account)){
                return true;
            }
        }
        return false;
    }

    private void addNewUser(){
        Log.d(TAG, "addNewUser: ");
        uploadPicture();

        UserInfo userInfo = new UserInfo(account, password,name,picture);
        myRef.child("user"+userCunt).setValue(userInfo);
        flag_ok = false;
    }

    private void readExistingData(){
        Log.d(TAG, "readExistingData: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d(TAG, "onDataChange: user001 ="+dataSnapshot.child("user001").child("account").getValue().toString());
                //Log.d(TAG, "onDataChange: user002 ="+dataSnapshot.child("user002").child("account").getValue().toString());
                if (flag_ok){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: "+snapshot.toString());
                        Log.d(TAG, "onDataChange: userCunt = " + userCunt);
                        accountBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("account").getValue().toString();
                        nameBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("name").getValue().toString();
                        userCunt++;
                    }
                    Log.d(TAG, "onDataChange: ---------userCunt = " + userCunt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadPicture(){
        String timeStamp;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        Log.d(TAG, "Calendar获取当前日期"+year+"年"+month+"月"+day+"日"+hour+":"+minute+":"+second);

        timeStamp = ""+year+month+day+hour+minute+second;

        if(uri != null){
            picture = name+".png";
            pic_storage = storageReference.child(picture);
            pic_storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(),"上傳PASS",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            picture= "people.png";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                uri = selectedImage;
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex); //获取照片路径
                Log.d(TAG, "onActivityResult: path = "+picturePath);
                cursor.close();
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                img_signUp_people.setImageBitmap(bitmap);
                /////////
                ContentResolver contentResolver = getContentResolver();
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                data_list = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
                Log.d(TAG, "onActivityResult: data_list = "+data_list);
                //////////
            } catch (Exception e) {
                // TODO Auto-generatedcatch block
                e.printStackTrace();
            }
        }
    }

}