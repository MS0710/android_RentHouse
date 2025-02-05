package com.example.renthouse2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatWindowActivity extends AppCompatActivity {
    
    private String TAG = "";
    private EditText edit_chatWindow_msg;
    private Button btn_chatWindow_send;
    private TextView txt_chatWindow_otherUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userName;
    private String otherName;
    private RecyclerView chatWindow_recycler;
    private List<ChatBean> chatBeanList;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        initView();
    }

    private void initView(){
        otherName = getIntent().getStringExtra("otherName");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ChatInfo");

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        userName = getPrefs.getString("userName", "");

        Log.d(TAG, "initView: otherName = " +otherName);
        Log.d(TAG, "initView: userName = " +userName);

        edit_chatWindow_msg = (EditText) findViewById(R.id.edit_chatWindow_msg);
        txt_chatWindow_otherUser = (TextView) findViewById(R.id.txt_chatWindow_otherUser);
        btn_chatWindow_send = (Button) findViewById(R.id.btn_chatWindow_send);
        btn_chatWindow_send.setOnClickListener(onClick);
        txt_chatWindow_otherUser.setText(otherName);

        chatWindow_recycler = (RecyclerView) findViewById(R.id.chatWindow_recycler);
        chatBeanList=new ArrayList<>();
        chatWindow_recycler.setLayoutManager(new LinearLayoutManager(this));
        //设置adapter
        chatAdapter = new ChatAdapter(this, chatBeanList);
        chatWindow_recycler.setAdapter(chatAdapter);
        //设置默认动画
        chatWindow_recycler.setItemAnimator(new DefaultItemAnimator());

    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_chatWindow_send){
                Log.d(TAG, "onClick: btn_chatWindow_send");
                sendMsg();
            }
        }
    };

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
                String msg = "";
                String userName1;
                String userName2;
                String time;
                chatBeanList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren() ){
                    Log.d(TAG, "onDataChange: "+ds.getValue().toString());
                    Log.d(TAG, "onDataChange: "+ds.child("message").getValue().toString());
                    msg = ds.child("message").getValue().toString();
                    userName1 = ds.child("userName").getValue().toString();
                    userName2 = ds.child("otherSide").getValue().toString();
                    time = ds.child("time").getValue().toString();
                    if(userName1.equals(userName) && userName2.equals(otherName)){
                        Log.d(TAG, "onDataChange: right");
                        chatBeanList.add(new ChatBean(msg,null,1,userName1,time));
                    }else if(userName1.equals(otherName) && userName2.equals(userName)){
                        Log.d(TAG, "onDataChange: left");
                        chatBeanList.add(new ChatBean(msg,null,0,userName2,time));
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMsg(){
        String timeStamp;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        Log.d(TAG, "Calendar取得目前日期"+year+"年"+month+"月"+day+"日"+hour+":"+minute+":"+second);

        timeStamp = ""+year+"/"+month+"/"+day+"/"+hour+":"+minute;

        String key = myRef.push().getKey();
        if(edit_chatWindow_msg.getText().equals("")){
            Toast.makeText(getApplicationContext(),"請輸入訊息",Toast.LENGTH_SHORT).show();
        }else {
            String Msg = edit_chatWindow_msg.getText().toString();
            ChatMsg chatMsg = new ChatMsg(userName,otherName,Msg,timeStamp);
            myRef.child(key).setValue(chatMsg);
            edit_chatWindow_msg.setText("");
        }

    }
}