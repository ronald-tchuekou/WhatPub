/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */

package com.whatpub.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.whatpub.Adapter.DeletePub_adapter;
import com.whatpub.DataBase.DatabaseManager;
import com.whatpub.Models.Annonce;
import com.whatpub.R;
import com.whatpub.ui.all_pub.All_pub;
import com.whatpub.ui.pub_course.Pub_course;
import com.whatpub.ui.pub_finish.Pub_finish;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeletePublicationsActivity extends AppCompatActivity {

    private static final String TAG = "DeletePublication";
    public static RecyclerView recyclerView;
    public static CheckBox selectAll;
    private  DeletePub_adapter adapter;
    private static DatabaseManager databaseManager;
    private List<Annonce> annonces;
    private List<Annonce> annoncesChecked;
    private int id_currentFragment;
    public TextView emptyData;
    public static TextView nb_selection_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_publications);

        Toolbar toolbar = findViewById(R.id.toolbarAct_deleted);
        this.setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.deleted);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        assert getIntent().getExtras() != null;
        id_currentFragment = getIntent().getExtras().getInt("id_currentFragment");

        initDatabase();
        setCurrentListAnnonce();

        initViews();
        initAdapter();

        adapter.selectAll(selectAll);

        if(annonces.size() == 0)
            emptyData.setText(R.string.emptyData);
        else
            emptyData.setText("");
    }

    /**
     * Fonction qui permet de charger la liste des annonces courantes.
     */
    private void setCurrentListAnnonce() {
        if(id_currentFragment == All_pub.ID_FRAG) {
            annonces = databaseManager.getAllAnnonce();
        }
        else if (id_currentFragment == Pub_course.ID_FRAG) {
            annonces = databaseManager.getAllAnnonceInCours();
        }
        else if (id_currentFragment == Pub_finish.ID_FRAG) {
            annonces = databaseManager.getAllAnnonceFinish();
        }
    }

    /**
     * Fonction qui permet de supprimer les annonces selectionnées.
     */
    public void deletedItems() {
        for (Annonce annonce : annoncesChecked){
            int position = getPositionAnnonce(annonce);
            int[] ids = {annonce.getId_annonce()};
            recyclerView.getChildAt(position);
            if(databaseManager.deleteFromAnnonce(ids))
                Log.d(TAG, "Annonce supprimer : "+annonce.getNom_annonce());
            annonces.remove(position);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Foncion qui renvoie la position d'une annonce dans la liste des annonces.
     * @param annonce Annonce dont on veux la position.
     * @return Position de l'annonce dans la liste.
     */
    private int getPositionAnnonce(Annonce annonce) {
        for (int i=0; i<annonces.size(); i++)
            if(annonce.equals(annonces.get(i)))
                return i;
        return 0;
    }

    /**
     *Fonction qui permet d'initialiser la base de données.
     */
    private void initDatabase() {
        databaseManager = new DatabaseManager(this);
    }
    /**
     * Fonction qui permet d'initialiser les vues.
     */
    private void initViews() {
        recyclerView = findViewById(R.id.collectionDeletedPub);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        selectAll = findViewById(R.id.selectedAll);
        emptyData = findViewById(R.id.emptyData);
        nb_selection_count = findViewById(R.id.nb_selection_count);

    }

    /**
     *Fonction qu initialise l'adapteur.
     */
    private void initAdapter() {
        adapter = new DeletePub_adapter(this, annonces);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // recuperation des elements selectionnés.
        annoncesChecked = adapter.getAnnonceChecked();

        int id = item.getItemId();

        if (id == R.id.delete_delete) {
            //On verifie si au moins un element à été selectioné.
            if (annoncesChecked.size() != 0){
                AlertDialog.Builder confirmedDeleted = new AlertDialog.Builder(this);
                confirmedDeleted.setTitle(R.string.titleConfirmDelet);
                confirmedDeleted.setMessage(R.string.msgConfirmDelet);
                confirmedDeleted.setIcon(R.drawable.ic_wich_elt);
                confirmedDeleted.setPositiveButton(R.string.yesBtnDelet, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletedItems();
                        Toast.makeText(DeletePublicationsActivity.this,
                                R.string.deletedComplet, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                confirmedDeleted.setNegativeButton(R.string.noBtnDelet, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ici on ne fait rien du tout.
                    }
                });
                confirmedDeleted.show();
            }else{
                // Dans le cas où aucun element n'est selectionner, on envoie un avertissement
                // à l'utilisateur.
                AlertDialog.Builder notElement = new AlertDialog.Builder(this);
                notElement.setTitle(R.string.titleNotElement);
                notElement.setMessage(R.string.msgNotElement);
                notElement.setIcon(R.drawable.ic_wich_elt);
                notElement.setPositiveButton("OK", null);
                notElement.show();
            }
        }else
            finish();
        return true;
    }

}
