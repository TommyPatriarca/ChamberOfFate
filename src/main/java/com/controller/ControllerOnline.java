package com.controller;

import com.controller.managers.cardManager.Deck;
import com.controller.objects.CardObj;
import com.controller.objects.PlayerObj;
import com.cof.okhttp.Okhttp;

import java.util.ArrayList;
import java.util.Scanner;

public class ControllerOnline {
    private Deck deck;
    private PlayerObj player1, player2;
    private Okhttp okhttp = new Okhttp();
    private boolean isFirstPlayer;
    private String playerKey;

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

    private void loadDeckFromServer() {
        ArrayList<String> cards = okhttp.getDeck();
        deck = new Deck();
        for (String card : cards) {
            player1.addCard(new CardObj(card));
        }
    }

    public void startGame(String playerName) {
        player1 = new PlayerObj(playerName);
        player2 = new PlayerObj("Opponent");
        turn();
    }

    public void turn() {
        player1.resetPlayer();
        player2.resetPlayer();

        player1.addCard(deck.hitCard());
        player1.addCard(deck.hitCard());

        player2.addCard(deck.hitCard());
        player2.addCard(deck.hitCard());
    }

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

    public void endTurn() {
        okhttp.setAzioneStand(playerKey);
    }

    public boolean isMyTurn() {
        String action = okhttp.getAzione(playerKey);
        return action != null && action.equals("draw");
    }

    public boolean checkGameOver() {
        return player1.getHP() == 0 || player2.getHP() == 0;
    }

    public void decreasePlayerHealth() {
        okhttp.decreaseHealth(playerKey);
    }

    public PlayerObj getPlayer1() {
        return player1;
    }

    public PlayerObj getPlayer2() {
        return player2;
    }

    public static void main(String[] args) {
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
