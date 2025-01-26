
package com.cof.ui;

import com.cof.managers.MusicManager;
import com.cof.managers.SoundManager;
import com.cof.okhttp.Okhttp;
import com.cof.utils.FontUtils;
import com.controller.Controller;
import com.controller.ControllerOnline;
import com.controller.managers.cardManager.Deck;
import com.controller.objects.CardObj;
import com.controller.objects.PlayerObj;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Objects;

public class OnlineGameScreen2 {
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isPlayerTurn = true, isGamePaused = false; // True: turno del giocatore, False: turno del bot
    private SoundManager soundManager = new SoundManager();
    private ControllerOnline controller;
    private HBox playerHandDisplay, opponentHandDisplay;
    private Label currentPlayerLabel, giocatore1HP, giocatore2HP,giocatore1ScoreLabel, giocatore2ScoreLabel;
    private Button drawCardButton, passTurnButton;
    private StackPane root;
    private int currentRound = 1;
    private Stage stagesaved;
    private String playerKey;
    private boolean isFirstPlayer;
    private Okhttp okhttp = new Okhttp();
    private Deck deck;
    private PlayerObj giocatore1, giocatore2;


    public OnlineGameScreen2(String playerKey, boolean isFirstPlayer) {
        this.playerKey = playerKey;
        this.isFirstPlayer = isFirstPlayer;

        if (isFirstPlayer) {
            deck = new Deck(); // Crea un nuovo mazzo solo se è il primo giocatore
            okhttp.setDeck(deck.getDeckArrAsString()); // Carica il mazzo sul server
        } else {
            loadDeckFromServer(); // Il secondo giocatore scarica il mazzo dal server
        }

        giocatore1 = new PlayerObj("Player 1");
        giocatore2 = new PlayerObj("Player 2");
    }


    private void loadDeckFromServer() {
        ArrayList<String> deckCards = okhttp.getDeck();
        deck = new Deck();
        for (String card : deckCards) {
            deck.addCard(new CardObj(card));
        }
    }


