package com.controller;

import com.controller.objects.CardObj;

import java.sql.SQLOutput;
import java.util.Scanner;

/**
 * Questa è una CLI temporanea, che non sarà aggiunta al gioco finale, ma è solo usata per
 * testare il controllore prima di aggiungerlo alla GUI
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

    /**
     * Turno del giocatore 1
     */
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

    /**
     * Turno del giocatore 2
     */
    public void pl2Turn(){
        controller.AITurn();
        int result;

        System.out.println("Il giocatore ha pescato le seguenti carte: ");

        for(int i=0;i<controller.getPlayer2().getPlayDeck().size(); i++){
            System.out.println(controller.getPlayer2().getPlayDeck().get(i).getTipo());
        }
        System.out.println("Totale : " + controller.checkCards(controller.getPlayer2(), false));

        System.out.println("Risultato sparo " + controller.checkResult());

        result = controller.checkResult();

        if(result!=-1){
            if(controller.shootPlayer()){
                System.out.println("Il giocatore"  + result + " si è sparato!");
                System.out.println("Il giocatore ha ora " + controller.getPlayer(result).getHP());
            }
            else {
                System.out.println("Il giocatore" + result + " si è salvato");
            }

        }


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
