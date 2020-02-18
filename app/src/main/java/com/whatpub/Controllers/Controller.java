/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */

package com.whatpub.Controllers;

import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;

public class Controller {

    private  static Controller instance;
    private static final String TAG = "Controller";

    // Constructeur de la classe.
    private Controller() { super(); }

    public static Controller getInstance () {
        if (instance == null)
            instance = new Controller();
        return instance;
    }

    /**
     * Fonctio qui permet de vérifier si le nom de la publication est valide.
     * @param name Nom de la publication.
     * @return Vrai ou Faux.
     */
    public boolean validateName(String name) {
        return !name.isEmpty();
    }

    /**
     * Fonction qui permet de vérifier si une date est valide.
     * @param echeance Delais de la publication.
     * @return Vrai ou Faux.
     */
    public boolean cameBefore(String echeance, String time) {
        Calendar date1 = toDate(echeance, time);
        Calendar date2 = Calendar.getInstance();
        return date2.before(date1);
    }

    /**
     * Fonction qui premet de convertir une date de type string en date.
     * @param strDate Date en string.
     * @return Date.
     */
    public Calendar toDate(String strDate, String time) {
        String[] tableDate = strDate.split("-");
        String[] tableTime = time.split(":");
        Calendar date = Calendar.getInstance();

        date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tableDate[2])); // Day
        date.set(Calendar.MONTH, Integer.parseInt(tableDate[1])-1); // Month
        date.set(Calendar.YEAR, Integer.parseInt((tableDate[0]))); // Year
        date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tableTime[0])); // Hour
        date.set(Calendar.MINUTE, Integer.parseInt(tableTime[1])); // Minute
        date.set(Calendar.SECOND, Integer.parseInt(tableTime[2])); // Second

        Log.d(TAG, "toDate : "+ DateFormat.getDateInstance().format(date.getTime()));
        return date;
    }


    /**
     * Fonction qui renvoie le bon format du nombre de repetition.
     * @param nb_repetition nombre de repetition.
     * @return nombre de repetition.
     */
    public int getRepetition(String nb_repetition) {
        return Integer.parseInt(nb_repetition.substring(0, 1));
    }

}
