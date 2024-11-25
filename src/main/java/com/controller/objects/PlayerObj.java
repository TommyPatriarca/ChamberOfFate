package com.controller.objects;

import java.util.ArrayList;

public class PlayerObj {

    private ArrayList<CardObj> playDeck;
    private int HP = 5;
    private String name;

    /**
     *
     * @param name The name of the player
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

    public void resetPlayer(){
        playDeck = new ArrayList<>();
        HP = 5;
    }


}
