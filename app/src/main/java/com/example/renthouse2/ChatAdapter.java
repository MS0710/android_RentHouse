package com.example.renthouse2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private String TAG = "ChatAdapter";
    private List<ChatBean> chatBeanList;
    private LayoutInflater inflater;
    private Context context;
    private StorageReference storageReference,pic_storage;

    public ChatAdapter(Context _context, List<ChatBean> chatBeans){
        this.chatBeanList=chatBeans;
        this.inflater=LayoutInflater.from(_context);
        this.context = _context;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        storageReference = FirebaseStorage.getInstance().getReference();

        ChatViewHolder holder;
        //根据ViewType值创建不同的ViewHolder
        if(viewType==0) {
            holder = new ChatViewHolder(LayoutInflater.from(
                    context).inflate(R.layout.item_response, parent,
                    false));
        }
        else{
            holder = new ChatViewHolder(LayoutInflater.from(
                    context).inflate(R.layout.item_send, parent,
                    false));
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return chatBeanList.get(position).getType();//**返回集合Bean里的值作为标记值将传递到onCreateViewHolder里
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        //这里只是简单的实现了设置聊天内容并未对头像处理
        if(chatBeanList.get(position).getType()==0) {
            holder.tvResponse.setText(chatBeanList.get(position).getContent());
            holder.txt_resopense_time.setText(chatBeanList.get(position).getTime());
            pic_storage = storageReference.child(chatBeanList.get(position).getName()+".png");
            final File file;
            try {
                file = File.createTempFile("images","png");
                pic_storage.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: file = "+file);
                        holder.img_resopense_picture.setImageURI(Uri.fromFile(file));
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
        }else {
            holder.tvSend.setText(chatBeanList.get(position).getContent());
            holder.txt_send_time.setText(chatBeanList.get(position).getTime());
            pic_storage = storageReference.child(chatBeanList.get(position).getName()+".png");
            final File file;
            try {
                file = File.createTempFile("images","png");
                pic_storage.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: file = "+file);
                        holder.img_send_picture.setImageURI(Uri.fromFile(file));
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
    }

    @Override
    public int getItemCount() {
        return chatBeanList.size();
    }
    //簡單實作的ViewHolder，開發中最好再做一些最佳化
    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView tvResponse;
        TextView tvSend;
        TextView txt_resopense_time;
        TextView txt_send_time;
        ImageView img_resopense_picture;
        ImageView img_send_picture;

        public ChatViewHolder(View view) {
            super(view);
            tvResponse = (TextView) view.findViewById(R.id.tv_resopense);
            tvSend=(TextView)view.findViewById(R.id.tv_send);
            txt_resopense_time=(TextView)view.findViewById(R.id.txt_resopense_time);
            txt_send_time=(TextView)view.findViewById(R.id.txt_send_time);
            img_resopense_picture=(ImageView) view.findViewById(R.id.img_resopense_picture);
            img_send_picture=(ImageView) view.findViewById(R.id.img_send_picture);
        }
    }
}
