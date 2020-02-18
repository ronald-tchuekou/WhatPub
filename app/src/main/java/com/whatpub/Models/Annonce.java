/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */

package com.whatpub.Models;

import java.io.Serializable;
import java.util.List;

public class Annonce implements Serializable {

    private int id_annonce;
    private String nom_annonce;
    private String date_creation;
    private String delais;
    private String heure;
    private String terminer;
    private String description;
    private int nb_repetition;
    private List<Photo> photos;
    private List<Groupe> groupes;

    // Constructeur de la classe.
    public Annonce(int id_annonce, String nom_annonce,
                   String date_creation, String delais,
                   String heure, String terminer, String description,
                   int nb_repetition, List<Photo> photos, List<Groupe> groupes) {

        this.id_annonce = id_annonce;
        this.nom_annonce = nom_annonce;
        this.date_creation = date_creation;
        this.delais = delais;
        this.heure = heure;
        this.terminer = terminer;
        this.description = description;
        this.nb_repetition = nb_repetition;
        this.photos = photos;
        this.groupes = groupes;
    }

    // Constructeur vide.
    public Annonce() {
    }

    public int getId_annonce() {
        return id_annonce;
    }

    public String getNom_annonce() {
        return nom_annonce;
    }

    public String getDate_creation() {
        return date_creation;
    }

    public String getDelais() {
        return delais;
    }

    public String getHeure() {
        return heure;
    }

    public String getTerminer() {
        return terminer;
    }

    public String getDescription() {
        return description;
    }

    public int getNb_repetition() {
        return nb_repetition;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public List<Groupe> getGroupes() {
        return groupes;
    }

}
