package com.cof.ui;

import com.controller.Controller;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private Controller controller;
    private HBox playerHandDisplay, opponentHandDisplay;
    private Label currentPlayerLabel;
    private Button drawCardButton, passTurnButton;
    private StackPane root;

    public GameScreen(Controller controller) {
        this.controller = controller;
    }

    public void show(Stage primaryStage) {
        // Configura lo stile del palco PRIMA di renderlo visibile
        if (!primaryStage.isShowing()) {
            primaryStage.initStyle(StageStyle.UNDECORATED); // Rimuove la barra superiore standard
        }

        primaryStage.setMaximized(true); // Schermo intero

        controller.startGame("Player 1"); // Avvia il gioco

        root = new StackPane();
        BorderPane gameLayout = new BorderPane();

        VBox topBar = createCustomTopBar(primaryStage);
        gameLayout.setTop(topBar);

        VBox gameArea = createGameArea();
        gameLayout.setCenter(gameArea);

        HBox bottomControls = createBottomControls();
        gameLayout.setBottom(bottomControls);

        root.getChildren().add(gameLayout);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene); // Configura la scena
        primaryStage.setTitle("Chamber of Fate - Fullscreen Modern UI");

        // Mostra le carte iniziali
        initializePlayerHands();
        primaryStage.show(); // Mostra il palco
    }

    private void initializePlayerHands() {
        // Inizializza le carte del giocatore
        playerHandDisplay.getChildren().clear();
        playerHandDisplay.getChildren().add(new Label("Your Hand"));
        controller.getPlayer1().getPlayDeck().forEach(card ->
                playerHandDisplay.getChildren().add(createCardView(card.getTipo()))
        );

        // Inizializza le carte dell'avversario
        opponentHandDisplay.getChildren().clear();
        opponentHandDisplay.getChildren().add(new Label("Opponent's Hand"));

        boolean isFirstCard = true;
        for (var card : controller.getPlayer2().getPlayDeck()) {
            if (isFirstCard) {
                opponentHandDisplay.getChildren().add(createCardView(card.getTipo())); // Prima carta visibile
                isFirstCard = false;
            } else {
                opponentHandDisplay.getChildren().add(createBackCardView()); // Carte successive nascoste
            }
        }

        currentPlayerLabel.setText("Your Turn");
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

        Button minimizeButton = new Button("_");
        minimizeButton.setOnAction(e -> primaryStage.setIconified(true));
        styleBarButton(minimizeButton);

        Button closeButton = new Button("X");
        closeButton.setOnAction(e -> primaryStage.close());
        styleBarButton(closeButton);

        barContent.getChildren().addAll(titleLabel, minimizeButton, closeButton);
        topBar.getChildren().add(barContent);

        return topBar;
    }


    private void styleBarButton(Button button) {
        button.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5;"));
    }

    private VBox createGameArea() {
        VBox gameArea = new VBox(20);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(20));

        opponentHandDisplay = new HBox(10);
        opponentHandDisplay.setAlignment(Pos.CENTER);
        opponentHandDisplay.getChildren().add(new Label("Opponent's Hand"));

        playerHandDisplay = new HBox(10);
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

        drawCardButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        passTurnButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");

        drawCardButton.setOnAction(e -> {
            controller.hitCard(true);
            updateGameDisplay();
            if (controller.checkCards(controller.getPlayer1(), false) > 21) {
                showGameOver("Busted! Opponent Wins.");
            }
        });

        passTurnButton.setOnAction(e -> {
            controller.AITurn();
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

        // Mostra la prima carta dell'avversario visibile
        boolean isFirstCard = true;
        for (var card : controller.getPlayer2().getPlayDeck()) {
            if (isFirstCard) {
                opponentHandDisplay.getChildren().add(createCardView(card.getTipo())); // Prima carta visibile
                isFirstCard = false;
            } else {
                opponentHandDisplay.getChildren().add(createBackCardView()); // Carte successive nascoste
            }
        }

        currentPlayerLabel.setText("Your Turn");
    }


    private ImageView createCardView(String cardType) {
        ImageView cardImageView = new ImageView(new Image(getClass().getResourceAsStream("/cards/Back_1.png")));
        cardImageView.setFitWidth(100);
        cardImageView.setFitHeight(150);

        // Imposta l'animazione per mostrare la faccia della carta
        playCardFlipAnimation(cardImageView, cardType);
        return cardImageView;
    }

    private ImageView createBackCardView() {
        ImageView backImageView = new ImageView(new Image(getClass().getResourceAsStream("/cards/Back_1.png")));
        backImageView.setFitWidth(100);
        backImageView.setFitHeight(150);
        return backImageView;
    }

    private void playCardFlipAnimation(ImageView cardView, String cardType) {
        cardView.setRotationAxis(Rotate.Y_AXIS);

        // Rotazione per nascondere il retro
        RotateTransition flipToBack = new RotateTransition(Duration.seconds(0.5), cardView);
        flipToBack.setFromAngle(0);
        flipToBack.setToAngle(90);

        // Rotazione per mostrare la faccia
        RotateTransition flipToFront = new RotateTransition(Duration.seconds(0.5), cardView);
        flipToFront.setFromAngle(90);
        flipToFront.setToAngle(0);

        flipToBack.setOnFinished(event -> {
            cardView.setImage(new Image(getClass().getResourceAsStream("/cards/" + cardType + ".png")));
            flipToFront.play();
        });

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
    }
}
