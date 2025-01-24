package com.cof.player;

import com.cof.game.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe del giocatore
 */
public class Player {
    private String name;
    private int health = 5; // Correzione di "healt" in "health"
    private List<Card> hand; // Lista delle carte del giocatore

    /**
     * Il costruttore del giocaotre
     * @param name il nome del giocatore
     */
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>(); // Inizializza la mano del giocatore
    }

    /**
     * Rimuove punti vita al giocatore
     * @param amount il danno
     */
    public void removeLife(int amount) {
        if (health > amount) {
            health -= amount;
        } else {
            health = 0;
            System.out.println(name + " ha perso la partita!");
        }
    }

    /**
     * Aggiunge punti vita al giocatore (massimo 5)
     * @param amount il danno
     */
    public void addLife(int amount) {
        if (5 - health > amount) {
            health += amount;
        } else {
            health = 5;
        }
    }

    /**
     * Pesca una carta e la aggiunge alla mano
     * @param card la carta
     */
    public void drawCard(Card card) {
        if (card != null) {
            hand.add(card);
        } else {
            System.out.println("Nessuna carta pescata!");
        }
    }

    /**
     * Calcola il valore delle carte nella mano (come nel Black Jack)
     * @return il totale delle carte in mano
     */
    public int calculateHandValue() {
        int totalValue = 0;
        int aceCount = 0;

        for (Card card : hand) {
            String value = card.getValue();

            // Converte il valore della carta in un numero
            switch (value) {
                case "J":
                case "Q":
                case "K":
                    totalValue += 10;
                    break;
                case "ACE":
                    aceCount++;
                    totalValue += 11; // Considera l'ACE come 11 inizialmente
                    break;
                default:
                    totalValue += Integer.parseInt(value); // Converte il valore numerico
                    break;
            }
        }

        // Gestisce gli ACE: riduce il valore da 11 a 1 se necessario
        while (totalValue > 21 && aceCount > 0) {
            totalValue -= 10;
            aceCount--;
        }

        return totalValue;
    }

    /**
     * Restituisce il nome del giocatore
     * @return il nome del giocatore
     */
    public String getName() {
        return name;
    }

    /**
     * Restituisce i punti vita del giocatore
     * @return la vita del giocatore
     */
    //
    public int getHealth() {
        return health;
    }

    /**
     * Restituisce le carte nella mano del giocatore
     * @return le carte in mano del giocatore
     */
    public List<Card> getHand() {
        return hand;
    }

    /**
     * Stampa lo stato del giocatore
     * @return stato del giocatore
     */
    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", health=" + health +
                ", hand=" + hand +
                '}';
    }
}
