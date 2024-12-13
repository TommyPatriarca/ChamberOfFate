
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
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

    /**
     * Funzione per mostrare la schermata di gioco
     */

    public void show(Stage primaryStage) {
        primaryStage.setMaximized(true);
        controller.startGame("Player 1");

        // Immagine di sfondo
        ImageView backgroundView = createBackground(primaryStage);

        root = new StackPane();
        BorderPane gameLayout = new BorderPane();

        VBox topBar = createCustomTopBar(primaryStage);
        gameLayout.setTop(topBar);

        VBox gameArea = createGameArea();
        gameLayout.setCenter(gameArea);

        HBox bottomControls = createBottomControls();
        gameLayout.setBottom(bottomControls);

        root.getChildren().addAll(backgroundView, gameLayout);

        // Aggiungi il menu
        createMenu();

        Scene scene = new Scene(root);
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
        Image backgroundImage = new Image(getClass().getResourceAsStream("/images/Table.jpg"));
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
     * Funzione per creare una top bar con stile custom
     * @return La top bar stilizzata
     */

    private VBox createCustomTopBar(Stage primaryStage) {
        VBox topBar = new VBox();
        topBar.setStyle("-fx-background-color: #333333;");
        topBar.setPadding(new Insets(10));

        HBox barContent = new HBox();
        barContent.setAlignment(Pos.CENTER);
        barContent.setSpacing(20);

        Label titleLabel = new Label("Chamber of Fate");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);

        player1HP = new Label("Player 1 HP: 5");
        player1HP.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        player1HP.setTextFill(Color.LIGHTGREEN);

        player2HP = new Label("Player 2 HP: 5");
        player2HP.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        player2HP.setTextFill(Color.RED);

        barContent.getChildren().addAll(titleLabel, player1HP, player2HP);
        topBar.getChildren().add(barContent);

        return topBar;
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
        playerHandDisplay.getChildren().clear();
        controller.getPlayer1().getPlayDeck().forEach(card -> {
            if (card != null) {
                playerHandDisplay.getChildren().add(createCardView(card));
            }
        });

        opponentHandDisplay.getChildren().clear();
        opponentHandDisplay.getChildren().add(createBackCardView());
    }

    /**
     * Funzione per risolvere il risultato del round
     */

    private void resolveRound(String message) {
        revealAllCards();

        int player1Score = controller.checkCards(controller.getPlayer1(), false);
        int player2Score = controller.checkCards(controller.getPlayer2(), false);

        if (message == null) {
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
            opponentHandDisplay.getChildren().add(createCardView(card));
        });

        playerHandDisplay.getChildren().clear();
        controller.getPlayer1().getPlayDeck().forEach(card -> {
            playerHandDisplay.getChildren().add(createCardView(card));
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
        initializePlayerHands();
        updatePlayerHP();

        opponentHandDisplay.getChildren().clear();
        boolean isFirstCard = true;

        for (CardObj card : controller.getPlayer2().getPlayDeck()) {
            if (isFirstCard) {
                opponentHandDisplay.getChildren().add(createCardView(card));
                isFirstCard = false;
            } else {
                opponentHandDisplay.getChildren().add(createBackCardView());
            }
        }
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

    private ImageView createCardView(CardObj card) {
        ImageView cardView = createBackCardView();
        String imagePath = card.getImagePath();

        playCardFlipAnimation(cardView, imagePath);

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

    private void createMenu() {
        // Creazione del menu
        VBox menu = new VBox(15);
        menu.setStyle("-fx-background-color: rgba(20, 20, 20, 0.95); -fx-border-color: gold; -fx-border-width: 3; -fx-padding: 20;");
        menu.setAlignment(Pos.CENTER);
        menu.setVisible(false);

        // Animazioni per apertura e chiusura
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

        // Rettangolo trasparente per catturare i clic fuori dal menu
        Rectangle clickCatcher = new Rectangle();
        clickCatcher.setFill(Color.TRANSPARENT);
        clickCatcher.setVisible(false);
        clickCatcher.setOnMouseClicked(event -> {
            if (menu.isVisible()) {
                fadeOut.setOnFinished(ev -> {
                    menu.setVisible(false);
                    clickCatcher.setVisible(false);
                });
                fadeOut.play();
            }
        });

        // Pulsante per aprire/chiudere il menu
        Button toggleMenuButton = createStyledButton("Menu", "#333333");
        toggleMenuButton.setStyle("-fx-background-color: rgba(40, 40, 40, 0.8); -fx-border-color: gold; -fx-border-width: 2; -fx-font-size: 16px; -fx-text-fill: gold;");
        toggleMenuButton.setOnAction(e -> {
            if (menu.isVisible()) {
                fadeOut.setOnFinished(ev -> {
                    menu.setVisible(false);
                    clickCatcher.setVisible(false);
                });
                fadeOut.play();
            } else {
                menu.setVisible(true);
                clickCatcher.setVisible(true);
                fadeIn.play();
            }
        });

        // Aggiusta il clickCatcher alla dimensione della scena
        //TODO ancora da sistemare il menu
        root.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                clickCatcher.widthProperty().bind(newScene.widthProperty());
                clickCatcher.heightProperty().bind(newScene.heightProperty());
            }
        });

        // Aggiunge il menu e il catcher al pannello
        root.getChildren().addAll(clickCatcher, menu);

        BorderPane.setAlignment(toggleMenuButton, Pos.TOP_RIGHT);
        ((BorderPane) root.getChildren().get(1)).setRight(toggleMenuButton);
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
}