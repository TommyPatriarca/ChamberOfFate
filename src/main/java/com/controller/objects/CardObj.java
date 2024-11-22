package com.controller.objects;

public class CardObj {

    private String type;
    //private String numero;

    /**
     *  Hearts= Cuori
     *  Diamonds = Quadri
     *  Clubs = Fiori
     *  Spades = Picche
     * @param type Il numero di carta ed il seme (In inglese) ed . ES. "4 di fiori" = 4C
     *
     */
    public CardObj(String type){
        this.type= type;

    }

    public String getTipo(){
        return type;
    }


}
