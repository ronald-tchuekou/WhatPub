/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */

package com.whatpub.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.whatpub.Controllers.Controller;
import com.whatpub.Models.Annonce;
import com.whatpub.R;
import com.whatpub.activities.DeletePublicationsActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.whatpub.R.color.card_green;

class DeletePubViewHolder extends RecyclerView.ViewHolder {

    TextView post_info_del, name_annonce_del, delais_annonce_del, small_annonce_del;
    androidx.appcompat.widget.AppCompatCheckBox checkBox;
    CardView cardView;

    DeletePubViewHolder(final View itemView) {
        super(itemView);
        cardView = (CardView) itemView;
        post_info_del = itemView.findViewById(R.id.post_info_del);
        name_annonce_del = itemView.findViewById(R.id.name_annonce_del);
        delais_annonce_del = itemView.findViewById(R.id.delais_annonce_del);
        small_annonce_del = itemView.findViewById(R.id.small_annonce_del);
        checkBox = itemView.findViewById(R.id.pub_one);
    }
}

public class DeletePub_adapter extends  RecyclerView.Adapter<DeletePubViewHolder> {

    private Context context;
    private List<Annonce> annonces;
    private List<DeletePubViewHolder> allViewHolder;
    private final boolean[] selectedItems;
    private static final String TAG = "DeletePub_adapter";

    public DeletePub_adapter(Context context, List<Annonce> annonces) {
        this.context = context;
        this.annonces = annonces;
        this.allViewHolder = new ArrayList<>();
        this.selectedItems = new boolean[annonces.size()];
    }

    @NonNull
    @Override
    public DeletePubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.single_pub_selection, parent, false);
        return new DeletePubViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeletePubViewHolder holder, final int position) {
        allViewHolder.add(holder); // Recuperation de tous les vues contenu dans l'adaptateur.

        String terminer = annonces.get(position).getTerminer();
        String heure = annonces.get(position).getHeure();
        Calendar date = Controller.getInstance().toDate(annonces.get(position).getDelais(), heure);
        String delais = context.getResources().getString(R.string.delais) +" "+ DateFormat.getDateInstance().format(date.getTime()) + " à " + heure;

        changeBackground(holder, Boolean.parseBoolean(terminer));

        holder.name_annonce_del.setText(getSmallString(annonces.get(position).getNom_annonce(),25));
        holder.delais_annonce_del.setText(delais);
        holder.small_annonce_del.setText(getSmallString(
                annonces.get(position).getDescription().replace("\n\n", " "),
                40)
        );

        holder.checkBox.setChecked(false);
        if (selectedItems[position]){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }

        // Le clique sur un item de la liste.
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedItem(holder, position);
            }
        });

    }

    /**
     * Fonction qui permet de recuperer le nombre d'item cheched.
     * @return nombre d'item checked.
     */
    private int getCountCheck () {
        int number = 0;
        for (boolean selectedItem : selectedItems) {
            if (selectedItem)
                number++;
        }
        return number;
    }

    /**
     * Fonction qui permet de reduire une chaîne de caractères.
     * @param description chaîne de caractère.
     * @param max Nombre de caractères maximal.
     * @return chaîne de caractère reduie.
     */
    private String getSmallString(String description, int max) {

        if(description.length() > max)
            return description.substring(0, max-3)+"...";
        return description;
    }

    /**
     * Fonction qui permet de selectionner ou de deselectionner tous les publications.
     */
    public void selectAll (final CheckBox selectedAll){
        selectedAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAll.isChecked()){
                    for (DeletePubViewHolder holder : allViewHolder){
                        holder.checkBox.setChecked(true);
                    }
                    for (int i=0; i<selectedItems.length; i++)
                        selectedItems[i] = true;
                }else{
                    for (DeletePubViewHolder holder : allViewHolder){
                        holder.checkBox.setChecked(false);
                    }
                    for (int i=0; i<selectedItems.length; i++)
                        selectedItems[i] = false;
                }

                updateViewCount();
            }

        });
    }

    /**
     * Fonction qui peremt de selectionner un élément de la liste.
     */
    private void checkedItem (DeletePubViewHolder holder, int position){
        if (holder.checkBox.isChecked()) {
            holder.checkBox.setChecked(false);
            selectedItems[position] = false;
        }else {
            holder.checkBox.setChecked(true);
            selectedItems[position] = true;
        }
        if (annonces.size() != getCountCheck())
            DeletePublicationsActivity.selectAll.setChecked(false);
        else
            DeletePublicationsActivity.selectAll.setChecked(true);
        updateViewCount();
        notifyDataSetChanged();
    }

    /**
     * Fonction qui permet de faire la mis à jours de la vue qui affiche le nombre d'annonces selectionnées.
     */
    private void updateViewCount() {
        int number = 0;
        for (boolean selectedItem : selectedItems) {
            if (selectedItem)
                number++;
        }
        String count = ""+number;
        DeletePublicationsActivity.nb_selection_count.setText(count);
    }

    @Override
    public int getItemCount() { return annonces.size(); }

    @Override
    public int getItemViewType(int position) { return super.getItemViewType(position); }

    /**
     * Fonction qui permet de changer la couleur de fond du cardView.
     * @param holder variable qui contient tous les vues.
     * @param isFinish booleen qui indique le status de la publication.
     */
    private void changeBackground (DeletePubViewHolder holder, Boolean isFinish) {
        if (isFinish) {
            holder.cardView.setBackground(context.getResources().getDrawable(card_green));
            holder.post_info_del.setTextColor(context.getResources().getColor(R.color.green));
            holder.post_info_del.setText(context.getResources().getString(R.string.terminer));
        }else {
            holder.cardView.setBackground(context.getResources().getDrawable(R.color.card_red));
            holder.post_info_del.setTextColor(context.getResources().getColor(R.color.red));
            holder.post_info_del.setText(context.getResources().getString(R.string.encours));
        }
    }

    /**
     * Fonction qui permet de recuperer la liste des éléments selectionnés.
     * @return Liste des annonces checked.
     */
    public List<Annonce> getAnnonceChecked() {
        List<Annonce> result = new ArrayList<>();
        for (int i=0; i<selectedItems.length; i++){
            if (selectedItems[i]){
                result.add(annonces.get(i));
            }
        }
        Log.d(TAG, "Result : "+result.toString());
        return result;
    }
}
