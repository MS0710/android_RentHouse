package com.example.renthouse2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class ForgetPasswordActivity extends AppCompatActivity {
    private String TAG = "ForgetPasswordActivity";
    private Button btn_forget_search;
    private EditText edit_forget_account;
    private TextView txt_forget_password;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private int userCunt;
    private String[] accountBuf = new String[100];
    private String[] passwordBuf = new String[100];
    private int passwordCunt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initView();
    }

    private void initView(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserInfo");
        for (int i=0; i<100 ; i++){
            accountBuf[i] = "-1";
            passwordBuf[i] = "-1";
        }
        passwordCunt = 0;

        edit_forget_account = (EditText)findViewById(R.id.edit_forget_account);
        txt_forget_password = (TextView) findViewById(R.id.txt_forget_password);
        btn_forget_search = (Button) findViewById(R.id.btn_forget_search);

        btn_forget_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordCunt = 0;
                if (checkAccount()){
                    txt_forget_password.setText(passwordBuf[passwordCunt]);
                }else {
                    Toast.makeText(getApplicationContext(),"查無資料",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean checkAccount(){
        for (int i=0; i<100 ; i++){
            if (accountBuf[i].equals(edit_forget_account.getText().toString())){
                passwordCunt = i;
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        readExistingData();
    }

    private void readExistingData(){
        Log.d(TAG, "readExistingData: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userCunt = 0;
                for (int i=0; i<100 ; i++){
                    accountBuf[i] = "-1";
                    passwordBuf[i] = "-1";
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+snapshot.toString());
                    Log.d(TAG, "onDataChange: userCunt = " + userCunt);
                    accountBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("account").getValue().toString();
                    passwordBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("password").getValue().toString();
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