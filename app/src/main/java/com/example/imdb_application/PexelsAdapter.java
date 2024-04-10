package com.example.imdb_application;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.List;

public class PexelsAdapter extends RecyclerView.Adapter<PexelsAdapter.PexelsViewHolder> {

    private List<PexelsPhoto> photoList;
    private Context context;
    private OnItemClickListener onItemClickListener; // Add this member variable
    private OnImageClickListener onImageClickListener; // Add this member variable

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    public PexelsAdapter(List<PexelsPhoto> photoList, Context context) {
        this.photoList = photoList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) { // Add this method
        this.onItemClickListener = listener;
    }

    public void setOnImageClickListener(OnImageClickListener listener) { // Add this method
        this.onImageClickListener = listener;
    }

    @NonNull
    @Override
    public PexelsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PexelsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PexelsViewHolder holder, int position) {
        PexelsPhoto photo = photoList.get(position);
        holder.photographerName.setText(photo.getPhotographer());
        Glide.with(context).load(photo.getSrc().getMedium()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(position);
                        Log.d("PexelsAdapter", "Item clicked at position: " + position);
                    }
                }
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onImageClickListener.onImageClick(position);
                    }
                }
            }
        });
    }

    public PexelsPhoto getItem(int position) {
        if (position < photoList.size()) {
            return photoList.get(position);
        }
        return null;
    }

    public void setPhotoList(List<PexelsPhoto> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    static class PexelsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView photographerName;

        PexelsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewFav);
            photographerName = itemView.findViewById(R.id.photographerName);
        }
    }
}
