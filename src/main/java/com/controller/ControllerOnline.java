package com.controller;

import com.controller.managers.cardManager.Deck;
import com.controller.objects.CardObj;
import com.controller.objects.PlayerObj;
import com.cof.okhttp.Okhttp;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Il controllore per il gioco Online
 */
public class ControllerOnline {
    private Deck deck;
    private PlayerObj player1, player2;
    private static Okhttp okhttp = new Okhttp();
    private boolean isFirstPlayer;
    private String playerKey;

    /**
     * Il costruttore del controllore
     * @param isFirstPlayer true se è il primo giocatore, false se è il secondo
     * @param playerKey l'identificativo univoco del giocatore
     */
    public ControllerOnline(boolean isFirstPlayer, String playerKey) {
        this.isFirstPlayer = isFirstPlayer;
        this.playerKey = playerKey;

        if (isFirstPlayer) {
            deck = new Deck();
            ArrayList<String> deckList = new ArrayList<>();
            for (CardObj card : deck.getDeckArr()) {
            }
            okhttp.setDeck(deckList);  // Carica il mazzo sul server
        } else {
            loadDeckFromServer();  // Recupera il mazzo dal server
        }
        player1 = new PlayerObj("Player 1");
        player2 = new PlayerObj("Player 2");
    }

    /**
     * Carica il mazzo dal Server
     */
    private void loadDeckFromServer() {
        ArrayList<String> cards = okhttp.getDeck();
        deck = new Deck();
        for (String card : cards) {
            player1.addCard(new CardObj(card));
        }
    }

    /**
     * Fa iniziare il gioco
     * @param playerName il nickname del giocatore
     */
    public void startGame(String playerName) {
        player1 = new PlayerObj(playerName);
        player2 = new PlayerObj("Opponent");
        turn();
    }

    /**
     * Funzione per gestire il turno dei giocatori
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
     * Funzione per far pescare il giocatore
     * @param isPlayerTurn true se è il turno del giocatore, false il truno del nemico
     */
    public void hitCard(boolean isPlayerTurn) {
        if (isPlayerTurn) {
            CardObj card = deck.hitCard();
            player1.addCard(card);
            okhttp.addCarta(card.getTipo(), playerKey);
        } else {
            CardObj card = deck.hitCard();
            player2.addCard(card);
            okhttp.addCarta(card.getTipo(), "Opponent");
        }
    }

    /**
     * Controlla i risultati
     * @return il risultato del confronto tra i due giocatore
     */
    public int checkResult() {
        int runCountP1 = checkCards(player1, false);
        int runCountP2 = checkCards(player2, false);

        if (runCountP1 < 22 && runCountP2 < 22) {
            return Integer.compare(runCountP2, runCountP1);
        } else if (runCountP1 > 21 && runCountP2 > 21) {
            return -1;
        } else if (runCountP1 > 21) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * Controlla il punteggio totale delle carte del giocatore
     * @param player il giocatore
     * @param firstOrAll Se è true controlla solo la prima carta del giocatore, false controlla tutte le carte del giocatore
     * @return
     */
    public int checkCards(PlayerObj player, boolean firstOrAll) {
        int runCount = 0;
        int aces = 0;
        ArrayList<CardObj> deck = player.getPlayDeck();
        for (CardObj card : deck) {
            String value = card.getTipo().substring(0, card.getTipo().length() - 1);
            switch (value) {
                case "J", "Q", "K" -> runCount += 10;
                case "A" -> {
                    runCount += 11;
                    aces++;
                }
                default -> runCount += Integer.parseInt(value);
            }
            if (firstOrAll) break;
        }
        while (runCount > 21 && aces > 0) {
            runCount -= 10;
            aces--;
        }
        return runCount;
    }

    /**
     * Funzione per finire il turno
     */
    public void endTurn() {
        okhttp.setAzioneStand(playerKey);
    }

    /**
     * Ritorna se è il turno del giocatore
     * @return true se è il truno del giocatore, false se è il truno del nemico
     */
    public boolean isMyTurn() {
        String action = okhttp.getAzione(playerKey);
        return action != null && action.equals("draw");
    }

    /**
     * Controlla le vite dei giocatori
     * @return true se un giocatore ha perso tutta la vita, false se i giocatori sono ancora vivi
     */
    public boolean checkGameOver() {
        return player1.getHP() == 0 || player2.getHP() == 0;
    }

    /**
     * Funzione per diminuire la vita del giocatore
     */
    public void decreasePlayerHealth() {
        okhttp.decreaseHealth(playerKey);
    }

    /**
     * Ritorna il giocatore 1
     * @return il giocatore 1
     */
    public PlayerObj getPlayer1() {
        return player1;
    }

    /**
     * Ritorna il giocaote 2
     * @return il giocaote 2
     */
    public PlayerObj getPlayer2() {
        return player2;
    }

    public static void main(String[] args) {

        okhttp.setGameStarted();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Are you the first player? (true/false): ");
        boolean isFirst = scanner.nextBoolean();
        System.out.println("Enter player key: ");
        String key = scanner.next();
        ControllerOnline game = new ControllerOnline(isFirst, key);

        while (!game.checkGameOver()) {
            if (game.isMyTurn()) {
                System.out.println("Your turn! Press 'h' to hit or 's' to stand: ");
                String action = scanner.next();
                if (action.equalsIgnoreCase("h")) {
                    game.hitCard(true);
                    System.out.println("You drew a card!");
                } else if (action.equalsIgnoreCase("s")) {
                    game.endTurn();
                    System.out.println("You ended your turn.");
                }
            } else {
                System.out.println("Waiting for opponent...");
                try {
                    Thread.sleep(3000); // Aggiunge un ritardo di 3 secondi tra le richieste
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Game over!");
    }
}
