package com.controller.objects;

import java.util.ArrayList;

public class PlayerObj {

    private ArrayList<CardObj> playDeck;
    private int HP = 5;
    private String name;
    private int revAMMO;

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
    public void addCard(CardObj cardObj){
        playDeck.add(cardObj);
    }

    /**
     * Funzione per farsi sparare
     * @param revAMMO Proiettili nel caricatore
     * @return Se true, si è sparato, se false no
     */
    public boolean shoot(int revAMMO){
        this.revAMMO = revAMMO;

        for(int i=0;i<6;i++) {
            if(i<revAMMO){
                caricatore[i] = 1;

            }
            else{
                caricatore[i] = 0;
            }
            /*for (int j = 0; j < revAMMO; j++) {
                caricatore[i] = 1;
            }*/
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
        HP = 5;
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

}
