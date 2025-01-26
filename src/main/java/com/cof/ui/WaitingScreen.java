package com.cof.ui;

import com.cof.okhttp.Okhttp;
import com.cof.utils.FontUtils;
import com.controller.Controller;
import com.controller.ControllerOnline;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Scanner;

import static javafx.application.Application.launch;

public class WaitingScreen {

    private double xOffset = 0;
    private double yOffset = 0;
    private Timeline checkPlayersTimeline;
    private Okhttp okhttp = new Okhttp();
    private Stage stage;

    public void show(Stage primaryStage) {
        this.stage = primaryStage;

        // UI esistente per mostrare la schermata di attesa
        HBox titleBar = createCustomTitleBar(primaryStage);
        ImageView backgroundView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/ChargingScreen.jpg"))));
        backgroundView.setFitWidth(primaryStage.getWidth());
        backgroundView.setFitHeight(primaryStage.getHeight());

        StackPane root = new StackPane(backgroundView);
        VBox layoutWithBar = new VBox(titleBar, root);
        Scene scene = new Scene(layoutWithBar, primaryStage.getWidth(), primaryStage.getHeight(), Color.BLACK);

        primaryStage.setScene(scene);
        primaryStage.show();

        // Controllo periodico per verificare se ci sono due giocatori
        checkPlayersTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            String playerCount = okhttp.countPlayers();
            if (playerCount != null && Integer.parseInt(playerCount) >= 2) {
                startGame();
            }
        }));
        checkPlayersTimeline.setCycleCount(Timeline.INDEFINITE);
        checkPlayersTimeline.play();
    }

    private void startGame() {
        checkPlayersTimeline.stop(); // Ferma il controllo periodico

        Scanner scanner = new Scanner(System.in);
        System.out.println("Are you the first player? (true/false): ");
        boolean isFirst = scanner.nextBoolean();
        System.out.println("Enter player key: ");
        String key = scanner.next();

        ControllerOnline controller = new ControllerOnline(isFirst, key);
        controller.startGame(isFirst ? "Player 1" : "Player 2");

        while (!controller.checkGameOver()) {
            if (controller.isMyTurn()) {
                System.out.println("Your turn! Press 'h' to hit or 's' to stand: ");
                String action = scanner.next();
                if (action.equalsIgnoreCase("h")) {
                    controller.hitCard(true);
                    System.out.println("You drew a card!");
                } else if (action.equalsIgnoreCase("s")) {
                    controller.endTurn();
                    System.out.println("You ended your turn.");
                }
            } else {
                System.out.println("Waiting for opponent...");
                try {
                    Thread.sleep(3000); // Aggiunge un ritardo di 5 secondi tra le richieste
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Game over!");
    }



    private HBox createCustomTitleBar(Stage stage) {
        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setStyle("-fx-background-color: linear-gradient(to right, #1E1E1E, #333333); -fx-padding: 4; -fx-border-color: #444; -fx-border-width: 0 0 1 0;");
        titleBar.setPrefHeight(40);

        // Title label
        Label titleLabel = new Label("Chamber of Fate");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Arial';");
        titleLabel.setPadding(new Insets(0, 10, 0, 10));

        // Bottone per minimizzare la finestra
        Button minimizeButton = new Button("_");
        minimizeButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 2 10 2 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-radius: 5;"
        );
        minimizeButton.setOnMouseEntered(e -> minimizeButton.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-border-radius: 5;"));
        minimizeButton.setOnMouseExited(e -> minimizeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-radius: 5;"));
        minimizeButton.setOnAction(e -> stage.setIconified(true));

        // Bottone per chiudere il gioco
        Button closeButton = new Button("X");
        closeButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 2 10 2 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-radius: 5;"
        );
        closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-background-color: #FF5C5C; -fx-text-fill: white; -fx-border-radius: 5;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-radius: 5;"));
        closeButton.setOnAction(e -> System.exit(0));

        // Funzione per trascinare la finestra
        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        titleBar.getChildren().addAll(titleLabel, spacer, minimizeButton, closeButton);
        return titleBar;
    }
}
