package com.example.renthouse2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomListAdapter extends BaseAdapter {
    private String TAG = "ChatRoomListAdapter";
    private List<ChatRoom> list = new ArrayList<>();
    private LayoutInflater listlayoutInflater;
    private StorageReference storageReference,pic_storage;

    public ChatRoomListAdapter(Context c, List<ChatRoom> _list){
        listlayoutInflater = LayoutInflater.from(c);
        this.list = _list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = listlayoutInflater.inflate(R.layout.chat_room_list,null);

        storageReference = FirebaseStorage.getInstance().getReference();
        //設定自訂樣板上物件對應的資料。
        TextView txt_chatRoom_name = (TextView) convertView.findViewById(R.id.txt_chatRoom_name);
        TextView txt_chatRoom_msg = (TextView) convertView.findViewById(R.id.txt_chatRoom_msg);
        ImageView img_chatRoom_picture = (ImageView) convertView.findViewById(R.id.img_chatRoom_picture);

        txt_chatRoom_name.setText(list.get(position).getName());
        txt_chatRoom_msg.setText(list.get(position).getMsg());

        pic_storage = storageReference.child(list.get(position).getName()+".png");
        Log.d(TAG, "getView: list.get(position).getName() = "+list.get(position).getName());
        final File file;
        try {
            file = File.createTempFile("images","png");
            pic_storage.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: file = "+file);
                    img_chatRoom_picture.setImageURI(Uri.fromFile(file));
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
        return convertView;
    }
}
