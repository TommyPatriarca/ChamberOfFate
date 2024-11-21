package com.controller;

import com.controller.managers.cardManager.Deck;
import com.controller.objects.CardObj;
import com.controller.objects.PlayerObj;

import javax.smartcardio.Card;

public class Controller {

    private Deck deck;
    private PlayerObj player1, player2;

    private String playerName;

    private boolean onOrOff;

    private int turnCount = 0;

    /**
     * Il controller è usato per gestire tutta la parte logica del gioco
     * @param onOrOff Se true, la partita sarà online, se false offline
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
     * Funzione per gestire la versione online del gioco
     */
    public void onlineGame(){

    }

    /**
     * Funzione per gestire la versione offline del gioco. DA AGGIUNGERE AI CHE SEGUE LA TABELLA DEL BLACKJACK
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

    /**
     * Funzione per gestire il turno del giocatore
     * DA SISTEMARE ED INTEGRARE CON LA GRAFICA
     */
    public void turn(){
        turnCount++;

        //DA AGGIUNGERE LE CARTE DELL'AVVERSARIO

        //PRIME DUE CARTE DEL PLAYER 1
        player1.addCard(deck.hitCard());
        player1.addCard(deck.hitCard());

        //PRIME DUE CARTE DEL PLAYER 2
        player2.addCard(deck.hitCard());
        player2.addCard(deck.hitCard());

        checkCards(player1);
        checkCards(player2);

        //CARTE SCELTE DAL GIOCATORE

    }

    /**
     * Hit carta
     * @param firstOrSec Se true player1, se false player2
     */
    public int hitCard(boolean firstOrSec){
        if(firstOrSec){
            player1.addCard(deck.hitCard());
            return checkCards(player1);
        }
        else {
            player2.addCard(deck.hitCard());
            return checkCards(player2);
        }


    }

    /**
     * Funzione per calcolare il totale delle carte del giocare, e controllare se ha sballato
     * @return
     */
    public int checkCards(PlayerObj player){

        int totCards = 0;
        boolean busted = false;
        int ace = 0;

        for(int i=0;i<player.getPlayDeck().size();i++){

            String stringCarta = player.getPlayDeck().get(i).getTipo();
            stringCarta = stringCarta.substring(0, stringCarta.length() -1);

            if(stringCarta.equals("J") || stringCarta.equals("Q") || stringCarta.equals("K") || stringCarta.equals("10")){
                totCards = totCards + 10;

            }

            //Conto quanti assi ho, dato che può valere sia 11 che 1
            else if(stringCarta.equals("A")){
                ace ++;

                totCards = totCards + 11;

            }

            else {
                totCards = totCards + Integer.parseInt(stringCarta);
            }

            if(totCards > 21){
                if(ace>0){
                    for(i=0;i<ace;i++){
                        totCards = totCards - 10;
                    }

                }

                if(totCards>21){
                    busted = true;

                }
                else{

                }
            }

        }

        if(busted){
            return totCards;
        }
        else {
            return totCards;
        }

    }


    //GET FUNCTIONS

    public PlayerObj getPlayer1(){
        return player1;
    }

    public PlayerObj getPlayer2(){
        return player2;
    }

}
