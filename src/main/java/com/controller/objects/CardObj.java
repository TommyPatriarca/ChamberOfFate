package com.controller.objects;

public class CardObj {

    private String type;

    /**
     *  Hearts= Cuori
     *  Diamonds = Quadri
     *  Clubs = Fiori
     *  Spades = Picche
     * @param type Il numero di carta ed il seme (In inglese). ES. "4 di fiori" = 4C
     */
    public CardObj(String type) {
        this.type = type;
    }

    public String getTipo() {
        return type;
    }

    // Genera il percorso dell'immagine basato sul seme e il valore
    public String getImagePath() {
        String suit = type.substring(type.length() - 1); // Ultimo carattere (es. C per Clubs)
        String value = type.substring(0, type.length() - 1); // Resto del valore (es. 4 per 4C)

        // Mappa i semi ai nomi completi
        String suitFullName;
        switch (suit) {
            case "C":
                suitFullName = "Clubs";
                break;
            case "H":
                suitFullName = "Hearts";
                break;
            case "D":
                suitFullName = "Diamonds";
                break;
            case "S":
                suitFullName = "Spades";
                break;
            default:
                throw new IllegalArgumentException("Seme non valido: " + suit);
        }

        return "/Cards/" + suitFullName + "_" + value + ".png";
    }
}
