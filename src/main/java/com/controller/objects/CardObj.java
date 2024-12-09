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
        String suit = type.substring(type.length() - 1);
        String value = type.substring(0, type.length() - 1);

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
        System.out.println(suitFullName);
        System.out.println(value);

        if(value.equals("A")){
            return "/Cards/" + suitFullName + "_" + "ACE" + ".png";
        }else{
            return "/Cards/" + suitFullName + "_" + value + ".png";
        }
    }
}
