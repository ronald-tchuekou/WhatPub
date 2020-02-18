/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */
package com.whatpub.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whatpub.Controllers.Controller;
import com.whatpub.DataBase.DatabaseManager;
import com.whatpub.Models.Annonce;
import com.whatpub.Models.Photo;
import com.whatpub.R;
import com.whatpub.imagesManage.ImagesManager;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DisplayPublicationActivity extends AppCompatActivity {

    /**
     * Fields , récupération des vues pour l'affichage de la publication.
     */
    private TextView tvName, tvDescription, tvDelais, tvNbDescription, tvStatus;
    private LinearLayout imageGridView;
    private TextView textView_image, nb_images;
    private ImageView image1, image2, image3;

    /**
     * Identifiant de la publication en cours de consultation.
     */
    private int id_annonce_displayed;
    DatabaseManager databaseManager;
    private static int ACTIVITY_EDIT = 120;
    private static final String TAG = "DisplayPublication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_publication);
        Toolbar toolbar = findViewById(R.id.toolbarAct_display);
        this.setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.titleActivityDisplay));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        assert getIntent().getExtras() != null;
        id_annonce_displayed = this.getIntent().getExtras().getInt("id_annonce");

        Log.d(TAG, "Id de l'annonce = "+id_annonce_displayed);

        initViews();
        getDatas();
    }

    /**
     * Fonction qui permet d'initialiser les vues de l'activité.
     */
    private void initViews() {
        tvName = findViewById(R.id.display_name);
        tvDelais = findViewById(R.id.display_delais);
        tvNbDescription = findViewById(R.id.display_nb_repetition);
        tvDescription = findViewById(R.id.display_description);
        tvStatus = findViewById(R.id.display_status);
        imageGridView = findViewById(R.id.imageGridView);
        textView_image = findViewById(R.id.textView_image);
        nb_images = findViewById(R.id.nb_images);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
    }

    /**
     * Fonction qui permet de recuperer les informations sur la publication à afficher les détailles.
     */
    public void getDatas() {
        databaseManager = new DatabaseManager(this);
        Annonce annonce = databaseManager.getAnnonce(id_annonce_displayed);

        String heure = annonce.getHeure();
        Calendar date = Controller.getInstance().toDate(annonce.getDelais(), heure);
        String delais = this.getResources().getString(R.string.delais) +" "+ DateFormat.getDateInstance().format(date.getTime()) + " à " + heure;
        String repeat = getString(R.string.repeter)+" "+annonce.getNb_repetition()+" "+getString(R.string.fois);
        String description = getString(R.string.description_annonce)+annonce.getDescription();
        String status = getString(R.string.status)+" "+getStatus(Boolean.valueOf(annonce.getTerminer()));

        // Les images
        List<String> images = new ArrayList<>();
        List photos = databaseManager.getPhotos(id_annonce_displayed);
        for (int i=0; i<photos.size(); i++){
            Photo photo = (Photo) photos.get(i);
            images.add(photo.getNom_photo());
            Log.d(TAG, "Nom de la photo : "+photo.getNom_photo());
        }

        // Adapter les images.
        adaptImagesChecked(images);

        // Clique sur la grille des images.
        listenrOnGridView(images);

        tvName.setText(annonce.getNom_annonce());
        tvDelais.setText(delais);
        tvNbDescription.setText(repeat);
        tvDescription.setText(description);
        tvStatus.setText(status);
    }

    private String getStatus (Boolean value) {
        return value ? getResources().getString(R.string.terminer) : getResources().getString(R.string.encours);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.delete_display:
                AlertDialog.Builder confirmedDeleted = new AlertDialog.Builder(this);
                confirmedDeleted.setTitle(R.string.titleConfirmDelet);
                confirmedDeleted.setMessage(R.string.msgConfirmDelet);
                confirmedDeleted.setPositiveButton(R.string.yesBtnDelet, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseManager.deleteFromAnnonce(new int[]{id_annonce_displayed});
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
                return true;
            case R.id.edit_display:
                Intent mIntent =  new Intent(this, AddPublicationActivity.class);
                mIntent.putExtra("id_annonce_edit", id_annonce_displayed);
                startActivityForResult(mIntent, ACTIVITY_EDIT);
                return true;

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_EDIT){
            getDatas();
        }
    }

    /**
     * Fonction qui permet d'adapter les images chosit.
     * @param images Liste des images.
     */
    private void adaptImagesChecked(List images) {
        if (images.size() != 0){
            imageGridView.setVisibility(View.VISIBLE);
            textView_image.setVisibility(View.VISIBLE);
            hideImageView();
            showImage(images);
            Log.d(TAG, "Nombre d'image selectionnée : "+ images.size());
        }else{
            imageGridView.setVisibility(View.GONE);
            textView_image.setVisibility(View.GONE);
            Toast.makeText(this, getResources().getString(R.string.emptyImage), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fonction qui masque les imageViews.
     */
    private void hideImageView() {
        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        findViewById(R.id.last_images).setVisibility(View.GONE); // The relative layout that content last image.
        nb_images.setVisibility(View.GONE);
    }

    /**
     * Fonction qui affiche les imageViews.
     * @param images liste des images.
     */
    private void showImage(final List images) {
        for (int i=0; i<images.size(); i++){
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            float proportion = 0.6f;
            switch (i){
                case 0:
                    Bitmap bmp1 = BitmapFactory.decodeFile((String) images.get(i), options);
                    bmp1 = ImagesManager.resizeBitmap(bmp1, proportion, this.getWindowManager());
                    image1.setVisibility(View.VISIBLE);
                    image1.setImageBitmap(bmp1);
                    image1.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 1:
                    Bitmap bmp2 = BitmapFactory.decodeFile((String) images.get(i), options);
                    bmp2 = ImagesManager.resizeBitmap(bmp2, proportion, this.getWindowManager());
                    image2.setVisibility(View.VISIBLE);
                    image2.setImageBitmap(bmp2);
                    image2.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 2:
                    Bitmap bmp3 = BitmapFactory.decodeFile((String) images.get(i), options);
                    bmp3 = ImagesManager.resizeBitmap(bmp3, proportion, this.getWindowManager());
                    findViewById(R.id.last_images).setVisibility(View.VISIBLE);
                    image3.setImageBitmap(bmp3);
                    image3.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                default:
                    nb_images.setVisibility(View.VISIBLE);
                    String countImages = "+"+(images.size()-2);
                    nb_images.setText(countImages);
                    break;
            }
        }
    }

    /**
     * Fonction qui permet d'écouter le clique sur le gridView.
     * @param images Liste des images.
     */
    private void listenrOnGridView (final List images) {
        imageGridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "listenerOnGridView: To view all the images.");
                Intent intentViewImage = new Intent(getApplicationContext(), DisplayImages.class);
                intentViewImage.putExtra("visualisation", true);
                intentViewImage.putExtra("imagesChecked", (Serializable) images);
                startActivity(intentViewImage);
            }
        });
    }

}
