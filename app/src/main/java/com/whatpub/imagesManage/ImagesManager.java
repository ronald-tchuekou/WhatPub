package com.whatpub.imagesManage;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * @author Ronald Tchuekou
 * Classe ImagesManager
 * Qui permet de gerer les images contenu dans le téléphone de l'utilisateur.
 */
public class ImagesManager {
    private static final String TAG = "ImagesManager";
    /**
     * Constructeur de la classe.
     */
    public ImagesManager() {
    }
    /**
     * Fonction qui peremt de recuperer la list des album.
     * @param context Context de l'application.
     * @return Liste des albums.
     */
    public List<ImagePath> getImagesAlbums(Context context) {
        Log.d(TAG, "Get images album");
        List<ImagePath> album = new ArrayList<>(); // Liste des chemin d'images.
        List<String> allPaths = new ArrayList<>(); // Liste de tous les chemins.
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA};
        String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, orderBy);
        if (cursor != null) {
            File file;
            while (cursor.moveToNext()) {
                String bucketPath = cursor.getString(cursor.getColumnIndex(projection[0]));
                String firstImage = cursor.getString(cursor.getColumnIndex(projection[1]));
                file = new File(firstImage);
                if (file.exists() && !allPaths.contains(bucketPath)) {
                    album.add(new ImagePath(bucketPath, firstImage, countImgs(bucketPath, context)));
                    allPaths.add(bucketPath);
                }
            }
        }else
            Log.d(TAG, "Empty cursor");
        assert cursor != null;
        cursor.close();
        Log.d(TAG, "Get images album successful");
        return album;
    }
    /**
     * Fonction qui peremt de recuperer la list des image d'une album.
     * @param albumPath Chemin d'accès à une album.
     * @param context Context de l'application.
     * @return Liste des images.
     */
    public List<ImageItem> getImages(@NonNull String albumPath, Context context){
        Log.d(TAG, "Start get images ");
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String [] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME+" =?";
        String orderBy = MediaStore.Images.Media.DATE_ADDED+" DESC";
        List<ImageItem> images = new ArrayList<>(); // Liste des images.
        Cursor cursor = context.getContentResolver().query(uri, projection,
                selection,new String[]{albumPath}, orderBy);
        if(cursor != null){
            File file;
            while (cursor.moveToNext()){
                String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                file = new File(path);
                if (file.exists() && !images.contains(new ImageItem(path, ""))) {
                    images.add(new ImageItem(path, ""));
                }
            }
        }else
            Log.d(TAG, "Empty cusor");
        assert cursor != null;
        cursor.close();
        Log.d(TAG, "Get images successful");
        return images;
    }
    /**
     * Fonction qui peremt de conter le nombre d'images contenue dans une album.
     * @param filePath Chemin des fichiers images.
     * @param context  Context de l'application.
     * @return Nombre d'image de l'album.
     */
    private int countImgs(String filePath, Context context) {
        Log.d(TAG, "Get count images");
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?";
        String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        List<ImageItem> images = new ArrayList<>(); // Liste des images.
        Cursor cursor = context.getContentResolver().query(uri, projection,
                selection, new String[]{filePath}, orderBy);
        if (cursor != null) {
            File file;
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                file = new File(path);
                if (file.exists() && !images.contains(new ImageItem(path, ""))) {
                    images.add(new ImageItem(path, ""));
                }
            }
            cursor.close();
        }
        return images.size();
    }

    /**
     * Fonction qui permet de modifier les dimensions d'une image.
     * @param bitmap Image en format bitmap.
     * @param proportion Proportion définie.
     * @param windowManager Caractéristique du divice.
     * @return Image resized.
     */
    public static Bitmap resizeBitmap (Bitmap bitmap, float proportion, WindowManager windowManager) {
        // The metrics : allow us to get all characteristics of the device.
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        // Size of the screen by the proportion.
        float screenHeight = metrics.heightPixels * proportion;
        float screenWidth = metrics.widthPixels * proportion;
        // Small side of the screen.
        float screen = Math.min(screenHeight, screenWidth);
        // Size of the image.
        float bitmapHeight = bitmap.getHeight();
        float bitmapWidth = bitmap.getWidth();
        // Get ratio between image and screen of the device.
        float ratioHeight = screen/bitmapHeight;
        float ratioWidth = screen/bitmapWidth;
        // Grand ratio
        float ratio = Math.max(ratioHeight, ratioWidth);
        // resize the image use the value ratio.
        bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmapWidth*ratio), (int)(bitmapHeight*ratio), true);
        // cote image like square.
        int x = (int)Math.max(0, (bitmap.getWidth()-screen)/2);
        int y = (int)Math.max(0, (bitmap.getHeight()-screen)/2);

        bitmap = Bitmap.createBitmap(bitmap, x, y, (int)screen, (int)screen);
        return bitmap;
    }
}
