package com.cof.ui;

import com.controller.Controller;
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
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameScreen {
    private Controller controller;
    private VBox playerHandDisplay, opponentHandDisplay;
    private Label currentPlayerLabel;
    private Button drawCardButton, passTurnButton;
    private StackPane root;

    public GameScreen(Controller controller) {
        this.controller = controller;
    }

    public void show(Stage primaryStage) {
        controller.startGame("Player 1"); // Avvia il gioco

        root = new StackPane();
        BorderPane gameLayout = new BorderPane();

        VBox gameArea = createGameArea();
        gameLayout.setCenter(gameArea);

        HBox bottomControls = createBottomControls();
        gameLayout.setBottom(bottomControls);

        root.getChildren().add(gameLayout);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Offline Game");
        primaryStage.show();

        updateGameDisplay(); // Inizializza la GUI con lo stato corrente del gioco
    }

    private VBox createGameArea() {
        VBox gameArea = new VBox(20);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(20));

        // Area avversario
        opponentHandDisplay = new VBox(10);
        opponentHandDisplay.setAlignment(Pos.CENTER);
        opponentHandDisplay.getChildren().add(new Label("Opponent's Hand"));

        // Area giocatore
        playerHandDisplay = new VBox(10);
        playerHandDisplay.setAlignment(Pos.CENTER);
        playerHandDisplay.getChildren().add(new Label("Your Hand"));

        gameArea.getChildren().addAll(opponentHandDisplay, playerHandDisplay);
        return gameArea;
    }

    private HBox createBottomControls() {
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(20));

        currentPlayerLabel = new Label("Your Turn");
        drawCardButton = new Button("Draw Card");
        passTurnButton = new Button("Pass Turn");

        drawCardButton.setOnAction(e -> {
            controller.hitCard(true); // Pesca una carta per il giocatore
            updateGameDisplay();
            if (controller.checkCards(controller.getPlayer1(), false) > 21) {
                showGameOver("Busted! Opponent Wins.");
            }
        });

        passTurnButton.setOnAction(e -> {
            controller.AITurn(); // Logica del turno dell'IA
            updateGameDisplay();
            checkGameResult();
        });

        controls.getChildren().addAll(currentPlayerLabel, drawCardButton, passTurnButton);
        return controls;
    }

    private void updateGameDisplay() {
        // Aggiorna le carte del giocatore
        playerHandDisplay.getChildren().clear();
        playerHandDisplay.getChildren().add(new Label("Your Hand"));
        controller.getPlayer1().getPlayDeck().forEach(card ->
                playerHandDisplay.getChildren().add(createCardView(card.getTipo()))
        );

        // Aggiorna le carte dell'avversario
        opponentHandDisplay.getChildren().clear();
        opponentHandDisplay.getChildren().add(new Label("Opponent's Hand"));
        controller.getPlayer2().getPlayDeck().forEach(card ->
                opponentHandDisplay.getChildren().add(createBackCardView()) // Carte nascoste
        );

        currentPlayerLabel.setText("Your Turn");
    }

    private ImageView createCardView(String cardType) {
        // Percorso basato su risorse del tuo progetto
        String imagePath = "resources/Cards/" + "Clubs_2" + ".png";
        ImageView cardImageView = new ImageView(new Image(getClass().getResourceAsStream("/cards/Clubs_2.png")));
        cardImageView.setFitWidth(100);
        cardImageView.setFitHeight(150);

        // Animazione di rotazione per la carta
        playCardFlipAnimation(cardImageView);
        return cardImageView;
    }

    private ImageView createBackCardView() {
        ImageView backImageView = new ImageView(new Image(getClass().getResourceAsStream("/cards/Back_1.png")));
        backImageView.setFitWidth(100);
        backImageView.setFitHeight(150);
        return backImageView;
    }


    private void playCardFlipAnimation(ImageView cardView) {
        cardView.setRotationAxis(Rotate.Y_AXIS);

        RotateTransition flipToBack = new RotateTransition(Duration.seconds(0.5), cardView);
        flipToBack.setFromAngle(0);
        flipToBack.setToAngle(90);

        RotateTransition flipToFront = new RotateTransition(Duration.seconds(0.5), cardView);
        flipToFront.setFromAngle(90);
        flipToFront.setToAngle(0);

        flipToBack.setOnFinished(event -> flipToFront.play());
        flipToBack.play();
    }

    private void checkGameResult() {
        int result = controller.checkResult();
        if (result == 1) {
            showGameOver("You Lose!");
        } else if (result == 2) {
            showGameOver("You Win!");
        } else if (result == -1) {
            showGameOver("It's a Draw!");
        }
    }

    private void showGameOver(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        // Puoi aggiungere logica per resettare il gioco o tornare al menu principale
    }
}
