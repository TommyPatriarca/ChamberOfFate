package com.controller;

import com.controller.managers.cardManager.Deck;
import com.controller.objects.CardObj;
import com.controller.objects.PlayerObj;

import javax.smartcardio.Card;

public class Controller {

    private Deck deck;
    private PlayerObj player1;

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

        turn(player1);

    }

    public void turn(PlayerObj player){
        turnCount++;

        //DA AGGIUNGERE LE CARTE DELL'AVVERSARIO

        //PRIME DUE CARTE
        player.addCard(deck.hitCard());
        player.addCard(deck.hitCard());

        //CARTE SCELTE DAL GIOCATORE

    }

    //GET FUNCTIONS

    public PlayerObj getPlayer1(){
        return player1;
    }

}
