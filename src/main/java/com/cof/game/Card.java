package com.cof.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 * La classe delle carte
 */
public class Card {
    private String suit; // Seme della carta
    private String value; // Valore della carta
    private String imagePath; // Percorso dell'immagine associata alla carta
    private ImageView cardImage; //

    /**
     * Il costruttore delle carte
     * @param suit il seme della carta
     * @param value il valore della carta
     */
    public Card(String suit, String value) {
        this.suit = suit;
        this.value = value;
        this.imagePath = generateImagePath(suit, value);
        this.cardImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
        configureCardImage();
    }

    /**
     * Genera il percorso dell'immagine basato sul seme e il valore
     * @param suit il seme della carta
     * @param value il valore della carta
     * @return il percorso dell'immagine basato sul seme e il valore
     */
    private String generateImagePath(String suit, String value) {
        return "/Cards/" + suit + "_" + value + ".png";
        //test return "/Cards/Clubs_10.png";
    }

    /**
     * Configura l'immagine della carta
     */
    private void configureCardImage() {
        cardImage.setFitWidth(100); // Larghezza dell'immagine
        cardImage.setFitHeight(150); // Altezza dell'immagine
        cardImage.setPreserveRatio(true); // Mantieni proporzioni
    }

    /**
     * Ritorna il seme della carta
     * @return il seme della carta
     */
    public String getSuit() {
        return suit;
    }

    /**
     * Ritorna il valore della carta
     * @return il valore della carta
     */
    public String getValue() {
        return value;
    }

    /**
     * Ritorna il percorso dell'immagine basato sul seme e il valore
     * @return il percorso dell'immagine basato sul seme e il valore
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Ritorna l'immagine della carta
     * @return l'immagine della carta
     */
    public ImageView getCardImage() {
        return cardImage;
    }

    /**
     * Stampa lo stato della carta
     * @return lo stato della carta
     */
    @Override
    public String toString() {
        return suit + " " + value;
    }
}
