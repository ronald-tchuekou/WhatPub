/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */

package com.whatpub.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.whatpub.Models.Annonce;
import com.whatpub.Models.Groupe;
import com.whatpub.Models.Photo;
import com.whatpub.Models.Publier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Classe qui permet de gérer la base de données.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WhatPubData.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DatabaseManager";

    private static String ordered = "date_creation DESC";

    /**
     * Constructeur de la classe.
     * @param context Context de l'application.
     */
    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> queries = new ArrayList<>();

        // Table annonce.
        queries.add("create table annonce (" +
                "id_annonce integer primary key autoincrement," +
                "nom_annonce text," +
                "date_creation datetime not null," +
                "delais datetime not null," +
                "terminer text not null," +
                "description text not null," +
                "nb_repetition integer" +
                ");");
        // Table photo.
        queries.add("create table photo (" +
                "id_photo integer primary key autoincrement," +
                "nom_photo text," +
                "id_annonce integer not null" +
                ");");
        // Table groupe.
        queries.add("create table groupe (" +
                "id_groupe integer primary key autoincrement," +
                "nom_groupe text" +
                ");");
        // Table publier.
        queries.add("create table publier (" +
                "id_annonce integer," +
                "id_groupe integer" +
                ");");

        for ( String query : queries )
            db.execSQL( query );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Fonction qui permet changer l'order d'affichage des publications.
     * @param order nouvelle ordre de classement des publications.
     */
    public static void changeOrder(String order){
        switch (order) {
            case "delais":
                ordered = "delais DESC";
                break;
            case "date":
                ordered = "date_creation DESC";
                break;
            case "nom":
                ordered = "nom_annonce";
                break;
        }
    }

    /**
     * Fonction qui permet de renvoyer les photos d'une annonce.
     * @param id_annonce identifiant de l'annonce.
     * @return liste de photos de l'annonce correspondante.
     */
    public List<Photo> getPhotos(int id_annonce) {
        List<Photo> allPhoto = new ArrayList<>();

        String table = "photo"; // nom de la table.
        String[] colums = {"id_photo", "nom_photo", "id_annonce"}; // Colonnes de la table.
        String condition = "id_annonce = ?";
        String[] value = {""+id_annonce};

        Cursor cursor = this.getReadableDatabase().query(table, colums, condition,
                value, null, null, null);

        // Le deplacement du curseur.
        if (cursor.moveToFirst())
            do {
                int id_photo = cursor.getInt(cursor.getColumnIndex("id_photo"));
                String nom_photo = cursor.getString(cursor.getColumnIndex("nom_photo"));

                Photo photo = new Photo(id_photo, nom_photo, id_annonce);

                allPhoto.add(photo);

            }while(cursor.moveToNext());
        cursor.close();
        return allPhoto;
    }

    /**
     * Fonction qui retourne un groupe.
     * @param id_groupe identifiant du groupe.
     * @return le groupe en question.
     */
    public Groupe getGroupe (int id_groupe) {

        String tableName = "groupe";
        String[] columns = { "id_groupe", "nom_groupe" };
        String condition = "id_groupe = ?";
        String[] value = { ""+id_groupe };

        Cursor cursor = this.getReadableDatabase().query(tableName, columns, condition, value, null,
                null, null);

        String nom_groupe = cursor.getString(cursor.getColumnIndex("nom_groupe"));

        cursor.close();

        return  new Groupe(id_groupe, nom_groupe);
    }

    /**
     * Fonction qui permet de recuperer tous les annonce dans la base de données.
     * @return liste de toutes les annonces de la base de données.
     */
    public List<Annonce> getAllAnnonce () {
        List<Annonce> result = new ArrayList<>();
        String sql= "select * from 'annonce' order by "+ordered;
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
        if (cursor.moveToFirst())
            do {
                int id_annonce = cursor.getInt(cursor.getColumnIndex("id_annonce"));
                String nom_annonce = cursor.getString(cursor.getColumnIndex("nom_annonce"));
                String date_creation = cursor.getString(cursor.getColumnIndex("date_creation"));
                String delais = cursor.getString(cursor.getColumnIndex("delais"));
                int nb_repetition = cursor.getInt(cursor.getColumnIndex("nb_repetition"));
                String terminer = cursor.getString(cursor.getColumnIndex("terminer"));
                String descirption = cursor.getString(cursor.getColumnIndex("description"));

                String heure = delais.split(" ")[1];
                delais = delais.split(" ")[0];

                List<Photo> photos = this.getPhotos(id_annonce);
                List<Groupe> groupes = this.getAllgroup(id_annonce);

                result.add( new Annonce(id_annonce, nom_annonce, date_creation, delais, heure,
                        terminer, descirption, nb_repetition, photos, groupes) );
            }while (cursor.moveToNext());
        cursor.close();
        return result;
    }


    /**
     * Fonction qui permet de recuperer tous les annonce en cours dans la base de données.
     * @return liste de toutes les annonces de la base de données.
     */
    public List<Annonce> getAllAnnonceInCours () {

        List<Annonce> result = new ArrayList<>();

        String sql= "select * from 'annonce' where terminer = 'false' order by "+ordered;
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);

        if (cursor.moveToFirst())
            do {

                int id_annonce = cursor.getInt(cursor.getColumnIndex("id_annonce"));
                String nom_annonce = cursor.getString(cursor.getColumnIndex("nom_annonce"));
                String date_creation = cursor.getString(cursor.getColumnIndex("date_creation"));
                String delais = cursor.getString(cursor.getColumnIndex("delais"));
                int nb_repetition = cursor.getInt(cursor.getColumnIndex("nb_repetition"));
                String terminer = cursor.getString(cursor.getColumnIndex("terminer"));
                String descirption = cursor.getString(cursor.getColumnIndex("description"));

                String heure = delais.split(" ")[1];
                delais = delais.split(" ")[0];

                List<Photo> photos = this.getPhotos(id_annonce);

                List<Groupe> groupes = this.getAllgroup(id_annonce);

                result.add( new Annonce(id_annonce, nom_annonce, date_creation, delais, heure,
                        terminer, descirption, nb_repetition, photos, groupes) );

            }while (cursor.moveToNext());

        cursor.close();

        return result;
    }

    /**
     * Fonction qui permet de recuperer tous les annonces terminées dans la base de données.
     * @return liste de toutes les annonces de la base de données.
     */
    public List<Annonce> getAllAnnonceFinish () {

        List<Annonce> result = new ArrayList<>();

        String sql= "select * from 'annonce' where terminer = 'true' order by "+ordered;
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);

        if (cursor.moveToFirst())
            do {

                int id_annonce = cursor.getInt(cursor.getColumnIndex("id_annonce"));
                String nom_annonce = cursor.getString(cursor.getColumnIndex("nom_annonce"));
                String date_creation = cursor.getString(cursor.getColumnIndex("date_creation"));
                String delais = cursor.getString(cursor.getColumnIndex("delais"));
                int nb_repetition = cursor.getInt(cursor.getColumnIndex("nb_repetition"));
                String terminer = cursor.getString(cursor.getColumnIndex("terminer"));
                String descirption = cursor.getString(cursor.getColumnIndex("description"));

                String heure = delais.split(" ")[1];
                delais = delais.split(" ")[0];

                List<Photo> photos = this.getPhotos(id_annonce);

                List<Groupe> groupes = this.getAllgroup(id_annonce);

                result.add( new Annonce(id_annonce, nom_annonce, date_creation, delais, heure,
                        terminer, descirption, nb_repetition, photos, groupes) );

            }while (cursor.moveToNext());

        cursor.close();

        return result;
    }

    /**
     * Fonction qui permet de recuperer une annonce de la base de données.
     * @param name nom de la l'annonce rechercher.
     * @return liste des publications retrouver.
     */
    public List<Annonce> getAnnonce (String name) {

        List<Annonce> result = new ArrayList<>();

        String tableName = "annonce";
        String[] columns = {
                "id_annonce",
                "nom_annonce",
                "date_creation",
                "delais",
                "terminer",
                "description",
                "nb_repetition"
        };
        String condition = "nom_annonce LIKE ?";
        String[] value = { ""+name };

        Cursor cursor = this.getReadableDatabase().query(tableName, columns, condition, value,
                null, null, ordered);

        if (cursor.moveToFirst())
            do {

                int id_annonce = cursor.getInt(cursor.getColumnIndex("id_annonce"));
                String nom_annonce = cursor.getString(cursor.getColumnIndex("nom_annonce"));
                String date_creation = cursor.getString(cursor.getColumnIndex("date_creation"));
                String delais = cursor.getString(cursor.getColumnIndex("delais"));
                int nb_repetition = cursor.getInt(cursor.getColumnIndex("nb_repetition"));
                String terminer = cursor.getString(cursor.getColumnIndex("terminer"));
                String descirption = cursor.getString(cursor.getColumnIndex("description"));

                String heure = delais.split(" ")[1];
                delais = delais.split(" ")[0];

                List<Photo> photos = this.getPhotos(id_annonce);

                List<Groupe> groupes = this.getAllgroup(id_annonce);

                result.add( new Annonce(id_annonce, nom_annonce, date_creation, delais, heure,
                        terminer, descirption, nb_repetition, photos, groupes) );

            }while (cursor.moveToNext());

        cursor.close();

        return result;
    }

    /**
     * Fonction qui permet de renvoyer tous les groupes d'une annonce.
     * @param id_annonce identifiant de l'annonce dont on veux recupérer les groupes.
     * @return liste de groupes.
     */
    private List<Groupe> getAllgroup(int id_annonce) {

        List<Groupe> result = new ArrayList<>();

        String tableName = "annonce INNER JOIN (publier INNER JOIN groupe ON publier.id_groupe = groupe.id_groupe) ON annonce.id_annonce = publier.id_annonce";
        String[] columns = { "groupe.id_groupe", "groupe.nom_groupe" };
        String condition = "annonce.id_annonce = ?";
        String[] value = { ""+id_annonce };


        Cursor cursor = this.getReadableDatabase().query(tableName, columns, condition, value, null,
                null, null);
        if(cursor.moveToFirst())
            do {

                String nom_groupe = cursor.getString(cursor.getColumnIndex("groupe.nom_groupe"));
                int id_groupe = cursor.getInt(cursor.getColumnIndex("groupe.id_groupe"));

                result.add(new Groupe(id_groupe, nom_groupe));
            }while (cursor.moveToNext());
        cursor.close();

        return  result;
    }

    /**
     * Fonction qui permet de retounrer tous les couple (id_annonce, id_groupe) existant dans la base de donneés.
     * @return liste de couples.
     */
    public List<Publier> allPublier() {

        List<Publier> result = new ArrayList<>();

        String tableName = "publier";
        String[] columns = { "id_annonce", "is_groupe"};

        Cursor cursor = this.getReadableDatabase().query(tableName, columns, null, null
                , null, null, null);

        if (cursor.moveToFirst())
            do {
                int id_annonce = cursor.getInt(cursor.getColumnIndex("id_annonce"));
                int id_groupe = cursor.getInt(cursor.getColumnIndex("id_groupe"));

                result.add(new Publier(id_annonce, id_groupe));
            }while(cursor.moveToNext());
        cursor.close();
        return result;
    }

    /**
     * Fonction qui permet d'insérer les annonces dans la table d'annonces.
     * @param annonce Annonce à inserer dans la base de données.
     * @return booleen de validation.
     */
    public boolean insertAnnonce(Annonce annonce) {
        String nom_annonce = annonce.getNom_annonce().replace("'", "''");
        String description = annonce.getDescription().replace("'", "''");
        try {
            String query = "insert into annonce (nom_annonce, date_creation, " +
                    "delais, terminer, description, nb_repetition) values ('"
                    + nom_annonce +"','"
                    + annonce.getDate_creation() +"','"
                    + annonce.getDelais()+" "+annonce.getHeure() +"','"
                    + annonce.getTerminer() +"','"
                    + description +"',"
                    + annonce.getNb_repetition() +")";

            this.getWritableDatabase().execSQL( query );

        }catch (SQLException e){
            Log.d(TAG, " Erreur d'insertion sur la requete *insertAnnonce* ");
            return false;
        }
        return true;
    }

    /**
     * Fonction qui permet d'insérer dans la table de photos.
     * @param photo Photo à inserer dans la base de données.
     * @return booleen de validation.
     */
    public boolean insertPhoto(Photo photo) {

        try {
            String query = "insert into photo (nom_photo, id_annonce) values ('"
                    + photo.getNom_photo() +"',"
                    + photo.getId_annonce() +")";

            this.getWritableDatabase().execSQL( query );

        }catch (SQLException e){
            Log.d(TAG, " Erreur d'insertion sur la requete *insertPhoto* "+e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Fonction qui permet d'insérer un groupe dans la base de données.
     * @param groupe nom du groupe.
     * @return booleen de validations.
     */
    public boolean insertGroupe(Groupe groupe) {
        try {
            String query = "insert into groupe (nom_groupe) values ('"+ groupe.getNom_groupe() +"')";

            this.getWritableDatabase().execSQL( query );

        }catch (SQLException e){
            Log.d(TAG, " Erreur d'insertion sur la requete *insertGroupe* ");
            return false;
        }
        return true;
    }

    /**
     * Fonction qui permet d'insérer un nouvelle element publier dans la base de données.
     * @param publier Elementd de type <strong>Publier</strong>.
     * @return booleen de validation.
     */
    public boolean insertPublier(Publier publier) {
        try {
            String query = "insert into publier (id_annonce, id_groupe) values ("+ publier.getId_annonce() +", "+ publier.getId_groupe() +")";

            this.getWritableDatabase().execSQL( query );

        }catch (SQLException e){
            Log.d(TAG, " Erreur d'insertion sur la requete *insertPublier* ");
            return false;
        }
        return true;
    }

    /**
     * Fonction qui permet de faire la mis à jour d'une annonce dans la table d'annonces.
     * @param annonce Annonce dont on veux faire la mis à jour dans la base de données.
     * @return booleen de validation.
     */
    public boolean updateAnnonce(Annonce annonce) {
        String nom_annonce = annonce.getNom_annonce().replace("'", "''");
        String description = annonce.getDescription().replace("'", "''");
        try {
            String query = "update annonce set nom_annonce = '"+nom_annonce+"', " +
                    "date_creation = '"+annonce.getDate_creation()+"', " +
                    "delais = '"+annonce.getDelais()+" "+annonce.getHeure()+"', " +
                    "terminer = '"+annonce.getTerminer()+"', " +
                    "description = '"+description+"', " +
                    "nb_repetition = "+annonce.getNb_repetition()+" "+
                    "where id_annonce = "+annonce.getId_annonce();

            this.getWritableDatabase().execSQL( query );

        }catch (SQLException e){
            Log.d(TAG, " Erreur d'insertion sur la requete *updateAnnonce* ");
            return false;
        }
        return true;
    }

    /**
     * Fonction qui permet de supprimer des annonces dans la base de données.
     * @param id_annonces tableau d'identifiants de tous les annonces à supprimer.
     * @return booleen de validation.
     */
    public boolean deleteFromAnnonce (int[] id_annonces) {
        try {
            for ( int id_annonce : id_annonces){
                String query = "delete from annonce where id_annonce = " + id_annonce;

                this.getWritableDatabase().execSQL( query );
            }

        }catch (SQLException e){
            Log.d(TAG, " Erreur de suppression sur la requete *deleteFromAnnonce* ");
            return false;
        }
        return true;
    }

    /**
     * Fonction qui permet de supprimer des publier dans la base de données.
     * @param id_publiers tableau d'identifiants de tous les publier à supprimer.
     * @return booleen de validation.
     */
    public boolean deleteFromPublier (int[] id_publiers) {
        try {
            for ( int id_publier : id_publiers){
                String query = "delete from groupe where id_groupe = " + id_publier;

                this.getWritableDatabase().execSQL( query );
            }

        }catch (SQLException e){
            Log.d(TAG, " Erreur de suppression sur la requete *deleteFromPhoto* ");
            return false;
        }
        return true;
    }

    /**
     * Fonction qui permet de supprimer des groupes dans la base de données.
     * @param id_groupes tableau d'identifiants de tous les groupes à supprimer.
     * @return booleen de validation.
     */
    public boolean deleteFromGroupe (int[] id_groupes) {
        try {
            for ( int id_groupe : id_groupes){
                String query = "delete from groupe where id_groupe = " + id_groupe;

                this.getWritableDatabase().execSQL( query );
            }

        }catch (SQLException e){
            Log.d(TAG, " Erreur de suppression sur la requete *deleteFromPhoto* ");
            return false;
        }
        return true;
    }

    /**
     * Fonction qui permet de supprimer des photos dans la base de données.
     * @param id_photos tableau d'identifiants de tous les photos à supprimer.
     * @return booleen de validation.
     */
    public boolean deleteFromPhoto (int[] id_photos) {
        try {
            for ( int id_photo : id_photos){
                String query = "delete from photo where id_photo = " + id_photo;

                this.getWritableDatabase().execSQL( query );
            }

        }catch (SQLException e){
            Log.d(TAG, " Erreur de suppression sur la requete *deleteFromPhoto* ");
            return false;
        }
        return true;
    }

    /**
     * Fonction qui renvoie une annonce selectionnée dans la base de données par son identifiant.
     * @param id_annonce Identifiant de l'annonce.
     * @return L'annonce trouver.
     */
    public Annonce getAnnonce(int id_annonce) {

        String tableName = "annonce";
        String[] columns = {
                "id_annonce",
                "nom_annonce",
                "date_creation",
                "delais",
                "terminer",
                "description",
                "nb_repetition"
        };
        String condition = "id_annonce = ?";
        String[] value = { ""+id_annonce };

        Cursor cursor = this.getReadableDatabase().query(tableName, columns, condition, value,
                null, null, null);
        cursor.moveToFirst();
        String nom_annonce = cursor.getString(cursor.getColumnIndex("nom_annonce"));
        String date_creation = cursor.getString(cursor.getColumnIndex("date_creation"));
        String delais = cursor.getString(cursor.getColumnIndex("delais"));
        int nb_repetition = cursor.getInt(cursor.getColumnIndex("nb_repetition"));
        String terminer = cursor.getString(cursor.getColumnIndex("terminer"));
        String descirption = cursor.getString(cursor.getColumnIndex("description"));

        String heure = delais.split(" ")[1];
        delais = delais.split(" ")[0];

        List<Photo> photos = this.getPhotos(id_annonce);

        List<Groupe> groupes = this.getAllgroup(id_annonce);

        cursor.close();

        return new Annonce(id_annonce, nom_annonce, date_creation, delais, heure,
                terminer, descirption, nb_repetition, photos, groupes);
    }

    /**
     * Fonction qui renvoie l'identifiant d'un élément du tableau des annonces.
     * @param annonceName Nom de l'annonce.
     * @return Identifiant de élément.
     */
    public int getLastId(String annonceName) {
        int id = 0;
        try {
            String sql= "select * from annonce where nom_annonce = '"+annonceName+"'";
            Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
            if (cursor.moveToFirst())
                id = cursor.getInt(cursor.getColumnIndex("id_annonce"));
            cursor.close();
        }catch (Exception e) {
            Log.d(TAG, "erreur dans la table **getLastId** "+e.getMessage());
        }
        return id;
    }

    /**
     * Fonction qui renvoie les id des photo pour une annonce.
     * @param id id de l'annonce.
     * @return table des ids des photos.
     */
    public int[] getAllId(int id) {
        int[] ids = new int[50];
        int i = 0;
        String sql= "select * from photo where id_annonce = "+id;
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
        if (cursor.moveToFirst())
            do {
                ids[i] = cursor.getInt(cursor.getColumnIndex("id_photo"));
                i++;
            }while (cursor.moveToNext());
        cursor.close();
        return ids;
    }

    /**
     * Fonction qui permet de terminer une publication.
     * @param annonce annonce à terminer.
     */
    public void isFinish(Annonce annonce, int repetition) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        String nextDay = year+"-"+month+"-"+day+" "+(hour < 10 ? ("0"+hour) : hour)+":"+(minutes < 10 ? ("0"+minutes) : minutes)+":00";

        try {
            String query;
            if (repetition == 1)
                query = "update annonce set terminer = 'true', nb_repetition = "+(repetition-1)+" where id_annonce = "+annonce.getId_annonce();
            else
                query = "update annonce set nb_repetition = "+(repetition-1)+", delais = '"+nextDay+"' where id_annonce = "+annonce.getId_annonce();

            this.getWritableDatabase().execSQL( query );

        }catch (SQLException e){
            Log.d(TAG, " Erreur d'insertion sur la requete *isFinish* "+e.getMessage());
        }
    }

}
