package com.whatpub.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whatpub.AsynckTasks.AsyncImageLoadAdapter;
import com.whatpub.R;
import com.whatpub.imagesManage.ImageItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

/**
 * Classe pour la viewHolder.
 */
class ViewHolder {
    TextView text_des_album;
    ImageView album;
    RelativeLayout layout;
}

/**
 * @author Ronald Tchuekou
 * Classe qui permet d'adapter un album à la gallerie.
 */
public class Gallery_adapter extends ArrayAdapter {
    private static final String TAG = "Gallery_adapter";
    private Context context;
    private int layoutResourceId;
    private List<ImageItem> data;
    private WindowManager windowManager;
    private boolean[] imagesChecked;
    private int count_images = 0;
    private Toolbar toolbar;

    public Gallery_adapter(@NonNull Context context,
                           int resource, @NonNull List<ImageItem> data, WindowManager windowManager, Toolbar toolbar) {
        super(context, resource, data);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = data;
        this.imagesChecked = new boolean[data.size()];
        this.windowManager = windowManager;
        this.toolbar = toolbar;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.text_des_album = row.findViewById(R.id.text_des_album);
            holder.album = row.findViewById(R.id.album);
            holder.layout = row.findViewById(R.id.image_selected);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        // Synchronisation de données.
        AsyncImageLoadAdapter imageLoad = new AsyncImageLoadAdapter(holder.album, holder.text_des_album,
                data.get(position).getImage(), windowManager, 0.3f);
        imageLoad.execute();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.layout.setBackground(context.getResources().getDrawable(R.drawable.border_white));
            if (imagesChecked[position]){
                holder.layout.setBackground(context.getResources().getDrawable(R.drawable.image_border_selection));
            }else{
                holder.layout.setBackground(context.getResources().getDrawable(R.drawable.border_white));
            }
        }

        RelativeLayout item_gallery = (RelativeLayout) holder.layout.getParent();
        item_gallery.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (imagesChecked[position]) {
                    holder.layout.setBackground(context.getResources().getDrawable(R.drawable.border_white));
                    imagesChecked[position] = false;
                    count_images --;
                    updateToolbar();
                }else {
                    imagesChecked[position] = true;
                    count_images ++;
                    holder.layout.setBackground(context.getResources().getDrawable(R.drawable.image_border_selection));
                    updateToolbar();
                }
            }
        });

        return row;
    }

    /**
     * Fonction qui permet de faire la mis à jour de la toolbar.
     */
    private void updateToolbar() {
        toolbar.setTitle(count_images+" Selectionnée(s)");
    }

    /**
     * Fonction qui revoie la liste de tous les images choisie.
     * @return Liste des images.
     */
    public List<String> getAllImageChecked () {
        List<String> result = new ArrayList<>();
        for (int i=0; i<imagesChecked.length; i++) {
            if (imagesChecked[i])
                result.add(data.get(i).getImage());
        }
        Log.d(TAG, "Taille de la liste des images checked : "+result.size());
        return result;
    }

}

