package com.cof.ui;

import com.cof.managers.SoundManager;
import com.controller.Controller;
import com.controller.objects.CardObj;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class GameScreen {
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

    public void show(Stage primaryStage) {
        primaryStage.setMaximized(true);

        controller.startGame("Player 1");

        // Immagine di sfondo
        Image backgroundImage = new Image(getClass().getResourceAsStream("/images/Table.jpg"));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(false);
        backgroundView.setFitWidth(primaryStage.getWidth());
        backgroundView.setFitHeight(primaryStage.getHeight());

        // Listener per aggiornare lo sfondo alle dimensioni della finestra
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) ->
                backgroundView.setFitWidth(newValue.doubleValue()));
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) ->
                backgroundView.setFitHeight(newValue.doubleValue()));

        root = new StackPane();
        BorderPane gameLayout = new BorderPane();

        VBox topBar = createCustomTopBar(primaryStage);
        gameLayout.setTop(topBar);

        VBox gameArea = createGameArea();
        gameLayout.setCenter(gameArea);

        HBox bottomControls = createBottomControls();
        gameLayout.setBottom(bottomControls);

        // Aggiungi lo sfondo al layout principale
        root.getChildren().addAll(backgroundView, gameLayout);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chamber of Fate - Round " + currentRound);

        initializePlayerHands();
        primaryStage.show();
    }


    private void initializePlayerHands() {
        playerHandDisplay.getChildren().clear();
        playerHandDisplay.getChildren().add(new Label("Your Hand"));

        // Carte del giocatore umano
        controller.getPlayer1().getPlayDeck().forEach(card -> {
            if (card != null) {
                playerHandDisplay.getChildren().add(createCardView(card));
            }
        });

        opponentHandDisplay.getChildren().clear();
        opponentHandDisplay.getChildren().add(new Label("Opponent's Hand"));

        // Carte del bot (una scoperta, una coperta)
        boolean isFirstCard = true;
        for (CardObj card : controller.getPlayer2().getPlayDeck()) {
            if (isFirstCard) {
                opponentHandDisplay.getChildren().add(createCardView(card));
                isFirstCard = false;
            } else {
                opponentHandDisplay.getChildren().add(createBackCardView());
            }
        }

        currentPlayerLabel.setText("Your Turn");
        updatePlayerHP();
    }

    private VBox createCustomTopBar(Stage primaryStage) {
        VBox topBar = new VBox();
        topBar.setStyle("-fx-background-color: #1e1e1e;");
        topBar.setPadding(new Insets(10));

        HBox barContent = new HBox();
        barContent.setAlignment(Pos.CENTER);
        barContent.setSpacing(20);

        Label titleLabel = new Label("Chamber of Fate");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);

        player1HP = new Label("Player 1 HP: 5");
        player1HP.setTextFill(Color.WHITE);
        player2HP = new Label("Player 2 HP: 5");
        player2HP.setTextFill(Color.WHITE);

        barContent.getChildren().addAll(titleLabel, player1HP, player2HP);
        topBar.getChildren().add(barContent);

        return topBar;
    }

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

    private HBox createBottomControls() {
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(20));

        currentPlayerLabel = new Label("Your Turn");
        drawCardButton = new Button("Draw Card");
        passTurnButton = new Button("Stand");

        drawCardButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        passTurnButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");

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

    //funzione per controllare il vincitore del round
    private void resolveRound(String message) {
        revealAllCards();

        int player1Score = controller.checkCards(controller.getPlayer1(), false);
        int player2Score = controller.checkCards(controller.getPlayer2(), false);

        if (message == null) {
            if (player1Score > 21) {
                message = "You Busted! Opponent Wins.";
            } else {
                controller.AITurn(() -> updateGameDisplay()); // Passa il callback
                player2Score = controller.checkCards(controller.getPlayer2(), false);

                if (player2Score > 21) {
                    message = "Opponent Busted! You Win.";
                } else if (player1Score == player2Score) {
                    message = "It's a draw.";
                } else if (player1Score > player2Score) {
                    message = "You Win! Opponent Fires.";
                } else {
                    message = "You Lose! You Fire.";
                }
            }
        }

        showRoundResult(message);
    }





    //per la fine di un round faccio in modo di far vedere tutte le carte
    private void revealAllCards() {
        opponentHandDisplay.getChildren().clear();
        controller.getPlayer2().getPlayDeck().forEach(card -> opponentHandDisplay.getChildren().add(createCardView(card)));
    }

    //Mostro il risultato del round
    private void showRoundResult(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Round Result");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        if (controller.getPlayer1().getHP() == 3 || controller.getPlayer2().getHP() == 3) {
            endGame();
        } else {
            currentRound++;
            controller.turn();
            updateGameDisplay();
        }
    }

    private void endGame() {
        String winner = controller.getPlayer1().getHP() > 0 ? "Player 1 Wins!" : "Player 2 Wins!";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(winner);
        alert.showAndWait();
        System.exit(0);
    }

    private void updateGameDisplay() {
        initializePlayerHands();
        updatePlayerHP();

        // Mostra le carte visibili del bot
        opponentHandDisplay.getChildren().clear();
        for (CardObj card : controller.getPlayer2().getPlayDeck()) {
            opponentHandDisplay.getChildren().add(createCardView(card));
        }
    }



    private void updatePlayerHP() {
        player1HP.setText("Player 1 HP: " + controller.getPlayer1().getHP());
        player2HP.setText("Player 2 HP: " + controller.getPlayer2().getHP());
    }


    private ImageView createBackCardView() {
        ImageView backImageView = new ImageView(new Image(getClass().getResourceAsStream("/Cards/Back_1.png")));
        backImageView.setFitWidth(100);
        backImageView.setFitHeight(150);
        return backImageView;
    }

    private ImageView createCardView(CardObj card) {
        ImageView cardView = createBackCardView(); // Inizia come coperta
        String imagePath = card.getImagePath();

        // Applica l'animazione di flip quando viene aggiunta
        playCardFlipAnimation(cardView, imagePath);

        return cardView;
    }

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

        // Cambia immagine e avvia la seconda parte dell'animazione
        rotateToSide.setOnFinished(event -> {
            cardView.setImage(new Image(getClass().getResourceAsStream(newImagePath)));
            rotateToFront.play();
        });

        // Avvia la prima parte dell'animazione
        rotateToSide.play();
    }



}
