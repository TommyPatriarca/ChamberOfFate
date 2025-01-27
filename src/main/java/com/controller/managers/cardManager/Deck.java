package com.controller.managers.cardManager;

import com.controller.objects.CardObj;
import com.controller.objects.PlayerObj;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Il mazzo della partita
 */
public class Deck {

    private ArrayList<CardObj> deckArr = new ArrayList<>();
    private String hearts = "H", diamonds = "D", clubs = "C", spades = "S";
/*
     Suit = Seme

     Hearts= Cuori
     Diamonds = Quadri
     Clubs = Fiori
     Spades = Picche

*/

    /**
     * Il mazzo completo della partita, composto da 52 carte
     */
    public Deck(){
        //Creo 1 mazzo da 52

        for(int i=0; i<4;i++){

            //Temp card suit String
            String tempSuit = null;

            for(int j=0;j<13;j++){

                tempSuit = switch (i) {
                    case 0 -> clubs;
                    case 1 -> diamonds;
                    case 2 -> hearts;
                    case 3 -> spades;
                    default -> tempSuit;
                };

                    //ACES
                    if (j == 0) {
                        deckArr.add(new CardObj("A" + tempSuit));
                    }

                    //NUMBERS
                    else if (j < 10) {
                        deckArr.add(new CardObj((j + 1) + tempSuit));

                    }

                    //ROYALS
                    else {
                        switch (j){
                            //JOLLY
                            case 10:
                                deckArr.add(new CardObj( "J" + tempSuit));
                                break;
                            //QUEEN
                            case 11:
                                deckArr.add(new CardObj( "Q" + tempSuit));
                                break;
                            //KING
                            case 12:
                                deckArr.add(new CardObj( "K" + tempSuit));
                                break;
                        }

                    }

            }

        }
        //printDeck();

        shuffleDeck();

    }




    /**
     * Funzione per mischiare il mazzo
     */
    public void shuffleDeck(){
        Collections.shuffle(deckArr);
        //printDeck();
    }

    /**
     * Funzione per pescare una carta
     * @return la carta pescata
     */
    public CardObj hitCard() {
        if (deckArr.isEmpty()) {
            System.out.println("Deck is empty. Regenerating the deck...");
            regenerateDeck(); // Rigenera il mazzo
        }

        if (!deckArr.isEmpty()) {
            CardObj tempC = deckArr.get(deckArr.size() - 1); // Ultima carta del mazzo
            deckArr.remove(deckArr.size() - 1); // Rimuovi la carta pescata
            return tempC;
        } else {
            return null; // Restituisci null se il mazzo non è rigenerabile
        }
    }

    /**
     * Rigenera un nuovo mazzo completo e lo mescola.
     */
    private void regenerateDeck() {
        deckArr.clear();
        String[] suits = {clubs, diamonds, hearts, spades};
        for (String suit : suits) {
            for (int i = 0; i < 13; i++) {
                String cardValue;
                if (i == 0) cardValue = "A"; // Assi
                else if (i == 10) cardValue = "J"; // Fanti
                else if (i == 11) cardValue = "Q"; // Regine
                else if (i == 12) cardValue = "K"; // Re
                else cardValue = String.valueOf(i + 1); // Numeri
                deckArr.add(new CardObj(cardValue + suit));
            }
        }
        shuffleDeck(); // Mischia il mazzo appena creato
    }


    /**
     * Ritorna se il mazzo è vuoto
     * @return true se il mazzo è vuoto, false se nel mazzo ci sono carte
     */
    public boolean isEmpty() {
        if(deckArr.isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Ritorna il mazzo do carte
     * @return il mazzo do carte
     */
    public ArrayList<CardObj> getDeckArr() {
        return deckArr;
    }

    public void setDeckArr(ArrayList<CardObj> onlineDeck) {
        this.deckArr = deckArr;
    }
}
