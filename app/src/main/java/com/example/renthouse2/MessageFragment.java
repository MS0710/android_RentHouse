package com.example.renthouse2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "MessageFragment";
    private ListView lv_message_list;
    private List<ChatRoom> list;
    private ChatRoomListAdapter chatRoomListAdapter;
    private LinearLayout ll_message_noInfoTip;
    private FirebaseDatabase database1;
    private DatabaseReference myRef1;
    private String currentName;
    private String[] tempName = new String[5];
    private String[] tempMsg = new String[5];
    private int userCunt;
    private int tempFlag;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
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
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        currentName = getPrefs.getString("userName", "");

        tempFlag = 0;
        /*for (int i=0; i<2; i++){
            tempName[i] = "-1";
            tempMsg[i] = "-1";
        }*/

        //userCunt = 0;
        for (int i=0;i<5;i++){
            tempName[i] = "-1";
            tempMsg[i] = "-1";
        }

        database1 = FirebaseDatabase.getInstance();
        myRef1 = database1.getReference("ChatInfo");

        lv_message_list = (ListView) view.findViewById(R.id.lv_message_list);
        ll_message_noInfoTip = (LinearLayout) view.findViewById(R.id.ll_message_noInfoTip);
        list = new ArrayList<>();

        chatRoomListAdapter = new ChatRoomListAdapter(getContext(),list);
        lv_message_list.setAdapter(chatRoomListAdapter);
        lv_message_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: position = "+position);
                Intent intent = new Intent(getContext(), ChatWindowActivity.class);
                intent.putExtra("otherName",list.get(position).getName());
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
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                String userName2,userName,msg;
                userCunt = 0;
                //Log.d(TAG, "onDataChange: user001 ="+dataSnapshot.child("user001").child("account").getValue().toString());
                //Log.d(TAG, "onDataChange: user002 ="+dataSnapshot.child("user002").child("account").getValue().toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //Log.d(TAG, "onDataChange: "+snapshot.toString());
                    userName = snapshot.child("userName").getValue().toString();
                    userName2 = snapshot.child("otherSide").getValue().toString();
                    msg = snapshot.child("message").getValue().toString();

                    setUserList(userName,userName2);
                    checkUser2(userName,userName2,msg);

                }

                for (int i=0 ;i<5; i++){
                    Log.d(TAG, "onDataChange: tempName["+i+"] = "+tempName[i]);
                    if(!tempName[i].equals("-1")){
                        ChatRoom chatRoom = new ChatRoom(tempName[i],tempMsg[i]);
                        list.add(chatRoom);
                    }
                }
                chatRoomListAdapter.notifyDataSetChanged();
                if (list.isEmpty()){
                    ll_message_noInfoTip.setVisibility(View.VISIBLE);
                }else {
                    ll_message_noInfoTip.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUserList(String send,String receive){
        if(send.equals(currentName) || receive.equals(currentName)){
            for (int i=0 ;i<5; i++){
                if (tempName[i].equals(send)){
                    return;
                }
            }
            if(!send.equals(currentName)){
                tempName[tempFlag] = send;
                tempFlag++;
                if (tempFlag>4){
                    tempFlag = 4;
                }
            }

            for (int i=0 ;i<5; i++){
                if (tempName[i].equals(receive) ){
                    return;
                }
            }
            if(!receive.equals(currentName)){
                tempName[tempFlag] = receive;
                tempFlag++;
                if (tempFlag>4){
                    tempFlag = 4;
                }
            }
        }
    }

    private void checkUser2(String send,String receive,String _msg){
        if(send.equals(currentName) || receive.equals(currentName)){
            for (int i=0 ;i<5; i++){
                if (tempName[i].equals(send)){
                    Log.d(TAG, "checkUser: in");
                    tempMsg[i] = _msg;
                }
            }

            for (int i=0 ;i<5; i++){
                if (tempName[i].equals(receive)){
                    Log.d(TAG, "checkUser: in");
                    tempMsg[i] = _msg;
                }
            }
        }

    }
}