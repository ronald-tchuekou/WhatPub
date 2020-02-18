/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */

package com.whatpub.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.whatpub.Controllers.Controller;
import com.whatpub.DataBase.DatabaseManager;
import com.whatpub.Models.Annonce;
import com.whatpub.Models.Groupe;
import com.whatpub.Models.Photo;
import com.whatpub.Pickers.DatePickerFragment;
import com.whatpub.Pickers.TimePickerFragment;
import com.whatpub.R;
import com.whatpub.imagesManage.ImagesManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class AddPublicationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "AddPublicationActivity";
    private static final int STORAGE_PERMISSION_CODE = 12;

    //FIELDS
    private EditText editName;
    private Button btnDelais;
    private Button btnTime;
    private Spinner spinner;
    private EditText description;
    private String spinnerText;
    private Controller controller;
    private DatabaseManager databaseManager;
    private LinearLayout imageGridView;
    private TextView textView_image, nb_images;
    private ImageView image1, image2, image3;

    // Caractéristiques d'une annonce.
    private int id;
    private String name_;
    private String create;
    private String delais;
    private String houre;
    private String terminer;
    private String description_;
    private int nb_repetition;
    private List<Photo> photos;
    private List<Groupe> groupes;
    private Button btn_image;
    private Button btn_group;
    public static final int ACTIVITY_CHOICE_IMAGE = 12;
    private static final int DISPLAY_IMAGES = 13;
    private static List images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_publication);

        images = new ArrayList<>();

        // Gestion de la toolbar.
        Toolbar toolbar = findViewById(R.id.toolbarAct_add);
        this.setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_add_pub);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Gérer la permission pour avoir accès à la base de données.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "La permission d'accès aux stockagex de la device est OK.");
        }else{
            requestStoragePermission(this);
        }

        // Initialisation des vues.
        initView();
        SpinnerSelection();

        // Clique pour la recuperation du delais sur le datePicker.
        btnDelais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date picker");
            }
        });

        // Clique pour la recuperation de l'heure sur le timePicker.
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "Time Picker");
            }
        });

        // Clique pour la recuperation des images.
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        // Gestion du clique pour la recuperation des images.
        listenerGroupBtn ();

        controller = Controller.getInstance();
        databaseManager = new DatabaseManager(this);

        // Recuperation des data stockées dans les intentions.
        getDatasIntent();

        // show the image.
        if (images.size() != 0){
            adaptImagesChecke(images);
        }

        //Display the images.
        imageGridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayImages();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String value = year+"-"+(month+1)+"-"+dayOfMonth;
        btnDelais.setText(value);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String value = (hourOfDay<10 ? ("0"+hourOfDay) : hourOfDay)+":"+(minute<10 ? ("0"+minute) : minute)+":00";
        btnTime.setText(value);
    }

    /**
     * Fonction qui permet de valider la permission d'accès à la base de données.
     * @param context Context de l'application.
     */
    private void requestStoragePermission(Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddPublicationActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(context)
                    .setTitle(R.string.titleRequestPermission)
                    .setMessage(R.string.msgRequestPermission)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AddPublicationActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                    STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton(R.string.cancle, null)
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(AddPublicationActivity.this,
                    new String [] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getString(R.string.permissionGranted), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, getResources().getString(R.string.permissionDinied), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Fonction qui permet de choisir les images.
     */
    private void chooseImage() {
        // Ici on vérifit si l'application à accès à la memoire du telephone.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(AddPublicationActivity.this, ChooseImages.class);
            startActivityForResult(intent, ACTIVITY_CHOICE_IMAGE);
        }else{
            requestStoragePermission(this);
        }
    }

    /**
     * Fonction qui permet d'afficher les images choisies par l'utilisateur.
     */
    private void displayImages() {
        Intent intent = new Intent(AddPublicationActivity.this, DisplayImages.class);
        intent.putExtra("imagesChecked", (Serializable) images);
        startActivityForResult(intent, DISPLAY_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_CHOICE_IMAGE && resultCode == RESULT_OK) {
            if (data != null){
                if (data.hasExtra("imagesChecked")){
                    images =  (List) data.getSerializableExtra("imagesChecked");
                    assert images != null;
                    adaptImagesChecke(images);
                }else{
                    Toast.makeText(this, "La variable data n'a pas de " +
                            "données avec la clé ''imagesCkecked''", Toast.LENGTH_LONG).show();
                }
            }else{
                Log.d(TAG, "Pas d'images reçus avec imagesChecked!!!");
            }
        }

        if(requestCode == DISPLAY_IMAGES && resultCode == RESULT_OK){
            if (data != null){
                if (data.hasExtra("newImagesChecked")){
                    images =  (List) data.getSerializableExtra("newImagesChecked");
                    assert images != null;
                    adaptImagesChecke(images);
                }else{
                    Toast.makeText(this, "La variable data n'a pas de " +
                            "données avec la clé ''newImagesChecked''", Toast.LENGTH_LONG).show();
                }
            }else{
                Log.d(TAG, "Pas d'images reçus avec newImagesChecked!!!");
            }
        }
    }

    /**
     * Fonction qui permet d'adapter les images chosit.
     * @param images Liste des images.
     */
    private void adaptImagesChecke(List images) {
        if (images.size() != 0){
            imageGridView.setVisibility(View.VISIBLE);
            textView_image.setVisibility(View.VISIBLE);
            hideImageView();
            showImage(images);
            btn_image.setVisibility(View.GONE);
            Log.d(TAG, "Nombre d'image selectionnée : "+ images.size());
        }else{
            imageGridView.setVisibility(View.GONE);
            textView_image.setVisibility(View.GONE);
            btn_image.setVisibility(View.VISIBLE);
            Toast.makeText(this, getResources().getString(R.string.emptyImage), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Fonction qui masque les imageViews.
     */
    private void hideImageView() {
        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        findViewById(R.id.last_images).setVisibility(View.GONE);
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
            final float proportion = 0.6f;
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
     * Fonction qui permet d'initialiser les parametre d'une annonce.
     */
    private void initData() {
        if (this.getIntent().hasExtra("id_annonce_edit")) {
            if (this.getIntent().getExtras() != null)
                id = this.getIntent().getExtras().getInt("id_annonce_edit");
        }else {
            id = 0;
        }
        name_ = editName.getText().toString();
        create = getCurrentDate();
        delais = btnDelais.getText().toString();
        houre = btnTime.getText().toString();
        terminer = "false";
        description_ = description.getText().toString();
        nb_repetition = controller.getRepetition(spinnerText);
        photos = null;
        groupes = null;
    }

    /**
     * Fonction qui permet de ceruperer la vue de saisie du nom de la publication.
     * @return Vue de saisie.
     */
    public EditText getName() {
        return editName;
    }
    /**
     * Fonction qui permet d'adapter le spinner (Liste de selections).
     */
    private void SpinnerSelection() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddPublicationActivity.this, R.array.repetions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(AddPublicationActivity.this);
    }
    /**
     * Fonction qui permet d'initialiser les views.
     */
    private void initView() {
        btnDelais = findViewById(R.id.txtEcheance);
        btnTime = findViewById(R.id.txtHoure);
        editName = findViewById(R.id.txtName);
        description = findViewById(R.id.txtDescription);
        spinner = findViewById(R.id.spinner_repeter);
        btn_image = findViewById(R.id.btn_image);
        btn_group = findViewById(R.id.btn_group);
        imageGridView = findViewById(R.id.imageGridView);
        textView_image = findViewById(R.id.textView_image);
        nb_images = findViewById(R.id.nb_images);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_cancel || id == android.R.id.home) {
            finish();
        }else if (id == R.id.add_save) {
            if (getIntent().hasExtra("id_annonce_edit"))
                updateListener();
            else
                saveListener();
        }
        return true;
    }
    /**
     * Fonction qui permet de faire la mis à jour d'une annonce dans la base de données.
     */
    private void updateListener() {
        initData(); // To initialized the data.
        if(controller.validateName(name_)){ // Si le nom de la publication est valide.
            if (!delais.toUpperCase().trim().equals(getResources().getString(R.string.date).trim().toUpperCase())){
                if (!houre.toUpperCase().trim().equals(getResources().getString(R.string.Houre).toUpperCase())) {
                    if (controller.cameBefore(delais, houre)){
                        if (!description_.equals("")){
                            if (databaseManager.updateAnnonce(new Annonce(this.id, toCapital(name_), create, delais, houre, terminer,
                                    description_, nb_repetition, photos, groupes))) {
                                if (databaseManager.deleteFromPhoto(databaseManager.getAllId(this.id)))
                                    Log.d(TAG, "Photos supprimées.");
                                inserImages(databaseManager.getLastId(toCapital(name_)));
                                finish();
                                Toast.makeText(this, getResources().getString(R.string.updatedSuccess), Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(this, "Erreur d'insertion dans la base de données !", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(this, "Indiquez la description de la publication !", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(this, "Le delais n'est pas correct, Veuillez choisir un delais supérieur !", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(this, "Indiquez une heure à la quelle cette publication doit être transmise !", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "Indiquez un délais pour cette publication !", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Indiquez le nom de la publication !", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Fonction qui permet d'inserer une annonce dans la base de données.
     */
    private void saveListener() {
        initData(); // To initialized the data.
        if(controller.validateName(name_)){ // Si le nom de la publication est valide.
            Log.d(TAG, toCapital(name_));
            if (!delais.toUpperCase().trim().equals(getResources().getString(R.string.date).trim().toUpperCase())){
                if (!houre.toUpperCase().trim().equals(getResources().getString(R.string.Houre).toUpperCase())) {
                    if (controller.cameBefore(delais, houre)){
                        if (!description_.isEmpty()){
                            if (databaseManager.insertAnnonce(new Annonce(this.id, toCapital(name_), create, delais, houre, terminer,
                                    description_, nb_repetition, photos, groupes))) {
                                inserImages(databaseManager.getLastId(toCapital(name_)));
                                finish();
                                Toast.makeText(this, getResources().getString(R.string.saveSuccess), Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(this, "Erreur d'insertion dans la base de données !", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(this, "Indiquez la description de la publication !", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(this, "Le delais n'est pas correct, Veuillez choisir un delais supérieur !", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(this, "Indiquez une heure à la quelle cette publication doit être transmise !", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "Indiquez un délais pour cette publication !", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Indiquez le nom de la publication !", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Fonction qui permet d'inserer les images d'une publication dans la base de données.
     * @param id_annonce Identifiant de l'annonce.
     */
    private void inserImages(int id_annonce) {
        if (images.size() != 0) {
            for (int i = 0; i < images.size(); i++) {
                Photo photo = new Photo(0, (String) images.get(i), id_annonce);
                Log.d(TAG, "Photo : "+photo.getNom_photo());
                if (databaseManager.insertPhoto(photo))
                    Log.d(TAG, "Photo non enregistrée ! ");
            }
        }
    }

    /**
     * Fonction qui premet de retourner la date courante .
     * @return Date courante sous forme de chaine de caractères.
     */
    public String getCurrentDate () {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        String strDate = year+"-"+month+"-"+dayOfMonth+" "+(hour<10 ? ("0"+hour) : hour)+":"+(minutes<10 ? ("0"+minutes) : minutes)+":"+seconds;
        Log.d(TAG, "The Current Date Is : "+strDate);
        return strDate;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerText = parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    /**
     * Fonction qui permet de recupérer les informations sur la publication à modifier.
     */
    public void getDatasIntent() {
        if (this.getIntent().getExtras() != null)
            if (this.getIntent().hasExtra("id_annonce_edit")){
                int id_annonce = this.getIntent().getExtras().getInt("id_annonce_edit");
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.titelEdit);
                Annonce annonce = databaseManager.getAnnonce(id_annonce);
                editName.setText(annonce.getNom_annonce());
                btnDelais.setText(annonce.getDelais());
                btnTime.setText(annonce.getHeure());
                spinner.setSelection(annonce.getNb_repetition()-1);
                description.setText(annonce.getDescription());

                List<String> imagesIntent = new ArrayList<>();
                List photos = databaseManager.getPhotos(id_annonce);
                for (int i=0; i<photos.size(); i++){
                    Photo photo = (Photo) photos.get(i);
                    imagesIntent.add(photo.getNom_photo());
                    Log.d(TAG, "getDataIntent () : Nom de la photo => "+photo.getNom_photo());
                }
                images = imagesIntent;

                adaptImagesChecke(imagesIntent);
            }
    }

    /**
     * Fonction qui permet d'ecouter le click sur le bouton de choix des groupes.
     */
    private void listenerGroupBtn() {
        btn_group.setEnabled(false); // On désactive le bouton de selection des groupes pour le moment.
        btn_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatsAppIntent();
                // TODO Récupération des groupes whatsapp pour les quelles on souhaite programmer la publication.
                Toast.makeText(AddPublicationActivity.this, "Gestion de la selection des groupes en cour !", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Fonction qui permet de créer une intention vers WhatsApp.
     */
    public void whatsAppIntent() {
        // Intent to open the application WhatsApp
        Intent toWhatsApp = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("whatsapp://chat");
        toWhatsApp.setData(uri);
        toWhatsApp.setPackage("com.whatsapp");
        startActivity(toWhatsApp);
    }

    /**
     * Fonction qui permet de mettre en majuscule la première lettre d'une chaîne de caratères.
     * @param words La chaîne de caractères.
     * @return Le nouveau mot.
     */
    private String toCapital (String words) {
        String[] tableString = words.trim().split(" ");
        StringBuilder result = new StringBuilder();
        for (String x : tableString){
            String firstLetter = ""+x.charAt(0);
            result.append(firstLetter.toUpperCase()).append(x.substring(1)).append(" ");
        }
        return result.toString();
    }

}
