package com.controller.objects;

import java.util.ArrayList;

public class PlayerObj {

    private ArrayList<CardObj> playDeck;
    private int HP = 5;
    private String name;

    private int[] caricatore = new int[6];

    /**
     * L'oggetto giocatore
     * @param name Il nome del giocatore
     */
    public PlayerObj(String name){
        this.name = name;
        playDeck = new ArrayList<>();
    }

    /**
     * @param cardObj La carta da aggiungere alle carte del giocatore
     */
    public void addCard(CardObj cardObj) {
        if (cardObj != null) {
            playDeck.add(cardObj);
        } else {
            System.out.println("Attempted to add a null card to the player's deck.");
        }
    }


    /**
     * Funzione per farsi sparare
     * @param punti Punti del giocatore
     * @return Se true, si è sparato, se false no
     */
    public boolean shoot(int punti){
        int revAMMO=0;

        if(punti<15){
            revAMMO=1;
        }else if(punti<16){
            revAMMO=2;
        }else if(punti<18){
            revAMMO=3;
        }else if(punti<20){
            revAMMO=4;
        }else if(punti<21){
            revAMMO=5;
        }else if(punti==21){
            revAMMO=6;
        }

        for(int i=0;i<6;i++) {
            if(i<revAMMO){
                caricatore[i] = 1;

            }
            else{
                caricatore[i] = 0;
            }
        }

        double randomN = Math.random() * 6;

        int randomN2 = (int) randomN;

        System.out.println("Colpo " + randomN2);

        for(int i=0;i<6;i++){
            if(caricatore[i] == 1 && randomN2 == i){
                HP--;
                return true;
            }

        }
        return false;
    }

    /**
     * Funzione per resettare mazzo e HP alla fine di ogni turno
     *
     */
    public void resetPlayer(){
        playDeck = new ArrayList<>();
    }

    /**
     * @return L'Arraylist di CardObj con le carte del giocatore
     */
    public ArrayList<CardObj> getPlayDeck(){
        return playDeck;
    }

    public int getHP(){
        return HP;
    }

    public void decrementHP() {
        HP--;
    }

    public void setPlayDeck(ArrayList<CardObj> onlineMazzo) {
        playDeck=onlineMazzo;
    }
}
