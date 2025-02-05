package com.example.renthouse2;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class HouseListAdapter extends BaseAdapter {
    private String TAG = "HouseListAdapter";
    private List<House> list = new ArrayList<>();
    private LayoutInflater listlayoutInflater;
    private StorageReference storageReference,pic_storage;
    private ProgressDialog progressDialog;
    private Context context;

    public HouseListAdapter(Context c, List<House> _list,ProgressDialog progressDialog){
        listlayoutInflater = LayoutInflater.from(c);
        this.list = _list;
        this.context = c;
        this.progressDialog = progressDialog;
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

        ViewHolder viewHolder;
        storageReference = FirebaseStorage.getInstance().getReference();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.house_list, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.img_houseList_picture = convertView.findViewById(R.id.img_houseList_picture);
            viewHolder.txt_houseList_title = convertView.findViewById(R.id.txt_houseList_title);
            viewHolder.txt_houseList_tag1 = convertView.findViewById(R.id.txt_houseList_tag1);
            viewHolder.txt_houseList_tag2 = convertView.findViewById(R.id.txt_houseList_tag2);
            viewHolder.txt_houseList_tag3 = convertView.findViewById(R.id.txt_houseList_tag3);
            viewHolder.txt_houseList_note = convertView.findViewById(R.id.txt_houseList_note);
            viewHolder.txt_houseList_price = convertView.findViewById(R.id.txt_houseList_price);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        pic_storage = storageReference.child(list.get(position).getPicture());
        final File file;
        try {
            file = File.createTempFile("images","png");
            pic_storage.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: file = "+file);
                    viewHolder.img_houseList_picture.setImageURI(Uri.fromFile(file));
                    Log.d(TAG, "onSuccess: ");
                    progressDialog.dismiss();
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

        viewHolder.txt_houseList_title.setText(list.get(position).getTitle());
        viewHolder.txt_houseList_tag1.setText(list.get(position).getTag1());
        viewHolder.txt_houseList_tag2.setText(list.get(position).getTag2());
        viewHolder.txt_houseList_tag3.setText(list.get(position).getTag3());
        viewHolder.txt_houseList_note.setText(list.get(position).getNote());
        viewHolder.txt_houseList_price.setText(list.get(position).getPrice()+"/月");

        return convertView;
    }

    static class ViewHolder {
        ImageView img_houseList_picture;
        TextView txt_houseList_title;
        TextView txt_houseList_tag1;
        TextView txt_houseList_tag2;
        TextView txt_houseList_tag3;
        TextView txt_houseList_note;
        TextView txt_houseList_price;

    }
}
