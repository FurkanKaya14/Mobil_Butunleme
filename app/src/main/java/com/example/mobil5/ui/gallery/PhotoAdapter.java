package com.example.mobil5.ui.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mobil5.R;
import com.example.mobil5.model.Photo;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<Photo> photoList;


    public PhotoAdapter(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public void setPhotos(List<Photo> photos) {
        this.photoList = photos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        holder.bind(photo);
    }

    @Override
    public int getItemCount() {
        return photoList == null ? 0 : photoList.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView photoImageView;
        private TextView labelTextView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            labelTextView = itemView.findViewById(R.id.labelTextView);
        }
        public void bind(@NonNull Photo photo) {
             photoImageView.setImageResource(photo.getImageUrl());
             labelTextView.setText(photo.getLabelName());
        }

    }
}
