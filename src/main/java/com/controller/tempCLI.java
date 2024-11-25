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
    private int scelta;

    //New controller in offline mode
    Controller controller = new Controller(false);

    public tempCLI(){
        startGame();
    }

    public void startGame(){
        System.out.println("Nome: ");
        controller.startGame(scanner.nextLine());

        pl1Turn();

    }

    public  void pl1Turn(){

        System.out.println("Hai le seguenti carte: ");
        printCards();

        System.out.println("Il giocatore 2 ha la seguente carta\n" + controller.getPlayer2().getPlayDeck().getFirst().getTipo());

        while (true){

            System.out.println("Cosa vuoi fare: \n1)Hit\n2)Stand");

            scelta = scanner.nextInt();

            if(scelta == 1){
                controller.hitCard(true);
                if(printCards()){
                    System.out.println("SBALLATO!");
                    pl2Turn();
                }

            }
            else {
                pl2Turn();
            }

        }
    }

    public void pl2Turn(){
        controller.AITurn();

        System.out.println("Il giocatore ha pescato le seguenti carte: ");


        for(int i=0;i<controller.getPlayer2().getPlayDeck().size(); i++){
            System.out.println(controller.getPlayer2().getPlayDeck().get(i).getTipo());
        }
        System.out.println("Totale : " + controller.checkCards(controller.getPlayer2(), false));

        System.out.println("Scrivi 1 per prossimo turno: ");

        if(scanner.nextInt() ==1){
            controller.turn();
            pl1Turn();
        }
    }

    /**
     *
     * @return true se sballato, false se sotto il 22
     */
    public boolean printCards(){
        int runCount;
        for(CardObj card : controller.getPlayer1().getPlayDeck()){
            System.out.println(card.getTipo());
        }
        runCount = controller.checkCards(controller.getPlayer1(), false);

        System.out.println("Totale: " + runCount);

        if(runCount >21){
            return true;
        }
        else {
            return false;
        }
    }

    public Controller getController(){
        return controller;
    }
}
