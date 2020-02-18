package com.whatpub.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.whatpub.Adapter.Image_adapter;
import com.whatpub.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class DisplayImages extends AppCompatActivity {

    private static final String TAG = "DisplayImages";
    private Button add_image;
    private RecyclerView recyclerView;
    private static final int ACTIVITY_CHOICE_IMAGE = 1212;

    private Image_adapter adapter;
    private static List images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);

        this.setSupportActionBar((Toolbar) findViewById(R.id.toolbarAct_add_images));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
        initViews();
        initAdapter();
        //initialisation du clique le bouton.
        initClickOnBtn_photo();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }else if (id == R.id.termier) {
            if (!getIntent().hasExtra("visualistion")){
                Intent intent = new Intent();
                intent.putExtra("newImagesChecked", (Serializable) images);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
        return true;
    }

    private void initViews() {
        recyclerView = findViewById(R.id.list_images);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    /**
     * Fonction qui permet d'initialiser l'adapteur des listes d'images.
     */
    private void initAdapter() {
        boolean visualisation = true;
        if (getIntent().hasExtra("visualisation"))
            visualisation = false;
        adapter = new Image_adapter(getApplicationContext(), images, this.getWindowManager(), visualisation);
        recyclerView.setAdapter(adapter);
    }
    /**
     * Fonction qui premet d'écouter le clique sur le bouton d'ajout d'une image.
     */
    public void initClickOnBtn_photo () {
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    /**
     * Fonction qui permet de choisir une image dans la gallery.
     */
    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(DisplayImages.this, ChooseImages.class);
            startActivityForResult(intent, ACTIVITY_CHOICE_IMAGE);
        }
    }

    /**
     * Fonction qui permet d'initialiser les vues.
     */
    private void init() {
        images = (List) getIntent().getSerializableExtra("imagesChecked");
        add_image = findViewById(R.id.btn_add_images);
        RecyclerView recycleView = findViewById(R.id.list_images);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setHasFixedSize(true);
        if (getIntent().hasExtra("visualisation"))
            add_image.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_CHOICE_IMAGE && resultCode == RESULT_OK) {
            if (data != null){
                if (data.hasExtra("imagesChecked")){
                    List imagesChecked = (List) data.getSerializableExtra("imagesChecked");
                    assert imagesChecked != null;
                    images.addAll(imagesChecked);
                    notifyAdapter();
                    Log.d(TAG, "Ajout de "+imagesChecked.size()+" images");
                }else{
                    Toast.makeText(this, "La variable data n'a pas de " +
                            "données avec la clé ''imagesCkecked''", Toast.LENGTH_LONG).show();
                }
            }else{
                Log.d(TAG, "Pas dimages reçus !!!");
            }
        }
    }

    public void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }
}