    private int checkCards(PlayerObj player) {
        int score = 0;
        int aces = 0;
        for (CardObj card : player.getPlayDeck()) {
            String value = card.getTipo().substring(0, card.getTipo().length() - 1);
            switch (value) {
                case "J", "Q", "K" -> score += 10;
                case "A" -> {
                    score += 11;
                    aces++;
                }
                default -> score += Integer.parseInt(value);
            }
        }
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }
        return score;
    }


    /**
     * Funzione per mostrare la schermata di gioco
     */

    private Label resultOverlay; // Overlay per i risultati

    public void show(Stage primaryStage) {
        stagesaved = primaryStage;
        primaryStage.setMaximized(true);


        // Immagine di sfondo
        ImageView backgroundView = createBackground(primaryStage);

        root = new StackPane();

        // Barra del titolo personalizzata
        HBox titleBar = createCustomTitleBar(primaryStage);

        // Posizionamento delle vite dei giocatori
        BorderPane healthPane = new BorderPane();
        healthPane.setPadding(new Insets(10));

        // Vite e punteggio del Player 1
        VBox giocatore1Box = new VBox(5);
        giocatore1HP = new Label("Player 1 HP: 5");
        styleHealthLabel(giocatore1HP, Color.LIGHTGREEN);
        giocatore1ScoreLabel = new Label("Score: 0");
        styleScoreLabel(giocatore1ScoreLabel);
        giocatore1Box.getChildren().addAll(giocatore1HP, giocatore1ScoreLabel);
        BorderPane.setAlignment(giocatore1Box, Pos.TOP_LEFT);
        BorderPane.setMargin(giocatore1Box, new Insets(10));

// Vite e punteggio del Player 2
        VBox giocatore2Box = new VBox(5);
        giocatore2HP = new Label("Player 2 HP: 5");
        styleHealthLabel(giocatore2HP, Color.RED);
        giocatore2ScoreLabel = new Label("Score: 0");
        styleScoreLabel(giocatore2ScoreLabel);
        giocatore2Box.getChildren().addAll(giocatore2HP, giocatore2ScoreLabel);
        BorderPane.setAlignment(giocatore2Box, Pos.TOP_RIGHT);
        BorderPane.setMargin(giocatore2Box, new Insets(10));

        healthPane.setLeft(giocatore1Box);
        healthPane.setRight(giocatore2Box);


        VBox gameArea = createGameArea();
        gameArea.setAlignment(Pos.CENTER);

        HBox bottomControls = createBottomControls();

        // Crea il layout principale
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(healthPane);
        mainLayout.setCenter(gameArea);
        mainLayout.setBottom(bottomControls);

        VBox fullLayout = new VBox();
        fullLayout.getChildren().addAll(titleBar, mainLayout);
        VBox.setVgrow(mainLayout, Priority.ALWAYS);

        // Overlay dei risultati
        resultOverlay = createResultOverlay();
        resultOverlay.setVisible(false);

        // Aggiungi tutto al root
        root.getChildren().addAll(backgroundView, fullLayout, resultOverlay);

        // Scene e gestione ESC
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chamber of Fate - Round " + currentRound);

        // Creazione del menu e aggiunta al layout
        VBox menu = createMenu();
        root.getChildren().add(menu); // Aggiunto il menu al layout principale

        // Listener se premo esc
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (menu.isVisible()) {
                    menu.setVisible(false); // Nascondi il menu se è visibile
                } else {
                    menu.setVisible(true); // Mostra il menu se è nascosto
                }
            }
        });

        Timeline turnChecker = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            String action = okhttp.getAzione(playerKey);

            if (action != null && action.equals("draw")) {
                isPlayerTurn = true;
                currentPlayerLabel.setText("Il tuo turno");
                setPlayerControlsEnabled(true);
            } else {
                isPlayerTurn = false;
                currentPlayerLabel.setText("Turno avversario");
                setPlayerControlsEnabled(false);
            }
        }));
        turnChecker.setCycleCount(Timeline.INDEFINITE);
        turnChecker.play();


        initializePlayerHands();
        menu.setVisible(false);
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
        updateScores();
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

        drawCardButton = createStyledButton("PESCA", "#4CAF50");
        passTurnButton = createStyledButton("STA", "#f44336");

        drawCardButton.setOnAction(e -> {
            if (isPlayerTurn) {
                // Pesca la carta dal mazzo
                CardObj drawnCard = deck.hitCard();
                giocatore1.addCard(drawnCard);

                okhttp.addCarta(drawnCard.getTipo(), playerKey); // Salva la carta sul server

                updateGameDisplay();

                // Controlla se il giocatore ha superato 21 punti
                if (checkCards(giocatore1) > 21) {
                    System.out.println("Hai sballato! Vince l'avversario.");
                    giocatore1.shoot(6); // Perde una vita
                    okhttp.setAzioneStand(playerKey); // Comunica al server che il turno è finito
                } else {
                    endPlayerTurn(false); // Passa il turno all'avversario
                }
            }
        });




        passTurnButton.setOnAction(e -> {
            if (isPlayerTurn) {
                okhttp.setAzioneStand(playerKey); // Comunica al server che il giocatore ha passato
                endPlayerTurn(true); // Conclude il turno del giocatore
            }
        });


        controls.getChildren().addAll(currentPlayerLabel, drawCardButton, passTurnButton);
        return controls;
    }

    /**
     * Funzione per fare giocare il turno al bot
     */
    private void opponentTurn(boolean completeTurn) {
        setPlayerControlsEnabled(false); // Disabilita i controlli del giocatore
        currentPlayerLabel.setText("Turno dell'avversario");

        //TODO logica per la giocata dell altro
        
        checkRoundCompletion();
        startPlayerTurn();
    }



    private void endRoundIfNecessary() {
        int giocatore1Score = controller.checkCards(controller.getgiocatore1(), false);
        int giocatore2Score = controller.checkCards(controller.getgiocatore2(), false);

        boolean giocatore1Finished = giocatore1Score > 21 || !isPlayerTurn; // Il giocatore ha sballato o deciso di stare
        boolean giocatore2Finished = giocatore2Score > 21 || controller.checkCards(controller.getgiocatore2(), false) >= 17;

        if (giocatore1Finished && giocatore2Finished) {
            resolveRound(null); // Termina il round
        }
    }

    /**
     * Funzione per iniziare il turno del player
     */
    private void startPlayerTurn() {
        isPlayerTurn = true;
        currentPlayerLabel.setText("Your Turn");
        setPlayerControlsEnabled(true); // Abilita i controlli del giocatore
        updateScores();
    }

    /**
     * Funzione per finire il turno del player
     */
    private void endPlayerTurn(boolean playerStands) {
        isPlayerTurn = false;
        currentPlayerLabel.setText("Turno avversario");

        if (playerStands) {
            okhttp.setAzioneStand(playerKey);
        } else {
            okhttp.setAzioneDraw("giocatore2");
        }

        setPlayerControlsEnabled(false);
    }


    /**
     * Funzione per controllare se è finito un round
     */
    private void checkRoundCompletion() {
        int giocatore1Score = controller.checkCards(controller.getgiocatore1(), false);
        int giocatore2Score = controller.checkCards(controller.getgiocatore2(), false);

        boolean giocatore1Finished = giocatore1Score > 21 || !isPlayerTurn; // Il giocatore ha sballato o deciso di stare
        boolean giocatore2Finished = giocatore2Score > 21 || controller.checkCards(controller.getgiocatore2(), false) >= 17;

        if (giocatore1Finished && giocatore2Finished) {
            resolveRound(null); // Termina il round
        }
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
        int playerDeckSize = controller.getgiocatore1().getPlayDeck().size();

        for (int i = 0; i < playerDeckSize; i++) {
            boolean animate = (i == playerDeckSize - 1) || currentRound == 1; // Anima l'ultima carta o tutte nel primo round
            playerHandDisplay.getChildren().add(createCardView(controller.getgiocatore1().getPlayDeck().get(i), animate));
        }

        // Aggiorna la mano dell'avversario
        opponentHandDisplay.getChildren().clear();
        int opponentDeckSize = controller.getgiocatore2().getPlayDeck().size();

        for (int i = 0; i < opponentDeckSize; i++) {
            if (i == 0) {
                // Mostra la prima carta dell'avversario
                boolean animate = currentRound == 1; // Anima solo nel primo round
                opponentHandDisplay.getChildren().add(createCardView(controller.getgiocatore2().getPlayDeck().get(i), animate));
            } else {
                // Mostra il retro per tutte le altre carte
                opponentHandDisplay.getChildren().add(createBackCardView());
            }
        }
        updateScores();
    }



    /**
     * Funzione per risolvere il  round
     */

    private void resolveRound(String message) {
        revealAllCards();

        int giocatore1Score = controller.checkCards(controller.getgiocatore1(), false);
        int giocatore2Score = controller.checkCards(controller.getgiocatore2(), false);

        if(message == null) {
            if (giocatore1Score > 21 && giocatore2Score > 21) {
                message = "Entrambi avete sballato!";
            } else if (giocatore1Score > 21) {
                message = "Hai sballato! Vince il bot.";
                controller.getgiocatore1().shoot(6);
            } else if (giocatore2Score > 21) {
                message = "Il bot ha sballato! Hai vinto.";
                controller.getgiocatore2().shoot(6); // Il bot perde una vita
            } else if (giocatore1Score > giocatore2Score) {
                message = "Hai vinto!";
                controller.getgiocatore2().shoot(6);
            } else if (giocatore1Score < giocatore2Score) {
                message = "Hai perso!";
                controller.getgiocatore1().shoot(6);
            } else {
                message = "Pareggio!";
            }
        }

        updatePlayerHP();
        showResultOverlay(message); // Mostra l'overlay e prepara il nuovo round
    }



    /**
     * Funzione per far girare tutte le carte sul tavolo
     */

    private void revealAllCards() {
        opponentHandDisplay.getChildren().clear();
        controller.getgiocatore2().getPlayDeck().forEach(card -> {
            opponentHandDisplay.getChildren().add(createCardView(card, true));
        });

        playerHandDisplay.getChildren().clear();
        controller.getgiocatore1().getPlayDeck().forEach(card -> {
            playerHandDisplay.getChildren().add(createCardView(card, true));
        });

        updatePlayerHP();
    }

    /**
     * Funzione per mostrare il risultato della partita
     */

    private void endGame(Stage stage) {
        if(controller.getgiocatore1().getHP() == 0){
            EndScreen endScreen = new EndScreen();
            endScreen.show(stage, false);
        } else if (controller.getgiocatore2().getHP() == 0) {
            EndScreen endScreen = new EndScreen();
            endScreen.show(stage, true);
        }
    }


    /**
     * Funzione per aggiornare la schermata di gioco
     */

    private void updateGameDisplay() {

        // Aggiorna la mano del giocatore
        playerHandDisplay.getChildren().clear();
        int playerDeckSize = controller.getgiocatore1().getPlayDeck().size();

        for (int i = 0; i < playerDeckSize; i++) {
            boolean animate = (i == playerDeckSize - 1); // Anima solo l'ultima carta
            playerHandDisplay.getChildren().add(createCardView(controller.getgiocatore1().getPlayDeck().get(i), animate));
        }

        // Aggiorna la mano dell'avversario
        opponentHandDisplay.getChildren().clear();
        int opponentDeckSize = controller.getgiocatore2().getPlayDeck().size();

        for (int i = 0; i < opponentDeckSize; i++) {
            if (i == 0) {
                // Mostra la prima carta dell'avversario
                opponentHandDisplay.getChildren().add(createCardView(controller.getgiocatore2().getPlayDeck().get(i), false));
            } else {
                // Mostra il retro per tutte le altre carte
                opponentHandDisplay.getChildren().add(createBackCardView());
            }
        }
        updateScores();
        updatePlayerHP();
    }




    /**
     * Funzione per aggiornare la vita dei player
     */

    private void updatePlayerHP() {
        giocatore1HP.setText("Player 1 HP: " + controller.getgiocatore1().getHP());
        giocatore2HP.setText("Player 2 HP: " + controller.getgiocatore2().getHP());

        // Controlla se il gioco deve terminare
        if (controller.getgiocatore1().getHP() == 0 || controller.getgiocatore2().getHP() == 0) {
            endGame((Stage) giocatore1HP.getScene().getWindow());
        }
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

        if (animate && !card.getAlreadyFlipped()) {
            playCardFlipAnimation(cardView, imagePath);
            card.setAlreadyFlipped(true);
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
        VBox menu = new VBox(20);
        menu.setStyle(
                "-fx-background-color: rgba(42, 42, 42, 0.6);" +
                        "-fx-border-color: #666666;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 20;" +
                        "-fx-effect: dropshadow(gaussian, #000000, 15, 0.7, 0, 2);"
        );
        menu.setAlignment(Pos.CENTER);

        // Transizioni per il menu
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), menu);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), menu);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        // Pulsanti del menu
        Button rulesButton = createMenuButton("Rules", this::showRules);
        Button creditsButton = createMenuButton("Credits", this::showCredits);
        Button volumeButton = createMenuButton("Volume", this::showVolumeControl);
        Button surrenderButton = createMenuButton("Surrender", this::surrender);
        Button exitButton = createMenuButton("Exit", this::closeGame);

        menu.getChildren().addAll(rulesButton, creditsButton, volumeButton, surrenderButton, exitButton);

        // Pulsante toggle per mostrare/nascondere il menu
        Button toggleMenuButton = createStyledButton("Menu", "#333333");
        toggleMenuButton.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.4);" +
                        "-fx-border-color: #666666;" +
                        "-fx-border-width: 2;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: #DDDDDD;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );
        toggleMenuButton.setOnAction(e -> {
            if (menu.isVisible()) {
                fadeOut.setOnFinished(ev -> menu.setVisible(false));
                fadeOut.play();
            } else {
                menu.setVisible(true);
                fadeIn.play();
            }
        });

        return menu;
    }
    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #2A2A2A;" +
                        "-fx-text-fill: #DDDDDD;" +
                        "-fx-padding: 12px;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #666666;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, #000000, 10, 0.8, 0, 2);" +
                        "-fx-cursor: hand;"
        );
        button.setFont(FontUtils.PIXEL_HORROR);
        button.setMinWidth(200);
        button.setMinHeight(60);

        button.setOnAction(e -> action.run()); // Collegamento alla funzione passata

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #555555;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-padding: 12px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #888888;" +
                        "-fx-border-width: 3px;" +
                        "-fx-border-radius: 6;" +
                        "-fx-effect: dropshadow(gaussian, #444444, 20, 0.8, 0, 0);"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #2A2A2A;" +
                        "-fx-text-fill: #DDDDDD;" +
                        "-fx-padding: 12px;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #666666;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, #000000, 10, 0.8, 0, 2);" +
                        "-fx-cursor: hand;"
        ));
        button.setOnMousePressed(e -> button.setStyle(
                "-fx-background-color: #222222;" +
                        "-fx-text-fill: #aaaaaa;" +
                        "-fx-padding: 12px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #444444;" +
                        "-fx-border-width: 3px;" +
                        "-fx-border-radius: 6;" +
                        "-fx-effect: dropshadow(gaussian, #111111, 30, 1.0, 0, 0);"));

        return button;
    }



    /**
     * Funzione per mostrare il regolamento del gioco
     */

    private void showRules() {
        showCustomPopup("Rules", "Devo mettere le regole", () -> {});
    }


    /**
     * Funzione per mostrare i crediti del gioco
     */

    private void showCredits() {
        showCustomPopup("Credits", "Creato da Tommaso Patriarca, Alessandro Anastasio, Michele Comalli e Marco Barlascini", () -> {});
    }


    /**
     * Funzione per mostrare i controlli del volume
     */

    private void showVolumeControl() {
        StackPane overlayPane = new StackPane();
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        VBox popup = new VBox(20);
        popup.setAlignment(Pos.CENTER);
        popup.setMaxWidth(350); // Limita la larghezza massima del popup
        popup.setMaxHeight(550);
        popup.setStyle(
                "-fx-background-color: rgba(42, 42, 42, 0.9);" +
                        "-fx-border-color: #666666;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 20;" +
                        "-fx-effect: dropshadow(gaussian, #000000, 15, 0.7, 0, 2);"
        );

        Label titleLabel = new Label("Volume Control");
        titleLabel.setStyle(
                "-fx-text-fill: gold;" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Arial';"
        );

        Slider volumeSlider = new Slider(0, 1, MusicManager.getVolume());
        volumeSlider.setPrefWidth(200); // NON VAAAA
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setMajorTickUnit(0.25);
        volumeSlider.setMinorTickCount(4);
        volumeSlider.setBlockIncrement(0.05);
        volumeSlider.setStyle(
                "-fx-control-inner-background: #222;" +
                        "-fx-accent: gold;" +
                        "-fx-track-color: #555;" +
                        "-fx-thumb-color: #FFD700;"
        );

        Label volumeValueLabel = new Label(String.format("Volume: %.0f%%", MusicManager.getVolume() * 100));
        volumeValueLabel.setStyle("-fx-text-fill: #DDDDDD; -fx-font-size: 14px;");

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            MusicManager.setVolume(newValue.doubleValue());
            volumeValueLabel.setText(String.format("Volume: %.0f%%", newValue.doubleValue() * 100));
        });

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);

        Button closeButton = new Button("Close");
        stylePopupButton(closeButton, "#f44336", "#E57373");
        closeButton.setOnAction(e -> overlayPane.setVisible(false));

        buttonContainer.getChildren().add(closeButton);

        popup.getChildren().addAll(titleLabel, volumeSlider, volumeValueLabel, buttonContainer);
        overlayPane.getChildren().add(popup);
        StackPane.setAlignment(overlayPane, Pos.CENTER);

        root.getChildren().add(overlayPane);
    }


    /**
     * Funzione per mostrare la schermata per arrendersi
     */

    private void surrender() {
        showCustomPopup("Surrender", "Vuoi arrenderti? Questa azione terminerà la partita.", () -> {
            ModeScreen modeScreen = new ModeScreen();
            modeScreen.show(stagesaved);
        });
    }
    /**
     * Funzione per chiudere il gioco
     */

    private void closeGame() {
        showCustomPopup("Surrender", "Vuoi chiudere il gioco? Perderai la partita", () -> System.exit(0));
    }


    /**
     * Funzione per stilizzare il label contenente le vite dei player
     */
    private void styleHealthLabel(Label label, Color textColor) {
        label.setFont(FontUtils.PIXEL_HORROR); // Applica il font personalizzato
        label.setTextFill(textColor); // Imposta il colore del testo
        label.setStyle(
                "-fx-background-color: #2A2A2A;" +
                        "-fx-font-size: 20px;"+
                        "-fx-text-fill: #DDDDDD;" +
                        "-fx-padding: 8px;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #666666;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, #000000, 10, 0.8, 0, 2);" // Ombra
        );
    }
    /**
     * Funzione per creare il box della barra del titolo
     * @return Il box contenente la barra del titolo
     */
    private HBox createCustomTitleBar(Stage stage) {
        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setStyle("-fx-background-color: linear-gradient(to right, #1E1E1E, #333333); "
                + "-fx-padding: 4; -fx-border-color: #444; -fx-border-width: 0 0 1 0; "
                + "-fx-effect: dropshadow(gaussian, #000000, 10, 0.7, 0, 1);");
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
        ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/Icon1.png"))));
        icon.setFitHeight(20);
        icon.setFitWidth(20);
        titleBar.getChildren().add(0, icon);

        return titleBar;
    }

    /**
     * Funzione per abilitare i tasti per pescaro o stare
     */
    private void setPlayerControlsEnabled(boolean enabled) {
        drawCardButton.setDisable(!enabled);
        passTurnButton.setDisable(!enabled);
    }

    /**
     * Funzione per creare il label per gli overlay
     * @return Il label
     */
    private Label createResultOverlay() {
        Label overlay = new Label();
        overlay.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.8);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 36px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 20;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 11;" +
                        "-fx-alignment: center;"
        );
        overlay.setAlignment(Pos.CENTER);
        overlay.setMaxWidth(400);
        overlay.setWrapText(true); //per mettere il testo in colonne
        StackPane.setAlignment(overlay, Pos.CENTER);
        return overlay;
    }

    /**
     * Funzione per mostrare i risultati del round
     */
    private void showResultOverlay(String message) {
        resultOverlay.setText(message);
        resultOverlay.setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), resultOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setOnFinished(event -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(3)); // Mostra il messaggio per 3 secondi
            pause.setOnFinished(e -> hideResultOverlay());
            pause.play();
        });
        fadeIn.play();
    }

    /**
     * Funzione per nascondere i risultati del round
     */
    private void hideResultOverlay() {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), resultOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> {
            resultOverlay.setVisible(false);
            shootAnimation(6);//TODO da cambiare il 6 con un numero variabile
        });
        fadeOut.play();
    }

    /**
     * Funzione per aggiornare il punteggio ovvero la somma dei valori delle carte
     */
    private void updateScores() {
        int giocatore1Score = controller.checkCards(controller.getgiocatore1(), false);
        int giocatore2Score = controller.checkCards(controller.getgiocatore2(), false);

        giocatore1ScoreLabel.setText("Score: " + giocatore1Score);
        giocatore2ScoreLabel.setText("Score: " + "X"); //
    }

    /**
     * Funzione per stilizzare il label contenente i punteggi
     */
    private void styleScoreLabel(Label label) {
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.GOLD);
        label.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.6);" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 5 10 5 10;" +
                        "-fx-effect: dropshadow(gaussian, #000000, 10, 0.5, 0, 0);"
        );
    }

    /**
     * Funzione per iniziare un nuovo round nel gioco
     */
    private void startNewRound() {
        if (isGamePaused) return;

        controller.turn();
        updateGameDisplay();
        startPlayerTurn();
    }


    /**
     * Funzione per mostrare un popup
     */
    private void showCustomPopup(String title, String message, Runnable onConfirm) {
        StackPane overlayPane = new StackPane();
        overlayPane.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.5);" +
                        "-fx-padding: 20;"
        );

        VBox popup = new VBox(20);
        popup.setAlignment(Pos.CENTER);
        popup.setStyle(
                "-fx-background-color: rgba(42, 42, 42, 0.9);" +
                        "-fx-border-color: #666666;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, #000000, 15, 0.7, 0, 2);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-text-fill: gold;" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Arial';"
        );

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setStyle(
                "-fx-text-fill: #DDDDDD;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-family: 'Arial';"
        );

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);

        Button confirmButton = new Button("Confirm");
        stylePopupButton(confirmButton, "#4CAF50", "#66BB6A");
        confirmButton.setOnAction(e -> {
            onConfirm.run();
            overlayPane.setVisible(false);
        });

        Button cancelButton = new Button("Cancel");
        stylePopupButton(cancelButton, "#f44336", "#E57373");
        cancelButton.setOnAction(e -> overlayPane.setVisible(false));

        buttonContainer.getChildren().addAll(confirmButton, cancelButton);

        popup.setMaxWidth(550);
        popup.setMaxHeight(350);

        popup.getChildren().addAll(titleLabel, messageLabel, buttonContainer);

        overlayPane.getChildren().add(popup);
        StackPane.setAlignment(overlayPane, Pos.CENTER);

        root.getChildren().add(overlayPane); // Aggiungi il popup al layout principale
    }

    /**
     * Funzione per stilizzare i bottoni dei popup
     */
    private void stylePopupButton(Button button, String baseColor, String hoverColor) {
        button.setStyle(
                "-fx-background-color: " + baseColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-color: #444444;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, #000000, 5, 0.7, 0, 1);"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + hoverColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-color: #444444;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, #000000, 5, 0.7, 0, 1);"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + baseColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-color: #444444;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, #000000, 5, 0.7, 0, 1);"
        ));
    }
    private void shootAnimation(int bullets) {
        // Metti in pausa la logica del round
        setGamePaused(true);

        // Creazione di un overlay grigio trasparente
        Rectangle overlay = new Rectangle(400, 400, Color.rgb(0, 0, 0, 0.00)); //TODO controllare il colore

        // Creazione dell'immagine del revolver
        ImageView revolver = new ImageView(new Image(getClass().getResourceAsStream("/images/revolver.png")));
        revolver.setFitWidth(150);
        revolver.setFitHeight(150);

        // Gruppo per i proiettili
        Group bulletGroup = new Group();

        // StackPane per centrare tutto
        StackPane animationPane = new StackPane();
        animationPane.getChildren().addAll(overlay, bulletGroup, revolver);
        root.getChildren().add(animationPane);
        StackPane.setAlignment(animationPane, Pos.CENTER);

        // Centrare il gruppo dei proiettili rispetto al caricatore
        double revolverCenterX = 75; // Mezzo della larghezza del revolver
        double revolverCenterY = 75; // Mezzo dell'altezza del revolver

        // Creazione dei cerchi per i proiettili
        double radius = 40; // Raggio dei proiettili
        for (int i = 0; i < bullets; i++) {
            Circle bullet = new Circle(5, Color.GOLD); // Proiettile di dimensione 5 e colore oro
            double angle = 360.0 / bullets * i; // Angolo per posizionare i proiettili

            // Posizionamento rispetto al centro del gruppo dei proiettili
            double bulletX = revolverCenterX + radius * Math.cos(Math.toRadians(angle));
            double bulletY = revolverCenterY + radius * Math.sin(Math.toRadians(angle));
            bullet.setTranslateX(bulletX - revolverCenterX); // Offset relativo al centro del revolver
            bullet.setTranslateY(bulletY - revolverCenterY); // Offset relativo al centro del revolver

            bulletGroup.getChildren().add(bullet);
        }

        // Animazione di caricamento dei proiettili
        SequentialTransition loadBulletsAnimation = new SequentialTransition();
        for (Node bullet : bulletGroup.getChildren()) {
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), bullet);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            loadBulletsAnimation.getChildren().add(fadeIn);
        }

        // Una volta completato il caricamento, esegui la rotazione
        loadBulletsAnimation.setOnFinished(event -> {
            // Riproduci il suono del revolver spin
            SoundManager.revolverSpin();

            // Animazione di rotazione del gruppo (revolver + proiettili)
            RotateTransition rotateAnimation = new RotateTransition(Duration.seconds(2), bulletGroup);
            rotateAnimation.setByAngle(360);

            RotateTransition rotateRevolver = new RotateTransition(Duration.seconds(2), revolver);
            rotateRevolver.setByAngle(360);

            ParallelTransition rotation = new ParallelTransition(rotateAnimation, rotateRevolver);
            rotation.setOnFinished(e -> {
                SoundManager.ShotgunSound();
                // Rimuovi tutto al termine
                root.getChildren().remove(animationPane);
                // Riprendi la logica del gioco e distribuisci le carte
                setGamePaused(false);
                startNewRound(); // Distribuisci le carte per il nuovo round
            });
            rotation.play();
        });

        loadBulletsAnimation.play();
        startNewRound(); // Avvia un nuovo round
    }



    private void setGamePaused(boolean paused) {
        isGamePaused = paused;

        // Disabilita o abilita i pulsanti del giocatore
        drawCardButton.setDisable(paused);
        passTurnButton.setDisable(paused);

        // Altre logiche di pausa (es. bloccare eventi del bot o altri aggiornamenti)
    }


}