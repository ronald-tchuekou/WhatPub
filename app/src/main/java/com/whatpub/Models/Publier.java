/*
 * Copyright © 2019.  WhatPub by Ronald Tchuekou.
 */

package com.whatpub.Models;

import java.io.Serializable;

public class Publier implements Serializable {

    private int id_annonce;
    private int id_groupe;

    public Publier(int id_annonce, int id_groupe) {
        this.id_annonce = id_annonce;
        this.id_groupe = id_groupe;
    }

    public Publier() {
    }

    public int getId_annonce() {
        return id_annonce;
    }

    public int getId_groupe() {
        return id_groupe;
    }
}
