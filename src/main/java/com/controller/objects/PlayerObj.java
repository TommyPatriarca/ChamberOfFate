package com.controller.objects;

import java.util.ArrayList;

public class PlayerObj {

    private ArrayList<CardObj> playDeck;
    private int HP = 5;
    private int runningCount =0;
    private String name;

    /**
     * @param name Il nome del giocatore
     */
    public PlayerObj(String name){
        this.name = name;
        playDeck = new ArrayList<>();
    }

    /**
     * Aggiungi carta al mazzo del giocatore
     * @param cardObj La carta da aggiungere
     */
    public void addCard(CardObj cardObj){
        playDeck.add(cardObj);
    }

    /**
     * @return L'Arraylist contenente il mazzo del giocare
     */
    public ArrayList<CardObj> getPlayDeck(){
        return playDeck;
    }

    /**
     * Funzione per resettare le carte ed il runinng count del giocatore alla fine di un turno
     */
    public void resetPlayerCard(){
        playDeck = new ArrayList<>();
        runningCount = 0;
    }

    /**
     * @return Nome del Giocatore
     */
    public String getName(){
        return name;
    }

    /**
     * @return Gli HP del giocatore
     */
    public int getHP(){
        return HP;
    }

    /**
     * @return Il running count (totale delle carte in mano) del giocatore
     */
    public int getRunningCount(){
        return runningCount;
    }


}
