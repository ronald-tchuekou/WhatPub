/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */
package com.whatpub.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.whatpub.AsynckTasks.AsyncImageLoadAdapter;
import com.whatpub.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Class pour les vues a adapter dans la liste de photos.

class ImageViewHolder extends RecyclerView.ViewHolder {

    Button deleted_btn;
    ImageView image;

    ImageViewHolder(View itemView, boolean visualisation) {
        super(itemView);
        deleted_btn = itemView.findViewById(R.id.deleted_image);
        image = itemView.findViewById(R.id.image);
        if (!visualisation)
            deleted_btn.setVisibility(View.GONE);
    }
}


public class Image_adapter extends  RecyclerView.Adapter<ImageViewHolder>{

    private Context context;
    private List imageUris;
    private boolean visualisation;
    private WindowManager windowManager;

    public Image_adapter(Context context, List imageUris, WindowManager windowManager, boolean visualisation) {
        this.context = context;
        this.imageUris = imageUris;
        this.visualisation = visualisation;
        this.windowManager = windowManager;
    }

    @NonNull
    @Override
    public com.whatpub.Adapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.single_image, parent, false);
        return new com.whatpub.Adapter.ImageViewHolder(itemView, visualisation);
    }

    @Override
    public void onBindViewHolder(@NonNull com.whatpub.Adapter.ImageViewHolder holder, final int position) {
        holder.deleted_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUris.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Image supprimée !", Toast.LENGTH_SHORT).show();
            }
        });

        // Synchronisation de données.
        AsyncImageLoadAdapter imageLoad = new AsyncImageLoadAdapter(holder.image, null,
                (String) imageUris.get(position), windowManager, 1f);
        imageLoad.execute();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

}
