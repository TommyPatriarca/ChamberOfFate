package com.controller;



import com.cof.okhttp.Okhttp;
import com.controller.managers.cardManager.Deck;
import com.controller.objects.CardObj;
import com.controller.objects.PlayerObj;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class ControllerOnline {

    private Deck deck;
    private PlayerObj player1, player2;

    private String playerKey, opponentKey;

    private boolean onOrOff, hostOrP;

    private int turnCount = 0;

    private Okhttp okhttp;

    /**
     * Il controller è usato per gestire tutta la parte logica del gioco
     */
    public ControllerOnline (){
            onlineGame();
    }

    /**
     * Funzione per gestire la versione online del gioco
     */
    public void onlineGame(){
        //OkHttpClient client;
    }

    /**
     * Funzione per gestire la versione offline del gioco con AI
     */
    public void offlineGame(){
        deck = new Deck();
    }

    /**
     * Funzione per far iniziare il game
     * @param playerKey l'identificativo univoco del giocatore
     */

    public void startGame(String playerKey){

        this.playerKey = playerKey;

        player1 = new PlayerObj(playerKey);

        if(!onOrOff){
            player2 = new PlayerObj("CPU");
            offlineTurn();

        }
        else {
            if(playerKey.equals("giocatore1")){
                hostOrP = true;
                player2 = new PlayerObj("giocatore2");
                opponentKey = "giocatore2";

            }
            else{
                hostOrP = false;
                player2 = new PlayerObj("giocatore1");
                opponentKey = "giocatore1";

            }

            //onlineTurn();
        }
    }

    /**
     * Funzione per gestire ogni turno
     */
    public void offlineTurn() {
        player1.resetPlayer();

        player1.addCard(deck.hitCard());
        player1.addCard(deck.hitCard());

        player2.resetPlayer();
        player2.addCard(deck.hitCard());
        player2.addCard(deck.hitCard());
    }

    public void onlineTurn(Okhttp okhttp){
        this.okhttp = okhttp;

        player1.resetPlayer();
        player2.resetPlayer();

        /*deck = new Deck();

        deck.setDeckArr(getOnlineDeck());*/

        drawOnlineCard(playerKey);
        drawOnlineCard(playerKey);


        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {
            player1.setPlayDeck(getOnlineMazzo(playerKey));
            player2.setPlayDeck(getOnlineMazzo(opponentKey));
            // Puoi anche aggiornare la UI con Platform.runLater se necessario
            Platform.runLater(() -> {
                // Aggiornamenti della UI
                System.out.println("Aggiornati i mazzi dei giocatori");
            });
        };

// Esegui il runnable con un ritardo di 1300 millisecondi
        scheduler.schedule(runnable, 1300, TimeUnit.MILLISECONDS);

     /*   Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

            }
        }, 500);   //5 centesimi
*/

        /*CardObj card1 = deck.hitCard();
        CardObj card2 = deck.hitCard();*/

    }

    /**
     * Funzione per ricevere il mazzo Online
     * @return il mazzo online
     */
    public ArrayList<CardObj> getOnlineDeck(){

        for(String string : okhttp.getDeck()){
            System.out.println(string);
        }
        ArrayList<String> stringDeck = okhttp.getDeck();

        ArrayList<CardObj> cardDeckP = new ArrayList<>();

        for (String string: stringDeck){
            cardDeckP.add(new CardObj(string));
        }

        return cardDeckP;
    }

    /**
     * Funzione per ricevere il mazzo del giocatore
     * @param playerKey il nickname del giocatore
     * @return Il mazzo del giocatore
     */
    public ArrayList<CardObj> getOnlineMazzo(String playerKey){
        ArrayList<String> cardMazzoS;
        ArrayList<CardObj> cardMazzoC = new ArrayList<>();
        cardMazzoS = okhttp.getMazzo(playerKey);

        for(String string : cardMazzoS){
            cardMazzoC.add(new CardObj(string));
            //System.out.println("Carta: " + string);

        }

        return cardMazzoC;


    }

    /**
     * Funzione per mandare il mazzo al Server
     * @param stringDeck Il mazzo
     */
    public void sendOnlineDeck(ArrayList<CardObj> stringDeck){

        ArrayList<String> cardDeckS = new ArrayList<>();

        for (CardObj cardObj: stringDeck){
            cardDeckS.add(cardObj.getTipo());
        }

        okhttp.setDeck(cardDeckS);

    }

    /**
     * Funzione per pescare la carta online
     * @param playerKey il nickname del giocatore
     */
    public void drawOnlineCard(String playerKey){

        CardObj cardDrawP;
        Deck deck = new Deck();
        deck.setDeckArr(getOnlineDeck());
        cardDrawP = deck.hitCard();
        sendOnlineDeck(deck.getDeckArr());

        okhttp.addCarta(cardDrawP.getTipo(), playerKey);
        player1.setPlayDeck(getOnlineMazzo(playerKey));

        okhttp.setAzioneDraw(playerKey);

        //okhttp.clearAzione(playerKey);

    }

    /**
     * Controllo se entrambi i giocatori sono in stand
     * @return se true, entrambi stand, se false no
     */
    public boolean checkStand(){
        String pl1 = okhttp.getAzione(playerKey);
        String pl2 = okhttp.getAzione(opponentKey);
        pl1 = pl1.replace("\"", "");
        pl2 = pl2.replace("\"", "");

        System.out.println(pl1);
        System.out.println(pl2);
        if(pl1.equals("stand") && pl2.equals("stand")){
            System.out.println("true");
            return true;
        }
        else{
            return false;
        }
    }

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
     *Funzione con contare il punteggio delle carte del giocatore
     * @param player Il giocatore a cui controllare le carte
     * @param firstOrAll Se true, controllo solo la prima, se false le controllo tutte
     * @return il punteggio del giocaotre
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

    public void nextTurnOnline(){
        okhttp.clearMazzo(playerKey);
        okhttp.clearMazzo(opponentKey);
    }

    /**
     * Funzione per controllare il risultato di entrambi i Blackjack online
     * @return Se 1, si spara il giocatore 1, se 2 il secondo,  se -1 pari
     */
    public int checkResultOnline(){

        player1.setPlayDeck(getOnlineMazzo(playerKey));
        player2.setPlayDeck(getOnlineMazzo(opponentKey));

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

            //Controllo se entrambi non abbiano sballato
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

            //Controllo se entrambi non abbiano sballato
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
     *Funzione per far sparare al giocatore che ha perso
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
                    if(onOrOff){
                        okhttp.decreaseHealth(playerKey);
                    }
                    return true;
                }
            }
            else {
                if(player1.shoot(1)){
                    if(onOrOff){
                        okhttp.decreaseHealth(playerKey);
                    }
                    return false;
                }
            }
        }
        else if(result==2){
            runCount = checkCards(player1, false);
            if(runCount==21){
                if(player2.shoot(3)){
                    if(onOrOff){
                        okhttp.decreaseHealth(opponentKey);
                    }
                    return true;
                }
            }
            else {
                if(player2.shoot(1)){
                    if(onOrOff){
                        okhttp.decreaseHealth(opponentKey);
                    }
                    return false;
                }
            }
        }
        return false;
    }
    /**
     *Funzione per far sparare al giocatore che ha perso Online
     */
    public boolean shootPlayerOnline(){
        int result = checkResultOnline();

        //Il running count del giocatore che ha vinto
        int runCount;

        if(result==1){
            runCount = checkCards(player2, false);
            //CONTROLLARE SE E' STATO FATTO BLACKJACK E NON SOLO 21
            //OTTIMIZZARE MEGLIO GLI IF, SE POSSIBILE FARE UN UNICO IF PER TUTTI E DUE I CASi
            if(runCount==21){
                if(player1.shoot(3)){
                    okhttp.decreaseHealth(playerKey);
                    return true;
                }
            }
            else {
                if(player1.shoot(1)){
                    okhttp.decreaseHealth(playerKey);
                    return false;
                }
            }
        }
        else if(result==2){
            runCount = checkCards(player1, false);
            if(runCount==21){
                if(player2.shoot(3)){
                    okhttp.decreaseHealth(opponentKey);

                    return true;
                }
            }
            else {
                if(player2.shoot(1)){
                    okhttp.decreaseHealth(opponentKey);

                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Funzione per far perscare il giocatore
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

    public ArrayList<String> getDeck() {

        Deck deck1= new Deck();
        ArrayList<String> stringDeck = new ArrayList<>();

        for(CardObj cardObj : deck1.getDeckArr()){
            stringDeck.add(cardObj.getTipo());
        }

        return stringDeck;
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
     *
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

    public void nextTurn() {
        // Cambia il turno
        if (playerKey.equals("giocatore1")) {
            okhttp.setAzioneStand(playerKey); // Azione completata per giocatore 1
            playerKey = "giocatore2";
        } else {
            okhttp.setAzioneStand(playerKey); // Azione completata per giocatore 2
            playerKey = "giocatore1";
        }
    }


    public String getOpponentKey() {
        return opponentKey;
    }

    public String getPlayerKey() {
        return playerKey;
    }
}
