package com.whatpub.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.whatpub.Controllers.Controller;
import com.whatpub.DataBase.DatabaseManager;
import com.whatpub.Models.Annonce;
import com.whatpub.R;
import com.whatpub.activities.DisplayPublicationActivity;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.whatpub.App.CHANNEL_ID;

/**
 * Classe qui permet de gerer les services de l'application.
 */
public class WhatPubService extends Service {
    // Fields.
    private static final String TAG = "WhatPubService";
    private static final String GROUP_KEY = "whatpub_key";
    private static final int SUMMARY_NOTIFICATION_ID = 1535453;
    private static int numberNotificationSend = 0;
    private boolean jobCancelled = false;

    /**
     * Fonction qui permet de faire le travail en background.
     */
    private void doBackgroundWork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!jobCancelled) {
                    DatabaseManager db = new DatabaseManager(getApplicationContext());
                    List<Annonce> annonces = db.getAllAnnonce();
                    int inCourseCount = db.getAllAnnonceInCours().size();
                    for (int i=0; i<annonces.size(); i++) {
                        Annonce annonce = annonces.get(i);
                        int nb_repetition = annonce.getNb_repetition();
                        boolean terminer = Boolean.parseBoolean(annonce.getTerminer());
                        Controller controller = Controller.getInstance();
                        Log.d(TAG, "run: "+controller.cameBefore(annonce.getDelais(), annonce.getHeure()));
                        if (!controller.cameBefore(annonce.getDelais(), annonce.getHeure()) && !terminer) {
                            db.isFinish(annonce, nb_repetition);
                            String contentText;
                            // Post dans whatsapp.
                            postToWhatsapp(annonce);
                            if (nb_repetition == 1) {
                                Intent bs = new Intent();
                                bs.setAction("ANNONCE_FINISH");
                                bs.putExtra("annonce_id", annonce.getId_annonce());
                                sendBroadcast(bs);
                                contentText = getResources().getString(R.string.pub_finish);
                                Log.d(TAG, getResources().getString(R.string.pub_finish)+": "+annonce.getNom_annonce());
                            }
                            else {
                                contentText = getResources().getString(R.string.rest_publish)+" "+(nb_repetition-1);
                                Log.d(TAG, "Une publication postée ==> "+ annonce.getNom_annonce() +" Reste de repetition : "+(annonce.getNb_repetition()-1));
                            }
                            buildNotification(annonce.getNom_annonce(), contentText, annonce.getId_annonce());
                        }
                    }
                    if (inCourseCount <= 0){
                        Log.d(TAG, "run: Size annonce in course = " + inCourseCount);
                        jobCancelled = true;
                        stopSelf();
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * Fonction qui permet de poster les publication dans la plate frome whatsapp.
     * @param annonce Annonce à a publier.
     */
    private void postToWhatsapp(Annonce annonce) {
        //TODO Gerer le poste de la publication dans la plate forme whatsapp.
        Log.d(TAG, annonce.getNom_annonce()+" à été postée dans whatsapp.");
    }

    /**
     * Fonction qui permet de créer une notification.
     */
    private void buildNotification(String pub_name, String contentText, int id_annonce) {
        Log.d(TAG, "La notification est lancée. Id pub = "+id_annonce);

        // Pour le displayPublication.
        Intent intent = new Intent(getApplicationContext(), DisplayPublicationActivity.class);
        intent.putExtra("id_annonce", id_annonce);
        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), id_annonce, intent, flag);
        // Pour la notification.
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.whatpub_icon)
                .setContentTitle(pub_name)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setGroup(GROUP_KEY)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        // Pour la notification summary.
        Notification summaryNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
              .setStyle(new NotificationCompat.InboxStyle()
                    .addLine(contentText)
                    .setBigContentTitle("WhatPub-app")
                    .setSummaryText("WhatPub"))
              .setPriority(NotificationCompat.PRIORITY_LOW)
              .setContentIntent(pendingIntent)
              .setSmallIcon(R.drawable.whatpub_icon)
              .setColor(getResources().getColor(R.color.green))
              .setAutoCancel(true)
              .setGroup(GROUP_KEY)
              .setColorized(true)
              .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
              .setGroupSummary(true)
              .build();
        // Pour la vibration.
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        assert vibrator != null;
        vibrator.vibrate(600);
        //Play sound.
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.whatpub_notify);
        mediaPlayer.start();
        // Afficher la notification.
        NotificationManager manager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        assert manager != null;
        manager.notify(numberNotificationSend, notification);
        manager.notify(SUMMARY_NOTIFICATION_ID, summaryNotification);
        numberNotificationSend ++;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind : methode");
        return null;
    }

    /**
     * Function to cancel all the notification.
     */
    public void cancelAllNotifications(Context context) {
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.cancelAll();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate : Le service est lancé.");
        super.onCreate();
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d(TAG, "stopService : Le service est arrêté.");
        jobCancelled = true;
        return super.stopService(name);
    }

    @Override
    public void startActivity(Intent intent) {
        Log.d(TAG, "startActivity : Une activité est déclanchée.");
        super.startActivity(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand : La commande est lancée.");
        doBackgroundWork();
        return super.onStartCommand(intent, flags, startId);
    }
}
