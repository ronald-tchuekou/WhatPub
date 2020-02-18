/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */

package com.whatpub.Models;

import java.io.Serializable;

public class Photo implements Serializable {

    private int id_photo;
    private String nom_photo;
    private int id_annonce;

    public Photo(int id_photo, String nom_photo, int id_annonce) {
        this.id_photo = id_photo;
        this.nom_photo = nom_photo;
        this.id_annonce = id_annonce;
    }

    public Photo() {
    }

    public int getId_photo() {
        return id_photo;
    }

    public String getNom_photo() {
        return nom_photo;
    }

    public int getId_annonce() {
        return id_annonce;
    }
}
