package com.whatpub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.whatpub.Adapter.Gallery_adapter;
import com.whatpub.R;
import com.whatpub.imagesManage.ImageItem;
import com.whatpub.imagesManage.ImagePath;
import com.whatpub.imagesManage.ImagesManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChooseImages extends AppCompatActivity {

    private ImagesManager manageImage;
    private Gallery_adapter adapter;
    private Toolbar toolbar;
    private static final String TAG = "ChooseImages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_images);
        toolbar = findViewById(R.id.toolbar_choose_image);
        this.setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_choose_image);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        manageImage = new ImagesManager();
        initAdapter();
        init();
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
            List<String> imagesChecked = adapter.getAllImageChecked();
            Intent intent = new Intent();
            intent.putExtra("imagesChecked", (Serializable) imagesChecked);
            setResult(RESULT_OK, intent);
            finish();
            Log.d(TAG, "Size of list imagesChecked : "+imagesChecked.size());
        }
        return true;
    }

    private void initAdapter() {
        adapter = new Gallery_adapter(this, R.layout.item_gallery, getAllImages(), this.getWindowManager(), toolbar);
    }

    /**
     * Fonction qui permet de recuperer les images.
     * @return Liste d'images.
     */
    public List<ImageItem> getAllImages () {
        List<ImageItem> images = new ArrayList<>();
        for (ImagePath imagePath : manageImage.getImagesAlbums(this)){
            images.addAll(manageImage.getImages(imagePath.getName(), this));
        }
        return images;
    }

    public void init () {
        // Inflate the layout for this fragment
        GridView gallery = findViewById(R.id.gallery);
        gallery.setAdapter(adapter);
    }
}
