package com.controller.objects;

import java.util.ArrayList;

public class PlayerObj {

    private ArrayList<CardObj> playDeck;
    private int HP = 5;
    private String name;
    private int revAMMO;

    /**
     * L'oggetto giocatore
     * @param name Il nome del giocatore
     */
    public PlayerObj(String name){
        this.name = name;
        playDeck = new ArrayList<>();
    }

    public void addCard(CardObj cardObj){
        playDeck.add(cardObj);
    }

    public ArrayList<CardObj> getPlayDeck(){
        return playDeck;
    }

    public void setRevAMMO(int revAMMO){
        this.revAMMO = revAMMO;
    }

    public void resetPlayer(){
        playDeck = new ArrayList<>();
        HP = 5;
    }


}
