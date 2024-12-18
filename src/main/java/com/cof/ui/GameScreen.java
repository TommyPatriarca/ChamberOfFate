
package com.cof.ui;

import com.cof.managers.MusicManager;
import com.cof.managers.SoundManager;
import com.controller.Controller;
import com.controller.objects.CardObj;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameScreen {
    private double xOffset = 0;
    private double yOffset = 0;
    private SoundManager soundManager = new SoundManager();
    private Controller controller;
    private HBox playerHandDisplay, opponentHandDisplay;
    private Label currentPlayerLabel, player1HP, player2HP;
    private Button drawCardButton, passTurnButton;
    private StackPane root;
    private int currentRound = 1;

    public GameScreen(Controller controller) {
        this.controller = controller;
    }

    /**
     * Funzione per mostrare la schermata di gioco
     */

    public void show(Stage primaryStage) {
        primaryStage.setMaximized(true);
        controller.startGame("Player 1");

        // Immagine di sfondo
        ImageView backgroundView = createBackground(primaryStage);

        root = new StackPane();

        // Barra del titolo personalizzata
        HBox titleBar = createCustomTitleBar(primaryStage);

        // Posizionamento delle vite dei giocatori
        BorderPane healthPane = new BorderPane();
        healthPane.setPadding(new Insets(10));

        // Vite del Player 1
        player1HP = new Label("Player 1 HP: 5");
        styleHealthLabel(player1HP, Color.LIGHTGREEN, "rgba(0, 100, 0, 0.8)", "lightgreen");
        BorderPane.setAlignment(player1HP, Pos.TOP_LEFT);
        BorderPane.setMargin(player1HP, new Insets(10));

        // Vite del Player 2
        player2HP = new Label("Player 2 HP: 5");
        styleHealthLabel(player2HP, Color.RED, "rgba(100, 0, 0, 0.8)", "red");
        BorderPane.setAlignment(player2HP, Pos.TOP_RIGHT);
        BorderPane.setMargin(player2HP, new Insets(10));

        healthPane.setLeft(player1HP);
        healthPane.setRight(player2HP);

        VBox gameArea = createGameArea();
        gameArea.setAlignment(Pos.CENTER);

        HBox bottomControls = createBottomControls();

        // Crea il menu e aggiungilo al layout
        VBox menu = createMenu();
        menu.setVisible(false); // Menu nascosto inizialmente

        // Layout principale
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(healthPane);
        mainLayout.setCenter(gameArea);
        mainLayout.setBottom(bottomControls);

        VBox fullLayout = new VBox();
        fullLayout.getChildren().addAll(titleBar, mainLayout);
        VBox.setVgrow(mainLayout, Priority.ALWAYS);

        // Aggiungi tutto al root
        root.getChildren().addAll(backgroundView, fullLayout, menu);

        // Scene e gestione ESC
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                menu.setVisible(!menu.isVisible());
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Chamber of Fate - Round " + currentRound);

        initializePlayerHands();
        primaryStage.show();
    }

    /**
     * Funzione per impostare l'immagine di sfondo in modo "responsive"
     * @return L'imageview da usare come sfondo
     */

    private ImageView createBackground(Stage primaryStage) {
        Image backgroundImage;
        if(Math.random()/2 == 0){
             backgroundImage = new Image(getClass().getResourceAsStream("/images/Table1.jpg"));
        }
        else{
             backgroundImage = new Image(getClass().getResourceAsStream("/images/Table1" +".jpg"));
        }
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(false);
        backgroundView.setFitWidth(primaryStage.getWidth());
        backgroundView.setFitHeight(primaryStage.getHeight());

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) ->
                backgroundView.setFitWidth(newValue.doubleValue()));
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) ->
                backgroundView.setFitHeight(newValue.doubleValue()));

        return backgroundView;
    }

    /**
     * Funzione per creare il vbox da usare come campo di gioco
     * @return Il box utilizzato come "campo di gioco"
     */

    private VBox createGameArea() {
        VBox gameArea = new VBox(20);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(20));

        opponentHandDisplay = new HBox(10);
        opponentHandDisplay.setAlignment(Pos.CENTER);

        playerHandDisplay = new HBox(10);
        playerHandDisplay.setAlignment(Pos.CENTER);

        gameArea.getChildren().addAll(opponentHandDisplay, playerHandDisplay);
        return gameArea;
    }

    /**
     * Funzione per creare il box con i pulsanti "draw card" e "stand"
     * @return Il box contenente i comandi per la partita
     */

    private HBox createBottomControls() {
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(20));

        currentPlayerLabel = new Label("Your Turn");
        currentPlayerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        currentPlayerLabel.setTextFill(Color.WHITE);

        drawCardButton = createStyledButton("Draw Card", "#4CAF50");
        passTurnButton = createStyledButton("Stand", "#f44336");

        drawCardButton.setOnAction(e -> {
            controller.hitCard(true);
            updateGameDisplay();
            if (controller.checkCards(controller.getPlayer1(), false) > 21) {
                resolveRound("You Busted! Opponent Wins.");
            }
        });

        passTurnButton.setOnAction(e -> resolveRound(null));

        controls.getChildren().addAll(currentPlayerLabel, drawCardButton, passTurnButton);
        return controls;
    }

    /**
     * Funzione per creare i bottoni stilizzati passandogli il testo e il colore di sfondo
     * @return Ritorna i bottoni stilizzati
     */

    private Button createStyledButton(String text, String backgroundColor) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-font-weight: bold; -fx-background-radius: 10px;");
        return button;
    }

    /**
     * Funzione per inizializzare le mani dei giocatori
     */

    private void initializePlayerHands() {
        // Aggiorna la mano del giocatore
        playerHandDisplay.getChildren().clear();
        int playerDeckSize = controller.getPlayer1().getPlayDeck().size();

        for (int i = 0; i < playerDeckSize; i++) {
            boolean animate = (i == playerDeckSize - 1) || currentRound == 1; // Anima l'ultima carta o tutte nel primo round
            playerHandDisplay.getChildren().add(createCardView(controller.getPlayer1().getPlayDeck().get(i), animate));
        }

        // Aggiorna la mano dell'avversario
        opponentHandDisplay.getChildren().clear();
        int opponentDeckSize = controller.getPlayer2().getPlayDeck().size();

        for (int i = 0; i < opponentDeckSize; i++) {
            if (i == 0) {
                // Mostra la prima carta dell'avversario
                boolean animate = currentRound == 1; // Anima solo nel primo round
                opponentHandDisplay.getChildren().add(createCardView(controller.getPlayer2().getPlayDeck().get(i), animate));
            } else {
                // Mostra il retro per tutte le altre carte
                opponentHandDisplay.getChildren().add(createBackCardView());
            }
        }
    }



    /**
     * Funzione per risolvere il risultato del round
     */

    private void resolveRound(String message) {
        revealAllCards();

        int player1Score = controller.checkCards(controller.getPlayer1(), false);
        int player2Score = controller.checkCards(controller.getPlayer2(), false);

        if (message == "You Busted! Opponent Wins.") {
            controller.getPlayer1().shoot(6);
        }
        else if (message == null) {
            if (player1Score > 21) {
                message = "You Busted! Opponent Wins.";
                controller.getPlayer1().shoot(6);
            } else {
                controller.AITurn(() -> updateGameDisplay());
                player2Score = controller.checkCards(controller.getPlayer2(), false);

                if (player2Score > 21) {
                    message = "Opponent Busted! You Win.";
                    controller.getPlayer2().shoot(6);
                } else if (player1Score == player2Score) {
                    message = "It's a draw.";
                } else if (player1Score > player2Score) {
                    message = "You Win! Opponent Fires.";
                    controller.getPlayer2().shoot(6);
                } else {
                    message = "You Lose! You Fire.";
                    controller.getPlayer1().shoot(6);
                }
            }
        }

        revealAllCards();
        showRoundResult(message);
    }

    /**
     * Funzione per far girare tutte le carte sul tavolo
     */

    private void revealAllCards() {
        opponentHandDisplay.getChildren().clear();
        controller.getPlayer2().getPlayDeck().forEach(card -> {
            opponentHandDisplay.getChildren().add(createCardView(card,true));
        });

        playerHandDisplay.getChildren().clear();
        controller.getPlayer1().getPlayDeck().forEach(card -> {
            playerHandDisplay.getChildren().add(createCardView(card,true));
        });

        updatePlayerHP();
    }

    /**
     * Funzione per mostrare i risultati dei round
     */

    private void showRoundResult(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Round Result");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        if (controller.getPlayer1().getHP() == 0 || controller.getPlayer2().getHP() == 0) {
            endGame();
        } else {
            currentRound++;
            controller.turn();
            updateGameDisplay();
        }
    }

    /**
     * Funzione per mostrare il risultato della partita
     */

    private void endGame() {
        String winner = controller.getPlayer1().getHP() > 0 ? "Player 1 Wins!" : "Player 2 Wins!";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(winner);
        alert.showAndWait();
        System.exit(0);
    }

    /**
     * Funzione per aggiornare la schermata di gioco
     */

    private void updateGameDisplay() {
        // Aggiorna la mano del giocatore
        playerHandDisplay.getChildren().clear();
        int playerDeckSize = controller.getPlayer1().getPlayDeck().size();

        for (int i = 0; i < playerDeckSize; i++) {
            boolean animate = (i == playerDeckSize - 1); // Anima solo l'ultima carta
            playerHandDisplay.getChildren().add(createCardView(controller.getPlayer1().getPlayDeck().get(i), animate));
        }

        // Aggiorna la mano dell'avversario
        opponentHandDisplay.getChildren().clear();
        int opponentDeckSize = controller.getPlayer2().getPlayDeck().size();

        for (int i = 0; i < opponentDeckSize; i++) {
            if (i == 0) {
                // Mostra la prima carta dell'avversario
                opponentHandDisplay.getChildren().add(createCardView(controller.getPlayer2().getPlayDeck().get(i), false));
            } else {
                // Mostra il retro per tutte le altre carte
                opponentHandDisplay.getChildren().add(createBackCardView());
            }
        }

        updatePlayerHP();
    }



    /**
     * Funzione per aggiornare la vita dei player
     */

    private void updatePlayerHP() {
        player1HP.setText("Player 1 HP: " + controller.getPlayer1().getHP());
        player2HP.setText("Player 2 HP: " + controller.getPlayer2().getHP());
    }

    /**
     * Funzione per creare la prospettiva del retro delle carte
     * @return Un imageview da usare come retro della carta
     */

    private ImageView createBackCardView() {
        ImageView backImageView = new ImageView(new Image(getClass().getResourceAsStream("/Cards/Back_1.png")));
        backImageView.setFitWidth(100);
        backImageView.setFitHeight(150);
        return backImageView;
    }

    /**
     * Funzione per creare la prospettiva della faccia delle carte
     * @return Un imageview da usare come faccia delle carte
     */

    private ImageView createCardView(CardObj card, boolean animate) {
        ImageView cardView = createBackCardView();
        String imagePath = card.getImagePath();

        if (animate) {
            playCardFlipAnimation(cardView, imagePath);
        } else {
            cardView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        }

        return cardView;
    }


    /**
     * Funzione per creare un animazione di rotazione per le carte
     */

    private void playCardFlipAnimation(ImageView cardView, String newImagePath) {
        SoundManager.FlipCardSound();

        RotateTransition rotateToSide = new RotateTransition(Duration.seconds(0.3), cardView);
        rotateToSide.setFromAngle(0);
        rotateToSide.setToAngle(90);
        rotateToSide.setAxis(Rotate.Y_AXIS);

        RotateTransition rotateToFront = new RotateTransition(Duration.seconds(0.3), cardView);
        rotateToFront.setFromAngle(90);
        rotateToFront.setToAngle(0);
        rotateToFront.setAxis(Rotate.Y_AXIS);

        rotateToSide.setOnFinished(event -> {
            cardView.setImage(new Image(getClass().getResourceAsStream(newImagePath)));
            rotateToFront.play();
        });

        rotateToSide.play();
    }

    /**
     * Funzione per creare il menu
     */

    private VBox createMenu() {
        // Creazione del menu
        VBox menu = new VBox(15);
        menu.setStyle("-fx-background-color: rgba(20, 20, 20, 0.95); -fx-border-color: gold; -fx-border-width: 3; -fx-padding: 20;");
        menu.setAlignment(Pos.CENTER);
        menu.setVisible(false); // Menu nascosto inizialmente

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), menu);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), menu);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        // Pulsanti del menu
        Button rulesButton = createMenuButton("Regole", this::showRules);
        Button creditsButton = createMenuButton("Crediti", this::showCredits);
        Button volumeButton = createMenuButton("Volume", this::showVolumeControl);
        Button surrenderButton = createMenuButton("Arrenditi", this::surrender);
        Button exitButton = createMenuButton("Esci", () -> System.exit(0));

        menu.getChildren().addAll(rulesButton, creditsButton, volumeButton, surrenderButton, exitButton);

        // Pulsante per aprire/chiudere il menu
        Button toggleMenuButton = createStyledButton("Menu", "#333333");
        toggleMenuButton.setStyle("-fx-background-color: rgba(40, 40, 40, 0.8); -fx-border-color: gold; -fx-border-width: 2; -fx-font-size: 16px; -fx-text-fill: gold;");
        toggleMenuButton.setOnAction(e -> {
            if (menu.isVisible()) {
                fadeOut.setOnFinished(ev -> menu.setVisible(false));
                fadeOut.play();
            } else {
                menu.setVisible(true);
                fadeIn.play();
            }
        });

        // Ritorna il menu per poterlo aggiungere al layout
        return menu;
    }


    /**
     * Funzione per creare dei bottoni stilizzati da usare nel menu
     * @return Il bottone da utilizzare nel menu
     */

    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: rgba(30, 30, 30, 0.9); -fx-border-color: gold; -fx-border-width: 2; -fx-font-size: 14px; -fx-text-fill: gold;");
        button.setOnAction(e -> action.run());
        return button;
    }

    /**
     * Funzione per mostrare il regolamento del gioco
     */

    private void showRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Regole del Gioco");
        alert.setHeaderText("Regole");
        alert.setContentText("Devo mettere le regole");
        alert.showAndWait();
    }

    /**
     * Funzione per mostrare i crediti del gioco
     */

    private void showCredits() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Crediti");
        alert.setHeaderText("Crediti");
        alert.setContentText("Creato da Tommaso Patriarca, Alessandro Anastasio, Michele Comalli e Marco Barlascini");
        alert.showAndWait();
    }

    /**
     * Funzione per mostrare i controlli del volume
     */

    private void showVolumeControl() {
        Stage volumeStage = new Stage();
        volumeStage.setTitle("Regolazione Volume");

        VBox volumeLayout = new VBox(20);
        volumeLayout.setAlignment(Pos.CENTER);
        volumeLayout.setPadding(new Insets(20));
        volumeLayout.setStyle("-fx-background-color: rgba(30, 30, 30, 0.95); -fx-border-color: gold; -fx-border-width: 3;");

        Label volumeLabel = new Label("Regolazione Volume");
        volumeLabel.setTextFill(Color.GOLD);
        volumeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // Slider per il volume
        Slider volumeSlider = new Slider(0, 1, MusicManager.getVolume());
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setMajorTickUnit(0.25);
        volumeSlider.setMinorTickCount(4);
        volumeSlider.setBlockIncrement(0.05);
        volumeSlider.setStyle(
                "-fx-control-inner-background: #222; " +
                        "-fx-accent: gold; " +
                        "-fx-track-color: #555;" +
                        "-fx-thumb-color: #FFD700;" // Colore del thumb
        );

        // Etichetta dinamica per mostrare il valore del volume
        Label volumeValueLabel = new Label(String.format("Volume: %.0f%%", MusicManager.getVolume() * 100));
        volumeValueLabel.setTextFill(Color.LIGHTGRAY);
        volumeValueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Listener per aggiornare il valore dinamico del volume
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            MusicManager.setVolume(newValue.doubleValue());
            volumeValueLabel.setText(String.format("Volume: %.0f%%", newValue.doubleValue() * 100));
        });

        // Pulsante di chiusura
        Button closeButton = new Button("Chiudi");
        closeButton.setStyle(
                "-fx-background-color: rgba(30, 30, 30, 0.8); " +
                        "-fx-border-color: gold; " +
                        "-fx-border-width: 2; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: gold; " +
                        "-fx-background-radius: 10px;"
        );
        closeButton.setOnAction(e -> volumeStage.close());

        // Layout pulsanti
        HBox buttonLayout = new HBox(closeButton);
        buttonLayout.setAlignment(Pos.CENTER);
        volumeLayout.getChildren().addAll(volumeLabel, volumeSlider, volumeValueLabel, buttonLayout);
        Scene volumeScene = new Scene(volumeLayout, 400, 250);
        volumeStage.setScene(volumeScene);
        volumeStage.show();
    }

    /**
     * Funzione per mostrare la schermata per arrendersi
     */

    private void surrender() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Arrendersi");
        alert.setHeaderText("Vuoi arrenderti?");
        alert.setContentText("Questa azione terminerà la partita.");
        if (alert.showAndWait().get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    private void styleHealthLabel(Label label, Color textColor, String bgColor, String borderColor) {
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(textColor);
        label.setStyle("-fx-background-color: " + bgColor + "; " +
                "-fx-border-color: " + borderColor + "; " +
                "-fx-border-width: 2px; " +
                "-fx-padding: 8 16; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, " + borderColor + ", 10, 0.5, 0, 0);");
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