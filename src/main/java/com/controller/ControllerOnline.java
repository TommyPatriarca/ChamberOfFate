package com.controller;

import com.controller.managers.cardManager.Deck;
import com.controller.objects.CardObj;
import com.controller.objects.PlayerObj;
import com.cof.okhttp.Okhttp;
import java.util.ArrayList;

public class ControllerOnline {

    private Deck deck;
    private PlayerObj player1, player2;
    private Okhttp okhttp;
    private String playerKey;
    private boolean isMyTurn;

    /**
     * Costruttore per la modalità online del gioco
     */
    public ControllerOnline(String playerKey) {
        this.playerKey = playerKey;
        this.okhttp = new Okhttp();
        onlineGame();
    }

    public void startOnlineGame(String playerKey) {
        this.playerKey = playerKey;

        // Attendi che entrambi i giocatori siano nella lobby
        while (!okhttp.countPlayers().equals("2")) {
            System.out.println("In attesa di un altro giocatore...");
            try {
                Thread.sleep(2000); // Aspetta 2 secondi prima di controllare di nuovo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Recupera lo stato iniziale del gioco dal server
        loadOnlineGameState();

        // Controlla se è il turno del giocatore
        checkTurn();
    }


    /**
     * Funzione per inizializzare il gioco online
     */
    public void onlineGame() {
        deck = new Deck();
        player1 = new PlayerObj("Player1");
        player2 = new PlayerObj("Player2");

        if (okhttp.countPlayers().equals("2")) {
            loadOnlineGameState();
            checkTurn();
        }
    }

    /**
     * Carica lo stato del gioco online
     */
    public void loadOnlineGameState() {
        // Recupera le carte per il giocatore 1 dal server
        ArrayList<String> player1Deck = okhttp.getMazzo("player1");
        player1.getPlayDeck().clear();
        for (String card : player1Deck) {
            player1.addCard(new CardObj(card));
        }

        // Recupera le carte per il giocatore 2 dal server
        ArrayList<String> player2Deck = okhttp.getMazzo("player2");
        player2.getPlayDeck().clear();
        for (String card : player2Deck) {
            player2.addCard(new CardObj(card));
        }
    }


    /**
     * Controlla di chi è il turno
     */
    public void checkTurn() {
        String action = okhttp.getAzione(playerKey);
        isMyTurn = action.equals("your_turn");
    }

    /**
     * Pesca una carta e aggiorna il server
     */
    public void hitCard() {
        if (isMyTurn) {
            CardObj newCard = deck.hitCard();
            player1.addCard(newCard);
            okhttp.addCarta(newCard.getTipo(), playerKey);
            endTurn();
        }
    }

    /**
     * Conclude il turno e notifica il server
     */
    public void endTurn() {
        isMyTurn = false;
        okhttp.updateTurn(playerKey);
    }

    /**
     * Controlla se il gioco è terminato leggendo la salute dei giocatori
     * @return true se uno dei due giocatori ha esaurito la vita
     */
    public boolean isGameOver() {
        int player1HP = Integer.parseInt(okhttp.getHealth("player1"));
        int player2HP = Integer.parseInt(okhttp.getHealth("player2"));

        player1.decrementHP();
        player2.decrementHP();

        return player1HP <= 0 || player2HP <= 0;
    }

    /**
     * Controlla il risultato della partita
     * @return il vincitore
     */
    public int checkResult() {
        int player1Score = checkCards(player1, false);
        int player2Score = checkCards(player2, false);

        if (player1Score > 21 && player2Score > 21) return -1; // Pareggio
        if (player1Score > 21) return 2;  // Player 2 vince
        if (player2Score > 21) return 1;  // Player 1 vince
        return player1Score > player2Score ? 1 : 2;
    }

    /**
     * Calcola il punteggio delle carte del giocatore
     * @param player Il giocatore
     * @param firstOrAll Se true calcola solo la prima carta
     * @return il punteggio totale
     */
    public int checkCards(PlayerObj player, boolean firstOrAll) {
        int score = 0;
        int aces = 0;
        for (CardObj cardObj : player.getPlayDeck()) {
            String card = cardObj.getTipo().substring(0, cardObj.getTipo().length() - 1);
            switch (card) {
                case "J", "Q", "K" -> score += 10;
                case "A" -> {
                    score += 11;
                    aces++;
                }
                default -> score += Integer.parseInt(card);
            }
            if (firstOrAll) break;
        }
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }
        return score;
    }

    /**
     * Riduce la salute del giocatore
     */
    public void reduceHealth(String playerKey) {
        okhttp.decreaseHealth(playerKey);
    }

    /**
     * Controlla se ci sono carte disponibili
     * @return true se ci sono carte nel mazzo
     */
    private boolean deckHasCards() {
        return !deck.isEmpty();
    }

    public PlayerObj getPlayer1(){
        return player1;
    }

    public PlayerObj getPlayer2(){
        return player2;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

}
