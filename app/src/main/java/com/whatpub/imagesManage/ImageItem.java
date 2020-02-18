package com.whatpub.imagesManage;
/**
 * @author Ronald Tchuekou
 * Classe ImageItem
 * Qui premet gerer une image.
 */
public class ImageItem {
    /**
     * Path de l'image.
     */
    private String image;
    /**
     * Nom de l'image.
     */
    private String title;
    /**
     * Constructeur.
     * @param image Path de l'image.
     * @param title Nom de l'image.
     */
    ImageItem(String image, String title) {
        this.image = image;
        this.title = title;
    }
    /**
     * Fonction qui peremt de retourner le path de l'image.
     * @return Path de l'image.
     */
    public String getImage() {
        return image;
    }
    /**
     * Fonction qui permet de modifier le chemin d'une image.
     * @param image Nouveau chemin de l'image.
     */
    public void setImage(String image) {
        this.image = image;
    }
    /**
     * Fonction qui permet de retourner le nom d'une image.
     * @return Nom de l'image.
     */
    public String getTitle() {
        return title;
    }
    /**
     * Fonction qui permet de modifier le nom d'une image.
     * @param title Nouveau nom de l'image.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
