/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */

package com.whatpub.Adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.whatpub.Controllers.Controller;
import com.whatpub.Models.Annonce;
import com.whatpub.R;
import com.whatpub.activities.MainActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.whatpub.R.color.card_green;

class pubViewHolder extends RecyclerView.ViewHolder {

    TextView post_info, name_annonce, delais_annonce, small_annonce;
    CardView cardView;

    pubViewHolder(View itemView) {
        super(itemView);
        post_info = itemView.findViewById(R.id.post_info);
        name_annonce = itemView.findViewById(R.id.name_annonce);
        delais_annonce = itemView.findViewById(R.id.delais_annonce);
        small_annonce = itemView.findViewById(R.id.small_annonce);
        cardView = (CardView) itemView;
    }

}

public class Pub_adapter extends  RecyclerView.Adapter<pubViewHolder> implements Filterable {

    private Context context;
    public static List annonces;
    private List<Annonce> annoncesFull;
    private static final String TAG = "Pub_adapter";

    /**
     * Constructeur de la classe.
     * @param context Context de l'application.
     * @param annonces Liste des annonces.
     */
    public Pub_adapter(Context context, List<Annonce> annonces) {
        this.context = context;
        Pub_adapter.annonces = annonces;
        this.annoncesFull = new ArrayList<>(annonces);
        Log.d(TAG, "Appel du constructeur de l'adaptateur.");
    }

    @NonNull
    @Override
    public pubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.single_pub, parent, false);
        return new pubViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull pubViewHolder holder, final int position) {
        Annonce annonce = (Annonce) annonces.get(position);
        String terminer = annonce.getTerminer();
        Calendar date = Controller.getInstance().toDate(annonce.getDelais(), annonce.getHeure());
        String delais = context.getResources().getString(R.string.delais) +" "+ DateFormat.getDateInstance().format(date.getTime()) + " à " + annonce.getHeure();
        Log.d(TAG, "onBindViewHolder: "+date.getTime().toString()+ " ; delais : "+annonce.getDelais());

        changeBackground(holder, Boolean.parseBoolean(terminer));

        holder.name_annonce.setText(getSmallString(annonce.getNom_annonce(),25));
        holder.delais_annonce.setText(delais);
        holder.small_annonce.setText(getSmallString(annonce.getDescription().replace("\n\n", " "),
                40));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.goToDisplay(annonces, position);
            }
        });
    }

    private String getSmallString(String description, int max) {

        if(description.length() > max)
            return description.substring(0, max-3)+"...";
        return description;
    }

    @Override
    public int getItemCount() { return annonces.size(); }

    @Override
    public Filter getFilter() {
        return annonceFilter;
    }

    private Filter annonceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Annonce> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(annoncesFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Annonce annonce : annoncesFull){
                    if (annonce.getNom_annonce().toLowerCase().contains(filterPattern)){
                        filteredList.add(annonce);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            annonces.clear();
            if (annonces.addAll((List) results.values))
                Log.d(TAG, "Elements ajouter dans la liste des annonces");
            MainActivity.notifyAdapters(context);
            MainActivity.showEmptyDataView(annonces);
        }
    };

    /**
     * Fonction qui retourne la liste des annonces en cours.
     * @return Liste des annonces.
     */
    public List getAnnonces () {
        return annonces;
    }


    /**
     * Fonction qui permet de changer la couleur de fond du cardView.
     * @param holder variable qui contient tous les vues.
     * @param isFinish booleen qui indique le status de la publication.
     */
    private void changeBackground (pubViewHolder holder, Boolean isFinish) {
        if (isFinish) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.cardView.setBackground(context.getResources().getDrawable(card_green));
            }
            holder.post_info.setTextColor(context.getResources().getColor(R.color.green));
            holder.post_info.setText(context.getResources().getString(R.string.terminer));
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.cardView.setBackground(context.getResources().getDrawable(R.color.card_red));
            }
            holder.post_info.setTextColor(context.getResources().getColor(R.color.red));
            holder.post_info.setText(context.getResources().getString(R.string.encours));
        }
    }
}
