package com.controller;

import com.controller.objects.CardObj;

import java.sql.SQLOutput;
import java.util.Scanner;

/**
 * CLI Temporanea che non sarà aggiunta al gioco finale, ma usata solo per testare la logica prima che sia aggiunta alla GUI
 */
public class tempCLI {
    private Scanner scanner = new Scanner(System.in);

    //New controller in offline mode
    Controller controller = new Controller(false);

    public tempCLI(){
        startGame();
    }

    public void startGame(){
        System.out.println("Nome: ");
        controller.startGame(scanner.nextLine());

        System.out.println("Hai le seguenti carte: ");

        for(CardObj card : controller.getPlayer1().getPlayDeck()){
            System.out.println(card.getTipo());
        }

        System.out.println("Totale : " + controller.checkCards(controller.getPlayer1()));

        System.out.println("Il giocatore " + controller.getPlayer2().getName() + " ha la seguente carta:");
        System.out.println(controller.getPlayer2().getPlayDeck().getFirst().getTipo());

        System.out.println("Cosa vuoi fare? \n1) Hit\n2) Stai");

        switch (scanner.nextInt()){
            case 1:
                controller.hitCard(true);
                break;
            case 2:
                break;
        }

        System.out.println("Hai le seguenti carte: ");

        for(CardObj card : controller.getPlayer1().getPlayDeck()){
            System.out.println(card.getTipo());
        }

        System.out.println("Totale : " + controller.checkCards(controller.getPlayer1()));


    }
}
