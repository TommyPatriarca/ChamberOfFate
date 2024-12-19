package com.cof.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards; // Lista delle carte nel mazzo

    // inizializza il mazzo con tutte le carte
    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"Clubs", "Diamonds", "Hearts", "Spades"};
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "ACE"};

        for (String suit : suits) {
            for (String value : values) {
                cards.add(new Card(suit, value));
            }
        }
        shuffleDeck();
    }

    // Mescola il mazzo
    public void shuffleDeck() {
        Collections.shuffle(cards);
    }

    // Pesca una carta
    public Card drawCard() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        } else {
            System.out.println("Hai finito il mazzo pirla");
            return null;
        }
    }

    // Restituisce il numero di carte rimaste nel mazzo
    public int getRemainingCards() {
        return cards.size();
    }
}
