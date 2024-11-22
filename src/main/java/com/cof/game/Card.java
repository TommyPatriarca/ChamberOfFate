package com.cof.game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Card {
    private String suit; // Seme della carta
    private String value; // Valore della carta
    private String imagePath; // Percorso dell'immagine associata alla carta
    private ImageView cardImage; //

    // Costruttore
    public Card(String suit, String value) {
        this.suit = suit;
        this.value = value;
        this.imagePath = generateImagePath(suit, value);
        this.cardImage = new ImageView(new Image(imagePath));
        configureCardImage();
    }

    // Genera il percorso dell'immagine basato sul seme e il valore
    private String generateImagePath(String suit, String value) {
        return "resources/Cards/" + suit + "_" + value + ".png";
    }

    // Configura l'immagine della carta
    private void configureCardImage() {
        cardImage.setFitWidth(100); // Larghezza dell'immagine
        cardImage.setFitHeight(150); // Altezza dell'immagine
        cardImage.setPreserveRatio(true); // Mantieni proporzioni
    }

    public String getSuit() {
        return suit;
    }

    public String getValue() {
        return value;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ImageView getCardImage() {
        return cardImage;
    }

    @Override
    public String toString() {
        return suit + " " + value;
    }
}
