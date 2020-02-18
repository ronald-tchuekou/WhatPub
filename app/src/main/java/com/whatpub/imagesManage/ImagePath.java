package com.whatpub.imagesManage;
/**
 * @author Ronald Tchuekou
 * Classe ImagePath
 * Qui premet gerer le chemin d'accès à une image.
 */
public class ImagePath {
    /**
     * Nom de l'album.
     */
    private String name;
    /**
     * Nomber d'images contenue dans l'album.
     */
    private int nbImages;
    /**
     * Chemin de la première image de l'album.
     */
    private String firstImageContainedPath;
    /**
     * Constructeur de la classe.
     * @param name Nom de l'album.
     * @param firstImageContainedPath Chemin d'accès à la première image de l'album.
     * @param nb Nombre d'image contenue dans l'album.
     */
    ImagePath(String name, String firstImageContainedPath, int nb) {
        this.name = name;
        this.firstImageContainedPath = firstImageContainedPath;
        this.nbImages = nb;
    }
    /**
     * Fonction qui permet de recuperer le nom de l'album.
     * @return Nom de l'album.
     */
    public String getName() {
        return name;
    }
    /**
     * Fonction qui permet de retourner le nombre d'image contenue dans un album.
     * @return Nombre d'image.
     */
    public int getNbImages() {
        return nbImages;
    }
    /**
     * Fonction qui permet de recuperer le chemin d'accès à la première image de l'album.
     * @return Chemin d'accès à la première image.
     */
    public String getFirstImageContainedPath() {
        return firstImageContainedPath;
    }
}
