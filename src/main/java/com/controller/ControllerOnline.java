package com.controller;

import com.controller.managers.cardManager.Deck;
import com.controller.objects.CardObj;
import com.controller.objects.PlayerObj;
import com.cof.okhttp.Okhttp;

import java.util.ArrayList;
import java.util.Scanner;

public class ControllerOnline {
    private Deck deck;
    private PlayerObj giocatore1, giocatore2;
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
                deckList.add(card.getTipo());
            }
            okhttp.setDeck(deck.getDeckArrAsString());  // Carica il mazzo sul server
        } else {
            loadDeckFromServer();  // Recupera il mazzo dal server
        }
        giocatore1 = new PlayerObj("Player 1");
        giocatore2 = new PlayerObj("Player 2");
    }

    private void loadDeckFromServer() {
        ArrayList<String> cards = okhttp.getDeck();
        deck = new Deck();
        for (String card : cards) {
            giocatore1.addCard(new CardObj(card));
        }
    }

    public void startGame(String playerName) {
        giocatore1 = new PlayerObj(playerName);
        giocatore2 = new PlayerObj("Opponent");
        turn();
    }

    public void turn() {
        giocatore1.resetPlayer();
        giocatore2.resetPlayer();

        giocatore1.addCard(deck.hitCard());
        giocatore1.addCard(deck.hitCard());

        giocatore2.addCard(deck.hitCard());
        giocatore2.addCard(deck.hitCard());
    }

    public void hitCard(boolean isPlayerTurn) {
        if (isPlayerTurn) {
            CardObj card = deck.hitCard();
            giocatore1.addCard(card);
            okhttp.addCarta(card.getTipo(), playerKey);
        } else {
            CardObj card = deck.hitCard();
            giocatore2.addCard(card);
            okhttp.addCarta(card.getTipo(), "Opponent");
        }
    }

    public int checkResult() {
        int runCountP1 = checkCards(giocatore1, false);
        int runCountP2 = checkCards(giocatore2, false);

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
        return giocatore1.getHP() == 0 || giocatore2.getHP() == 0;
    }

    public void decreasePlayerHealth() {
        okhttp.decreaseHealth(playerKey);
    }

    public PlayerObj getgiocatore1() {
        return giocatore1;
    }

    public PlayerObj getgiocatore2() {
        return giocatore2;
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
                } else if (action.equalsIgnoreCase("s")) {
                    game.endTurn();
                }
            } else {
                System.out.println("Waiting for opponent...");
            }
        }
        System.out.println("Game over!");
    }
}
