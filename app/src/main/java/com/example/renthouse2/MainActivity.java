package com.example.renthouse2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private TextView txt_main_condition,txt_main_forgetPassword;
    private CheckBox cb_main_checkStatus;
    private Button btn_main_signUp,btn_main_signIn;
    private TextInputLayout textInput_main_account_layout,textInput_main_password_layout;
    private EditText edit_main_account,edit_main_password;
    private ImageView img_main_watchPassword;
    private String account,password;
    private boolean flag_checkBox,flag_password;
    private int userCunt;
    private String[] accountBuf = new String[100];
    private String[] passwordBuf = new String[100];
    private String[] nameBuf = new String[100];
    private String[] pictureBuf = new String[100];
    private int correctCunt;
    private SharedPreferences getPrefs;
    private SharedPreferences.Editor editor;
    //////////
    private StorageReference storageReference,pic_storage;
    Uri uri;
    String data_list;

    private FirebaseDatabase database1;
    private DatabaseReference myRef1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        flag_checkBox = false;
        flag_password = false;
        account = "";
        password = "";
        userCunt = 0;
        correctCunt = 0;

        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        getPrefs.getString("userName", "");
        getPrefs.getString("userAccount", "");
        getPrefs.getString("userPicture", "");

        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserInfo");

        database1 = FirebaseDatabase.getInstance();
        myRef1 = database1.getReference("ChatInfo");

        edit_main_account = (EditText)findViewById(R.id.edit_main_account);
        edit_main_password = (EditText)findViewById(R.id.edit_main_password);
        img_main_watchPassword = (ImageView)findViewById(R.id.img_main_watchPassword);
        txt_main_forgetPassword = (TextView) findViewById(R.id.txt_main_forgetPassword);
        textInput_main_account_layout = (TextInputLayout) findViewById(R.id.textInput_main_account_layout);
        textInput_main_password_layout = (TextInputLayout) findViewById(R.id.textInput_main_password_layout);
        btn_main_signUp = (Button)findViewById(R.id.btn_main_signUp);
        btn_main_signIn = (Button)findViewById(R.id.btn_main_signIn);
        btn_main_signUp.setOnClickListener(onClick);
        btn_main_signIn.setOnClickListener(onClick);
        img_main_watchPassword.setOnClickListener(onClick);
        txt_main_forgetPassword.setOnClickListener(onClick);

        txt_main_condition = (TextView) findViewById(R.id.txt_main_condition);
        SpannableStringBuilder style = new SpannableStringBuilder("登入及視為同意服務條款、免責聲明、隱私權聲明、即時通訊功能服務條款");
        style.setSpan(new ForegroundColorSpan(Color.BLUE), 7, 32, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_main_condition.setText(style);

        cb_main_checkStatus = (CheckBox) findViewById(R.id.cb_main_checkStatus);
        cb_main_checkStatus.setOnCheckedChangeListener(checkBoxOnCheckedChange);

        //edit_main_account.setText("abc2");
        //edit_main_password.setText("123");
    }

    @Override
    protected void onStart() {
        super.onStart();
        readExistingData();
    }

    private CompoundButton.OnCheckedChangeListener checkBoxOnCheckedChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                flag_checkBox = true;
            }else {
                flag_checkBox = false;
            }

        }
    };
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_main_signUp){
                Log.d(TAG, "onClick: btn_main_signUp");
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }else if(v.getId() == R.id.btn_main_signIn){
                Log.d(TAG, "onClick: btn_main_signIn");
                Log.d(TAG, "onClick: checkBox_flag = "+flag_checkBox);
                account = edit_main_account.getText().toString();
                password = edit_main_password.getText().toString();

                if(checkEditViewEmpty()){
                    if (!flag_checkBox){
                        Toast.makeText(getApplicationContext(),"未同意服務條款",Toast.LENGTH_SHORT).show();
                    }else {
                        if(checkAccount() && checkPassword()){
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            editor = getPrefs.edit();
                            editor.putString("userName", nameBuf[correctCunt]);
                            editor.putString("userAccount", accountBuf[correctCunt]);
                            editor.putString("userPicture", pictureBuf[correctCunt]);
                            editor.apply();
                            startActivity(intent);
                        }else {
                            Toast.makeText(getApplicationContext(),"帳號密碼錯誤",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }else if(v.getId() == R.id.img_main_watchPassword){
                Log.d(TAG, "onClick: img_main_watchPassword");
                if(flag_password){
                    edit_main_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    flag_password = false;
                }else {
                    edit_main_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    flag_password = true;
                }
            }else if(v.getId() == R.id.txt_main_forgetPassword){
                Log.d(TAG, "onClick: txt_main_forgetPassword");
                Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                startActivity(intent);
            }
        }
    };

    private boolean checkAccount(){
        for (int i=0; i<userCunt ; i++){
            if(account.equals(accountBuf[i])){
                correctCunt = i;
                return true;
            }
        }
        return false;
    }

    private boolean checkPassword(){
        for (int i=0; i<userCunt ; i++){
            if(password.equals(passwordBuf[i])){
                return true;
            }
        }
        return false;
    }

    private boolean checkEditViewEmpty(){
        if(TextUtils.isEmpty(account)){
            textInput_main_account_layout.setError("請輸入帳號");
            textInput_main_password_layout.setError("");
            return false;
        }else if(TextUtils.isEmpty(password)){
            textInput_main_account_layout.setError("");
            textInput_main_password_layout.setError("請輸入密碼");
            return false;
        }else {
            textInput_main_account_layout.setError("");
            textInput_main_password_layout.setError("");
            return true;
        }
    }

    private void readPicture(){
        data_list = "png";
        pic_storage = storageReference.child("house1."+data_list);
        final File file;
        try {
            file = File.createTempFile("images","png");
            pic_storage.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //img2.setImageURI(Uri.fromFile(file));
                    Log.d(TAG, "onSuccess: Uri.fromFile(file) = "+Uri.fromFile(file));
                    Toast.makeText(getApplicationContext(),"讀取PASS",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"讀取Fail",Toast.LENGTH_SHORT).show();
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
                userCunt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+snapshot.toString());
                    Log.d(TAG, "onDataChange: userCunt = " + userCunt);
                    accountBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("account").getValue().toString();
                    passwordBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("password").getValue().toString();
                    nameBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("name").getValue().toString();
                    pictureBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("picture").getValue().toString();
                    userCunt++;
                }
                Log.d(TAG, "onDataChange: ---------userCunt = " + userCunt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}