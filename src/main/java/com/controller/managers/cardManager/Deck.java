package com.controller.managers.cardManager;

import com.controller.objects.CardObj;

import java.util.ArrayList;

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
            String tempSuit = null;

            for(int j=0;j<13;j++){

                tempSuit = switch (i) {
                    case 0 -> clubs;
                    case 1 -> diamonds;
                    case 2 -> hearts;
                    case 3 -> spades;
                    default -> tempSuit;
                };

                    //Ace of Spades
                    if (j == 0) {
                        deckArr.add(new CardObj("A" + tempSuit));
                    }

                    //Spade numbers
                    else if (j < 10) {
                        deckArr.add(new CardObj((j + 1) + tempSuit));

                    }

                    else {
                        switch (j){
                            case 10:
                                deckArr.add(new CardObj( "J" + tempSuit));
                                break;
                            case 11:
                                deckArr.add(new CardObj( "Q" + tempSuit));
                                break;
                            case 12:
                                deckArr.add(new CardObj( "K" + tempSuit));
                                break;
                        }

                    }

            }

        }
        printDeck();


    }

    public void printDeck(){

        for(CardObj obj : deckArr){
            System.out.println(obj.getTipo());

        }
    }

    public void shuffle(){

    }


}
