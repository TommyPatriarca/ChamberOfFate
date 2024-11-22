package com.controller.managers.cardManager;

import com.controller.objects.CardObj;

import java.util.ArrayList;
import java.util.Collections;

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
     * Function to print the entire deck
     */
    public void printDeck(){

        System.out.println("||||||||||||");
        System.out.println("DECK:");
        for(CardObj obj : deckArr){
            System.out.println(obj.getTipo());

        }
        System.out.println("||||||||||||");

    }

    /**
     * Function used to shuffle the deck
     */
    public void shuffleDeck(){
        Collections.shuffle(deckArr);
        //printDeck();
    }

    public CardObj hitCard() {
        CardObj tempC = deckArr.getLast();
        deckArr.removeLast();

        return tempC;
    }


}
