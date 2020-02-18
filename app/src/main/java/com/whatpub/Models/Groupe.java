/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */

package com.whatpub.Models;

import java.io.Serializable;

public class Groupe implements Serializable {

    private int id_groupe;
    private String nom_groupe;

    public Groupe(int id_groupe, String nom_groupe) {
        this.id_groupe = id_groupe;
        this.nom_groupe = nom_groupe;
    }

    public Groupe() {
    }

    public int getId_groupe() {
        return id_groupe;
    }

    public String getNom_groupe() {
        return nom_groupe;
    }
}

