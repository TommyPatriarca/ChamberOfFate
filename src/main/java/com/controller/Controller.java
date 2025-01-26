package com.controller;

import com.cof.player.Player;
import com.controller.managers.cardManager.Deck;
import com.controller.objects.CardObj;
import com.controller.objects.PlayerObj;
import okhttp3.*;

import javax.smartcardio.Card;
import java.util.ArrayList;

/**
 * La classe del Controllore
 */
public class Controller {

    private Deck deck;
    private PlayerObj player1, player2;

    private String playerName;

    private boolean onOrOff;

    private int turnCount = 0;

    /**
     * Il controller è usato per gestire tutta la parte logica del gioco
     * @param onOrOff Se true il gioco sarà online, se false offline
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
        deck = new Deck(); // Inizializza il mazzo per la modalità online
    }


    /**
     * Funzione per gestire la versione offline del gioco con AI
     */
    public void offlineGame(){

        deck = new Deck();
    }

    /**
     * fa partire il gioco
     * @param playerName il nickname del giocatore
     */
    public void startGame(String playerName){
        this.playerName = playerName;
        player1 = new PlayerObj(playerName);
        player2 = new PlayerObj("CPU");
        turn();
    }

    /**
     * Funzione per gestire ogni turno
     */
    public void turn() {
        player1.resetPlayer();
        player2.resetPlayer();

        player1.addCard(deck.hitCard());
        player1.addCard(deck.hitCard());

        player2.addCard(deck.hitCard());
        player2.addCard(deck.hitCard());
    }

    /**
     * Funzione per il turno del bot
     * @param updateDisplayCallback
     * @return true quando il bot ha finito il turno
     */
    public boolean AITurn(Runnable updateDisplayCallback) {
        int firstCard1 = checkCards(player1, true);
        int runCount2 = checkCards(player2, false);

        System.out.println("AI Turn Start - Player 1 First Card: " + firstCard1 + ", AI Total: " + runCount2);

        while (runCount2 < 17) {
            if (deckHasCards()) {
                hitCard(false);
                runCount2 = checkCards(player2, false);
                System.out.println("AI Draws a Card - New AI Total: " + runCount2);

                // Callback per aggiornare la GUI
                updateDisplayCallback.run();
            } else {
                System.out.println("Deck is empty. AI cannot draw more cards.");
                break;
            }
        }

        System.out.println("AI Turn End - Final AI Total: " + runCount2);
        return true;
    }


    /**
     * Controlla le carte
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
     * Funzione per controllare il risultato di entrambi i Blackjack
     * @return Se 1, si spara il giocatore 1, se 2 il secondo,  se -1 pari
     */
    public int checkResult(){
        int runCountP1, runCountP2;

        runCountP1 = checkCards(player1, false);
        runCountP2 = checkCards(player2, false);

        //Controllo se non siano uguali
        if(runCountP1!=runCountP2){
            //Controllo se siano minori di 22
            if(runCountP1<22 && runCountP2<22){

                //Controllo se P1>P2
                if(runCountP1>runCountP2){

                    //Si spara il P2
                    return 2;
                }

                //Altrimenti se P2>P1
                else{

                    //Si spara il P1
                    return 1;
                }
            }

            //Controllo se entrambi non hanno sballato
            else if(runCountP1>21 && runCountP2 > 21){
                return -1;
            }

            //Controllo se P1 non ha sballato
            else if(runCountP1>21){
                //Alrimenti si spara P1
                return 1;

            }

            //Controllo se P2 non ha sballato
            else if(runCountP2>21){
                //Alrimenti si spara P2
                return 2;

            }

            //Entrambi hanno sballato
            else{
                //Nessuno si spara, pari
                return -1;
            }

        }

        //Altrimenti sono pari
        else{
            //Nessuno si spara
            return -1;
        }

    }

    /**
     *Spara al giocatore che ha perso
     */
    public boolean shootPlayer(){
        int result = checkResult();

        //Il running count del giocatore che ha vinto
        int runCount;

        if(result==1){
            runCount = checkCards(player2, false);
            //CONTROLLARE SE E' STATO FATTO BLACKJACK E NON SOLO 21
            //OTTIMIZZARE MEGLIO GLI IF, SE POSSIBILE FARE UN UNICO IF PER TUTTI E DUE I CASi
            if(runCount==21){
                if(player1.shoot(3)){
                    return true;
                }
            }
            else {
                if(player1.shoot(1)){
                    return false;
                }
            }
        }
        else if(result==2){
            runCount = checkCards(player1, false);
            if(runCount==21){
                if(player2.shoot(3)){
                    return true;
                }
            }
            else {
                if(player2.shoot(1)){
                    return false;
                }
            }
        }
            return false;
    }

    /**
     * Funzione per pescare le carte
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
     * Controlla se il gioco è terminato.
     * @return true se uno dei due giocatori ha esaurito le vite, false altrimenti.
     */
    public boolean isGameOver() {
        return player1.getHP() <= 0 || player2.getHP() <= 0;
    }

    /**
     * Intelligenza Artificiale che segue la tabella del BlackJack
     */

    public boolean AI() {
        int firstCard1 = checkCards(player1, true); // Prima carta del giocatore umano
        int runCount2 = checkCards(player2, false); // Totale punteggio del bot

        System.out.println("AI Turn Start - Player 1 First Card: " + firstCard1 + ", AI Total: " + runCount2);

        while (runCount2 < 17) { // L'AI pesca fino a un punteggio minimo di 17
            System.out.println("AI Decision - AI Total: " + runCount2);

            // Pescare una carta
            if (deckHasCards()) { // Controlla che il mazzo non sia vuoto
                hitCard(false);
                runCount2 = checkCards(player2, false); // Aggiorna il punteggio dopo la pesca
                System.out.println("AI Draws a Card - New AI Total: " + runCount2);
            } else {
                System.out.println("Deck is empty. AI cannot draw more cards.");
                break; // Termina il ciclo se non ci sono più carte
            }
        }

        System.out.println("AI Turn End - Final AI Total: " + runCount2);
        return true; // Turno completato
    }

    /**
     * Controlla se il mazzo ha ancora carte.
     * @return true se il mazzo contiene carte, false altrimenti.
     */
    private boolean deckHasCards() {
        return !deck.isEmpty();
    }




    //GET FUNCTIONS

    //ANDRANNO SOSTITUITE CON IL GETPLAYER GENERALe

    /**
     * Ritorna il giocatore 1
     * @return il giocatore 1
     */
    public PlayerObj getPlayer1(){
        return player1;
    }

    /**
     * Ritorna il giocatore 2
     * @return il giocatore 2
     */
    public PlayerObj getPlayer2(){
        return player2;
    }

    /**
     * Ritorna il giocatore richiesto
     * @param player Se 1, player1, se 2 player2
     * @return il giocatore richiesto
     */
    public PlayerObj getPlayer(int player){
        if(player == 1){
            return player1;
        }
        else if(player==2){
            return player2;
        }
        else {
            return null;
        }
    }

}
