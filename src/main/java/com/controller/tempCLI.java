package com.controller;

import com.controller.objects.CardObj;

import java.sql.SQLOutput;
import java.util.Scanner;

/**
 * This is a temporary CLI, not to be added to the final game, but used to test the controller before
 * using it in the GUIs
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

    }
}
