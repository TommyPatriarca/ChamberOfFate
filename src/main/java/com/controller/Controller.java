package com.controller;

import com.cof.player.Player;
import com.controller.managers.cardManager.Deck;
import com.controller.objects.CardObj;
import com.controller.objects.PlayerObj;

import javax.smartcardio.Card;
import java.util.ArrayList;

public class Controller {

    private Deck deck;
    private PlayerObj player1, player2;

    private String playerName;

    private boolean onOrOff;

    private int turnCount = 0;

    /**
     * The controller is used to manage all the logical part of the game.
     * @param onOrOff If true, the game will be online, if false offline
     */
    public Controller (boolean onOrOff){

        //This function creates a new deck

        if(onOrOff){
            onlineGame();
        }
        else {
            offlineGame();
        }

    }

    /**
     * Function to manage the Online version of the game
     */
    public void onlineGame(){

    }

    /**
     * Function to manage the Offline version of the game. AI TO BE ADDED
     */
    public void offlineGame(){
        deck = new Deck();
    }

    public void startGame(String playerName){
        this.playerName = playerName;
        player1 = new PlayerObj(playerName);
        player2 = new PlayerObj("CPU");

        turn();

    }

    public void turn(){

        player1.resetPlayer();
        player2.resetPlayer();

        turnCount++;

        //PRIME DUE CARTE
        player1.addCard(deck.hitCard());
        player1.addCard(deck.hitCard());

        player2.addCard(deck.hitCard());
        player2.addCard(deck.hitCard());

        //CARTE SCELTE DAL GIOCATORE

    }

    public boolean AITurn(){
        if(AI()){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *
     * @param player Il giocatore a cui controllare le carte
     * @param firstOrAll Se true, controllo solo la prima, se false le controllo tutte
     * @return
     */
    public int checkCards(PlayerObj player, boolean firstOrAll){
        int runCount = 0;
        int aces = 0;
        String card;
        ArrayList<CardObj> mazzo = player.getPlayDeck();

        for(int i=0;i<mazzo.size();i++){
            card = mazzo.get(i).getTipo();

            card = card.substring(0 , card.length() -1);

            if(card.equals("J") || card.equals("Q") || card.equals("K")){
                runCount = runCount + 10;
            }
            else if(card.equals("A")){
                runCount = runCount +11;
                aces++;
            }
            else{
                runCount = runCount + Integer.parseInt(card);
            }

            if(firstOrAll){
                break;
            }

        }

        if(runCount >21) {
            for (int i = 0; i < aces; i++) {
                runCount = runCount - 10;
            }
            return runCount;

        }
        else{
            return runCount;
        }

    }

    /**
     *
     * @param firstOrSec Se true player1, se false player2
     */
    public void hitCard(boolean firstOrSec){
        if(firstOrSec){
            player1.addCard(deck.hitCard());
        }
        else{
            player2.addCard(deck.hitCard());
        }
    }

    /**
     * Intelligenza Artificiale che segue la tabella del BlackJack
     */
    public boolean AI(){

        int firstCard1 = checkCards(player1, true);
        int runCount2 = checkCards(player2, false);

        System.out.println("Giocatore 1 totale: " + firstCard1);

        while(true){
            System.out.println("Giocatore 2 totale: " + runCount2);
            if(runCount2<17){
                if(runCount2<12){
                    hitCard(false);
                }
                else {
                    if(runCount2 == 12){
                        if(firstCard1 == 2 || firstCard1 == 3){
                            hitCard(false);
                        }
                    }
                    else {
                        if(firstCard1<7){
                            break;
                        }
                        else {
                            hitCard(false);
                        }
                    }
                }

            }
            else{
                break;
            }
            runCount2 = checkCards(player2, false);
        }

        return true;

    }


    //GET FUNCTIONS

    public PlayerObj getPlayer1(){
        return player1;
    }

    public PlayerObj getPlayer2(){
        return player2;
    }

}
