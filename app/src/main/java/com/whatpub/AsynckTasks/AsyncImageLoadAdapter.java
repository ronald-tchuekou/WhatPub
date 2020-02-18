package com.whatpub.AsynckTasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.whatpub.R;
import com.whatpub.imagesManage.ImagesManager;

public class AsyncImageLoadAdapter extends AsyncTask<Void, Integer, Bitmap> {

    private ImageView album;
    private TextView text_des_album;
    private String name;
    private WindowManager windowManager;
    private final float proportion;

    public AsyncImageLoadAdapter(ImageView album, TextView text_des_album, String name ,
                                 WindowManager windowManager, final float proportion) {
        this.album = album;
        this.text_des_album = text_des_album;
        this.name = name;
        this.windowManager = windowManager;
        this.proportion = proportion;
    }

    @Override
    protected void onPreExecute() {
        album.setImageResource(R.drawable.ic_image_default);
        if (text_des_album != null)
            text_des_album.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        synchronized (this) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bmp = BitmapFactory.decodeFile(name, options);
            return ImagesManager.resizeBitmap(bmp, proportion, windowManager);
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (text_des_album != null)
            text_des_album.setText(name);
        album.setImageBitmap(bitmap);
        album.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
