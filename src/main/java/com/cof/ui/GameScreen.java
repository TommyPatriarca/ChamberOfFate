
package com.cof.ui;

import com.cof.managers.MusicManager;
import com.cof.managers.SoundManager;
import com.cof.utils.FontUtils;
import com.controller.Controller;
import com.controller.objects.CardObj;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
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

import java.util.Objects;

public class GameScreen {
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isPlayerTurn = true; // True: turno del giocatore, False: turno del bot
    private SoundManager soundManager = new SoundManager();
    private Controller controller;
    private HBox playerHandDisplay, opponentHandDisplay;
    private Label currentPlayerLabel, player1HP, player2HP,player1ScoreLabel, player2ScoreLabel;
    private Button drawCardButton, passTurnButton;
    private StackPane root;
    private int currentRound = 1;

    public GameScreen(Controller controller) {
        this.controller = controller;
    }

    /**
     * Funzione per mostrare la schermata di gioco
     */

    private Label resultOverlay; // Overlay per i risultati

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

        // Vite e punteggio del Player 1
        VBox player1Box = new VBox(5);
        player1HP = new Label("Player 1 HP: 5");
        styleHealthLabel(player1HP, Color.LIGHTGREEN);
        player1ScoreLabel = new Label("Score: 0");
        styleScoreLabel(player1ScoreLabel);
        player1Box.getChildren().addAll(player1HP, player1ScoreLabel);
        BorderPane.setAlignment(player1Box, Pos.TOP_LEFT);
        BorderPane.setMargin(player1Box, new Insets(10));

// Vite e punteggio del Player 2
        VBox player2Box = new VBox(5);
        player2HP = new Label("Player 2 HP: 5");
        styleHealthLabel(player2HP, Color.RED);
        player2ScoreLabel = new Label("Score: 0");
        styleScoreLabel(player2ScoreLabel);
        player2Box.getChildren().addAll(player2HP, player2ScoreLabel);
        BorderPane.setAlignment(player2Box, Pos.TOP_RIGHT);
        BorderPane.setMargin(player2Box, new Insets(10));

        healthPane.setLeft(player1Box);
        healthPane.setRight(player2Box);


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
            updateScores();
            if (isPlayerTurn) {
                controller.hitCard(true); // Il giocatore pesca una carta
                updateGameDisplay();

                // Controlla se il giocatore ha perso
                if (controller.checkCards(controller.getPlayer1(), false) > 21) {
                    resolveRound("Hai sballato! Vince l'avversario.");
                } else {
                    endPlayerTurn(false); // Passa il turno al bot, senza che il giocatore stia
                }
            }
        });

        passTurnButton.setOnAction(e -> {
            if (isPlayerTurn) {
                endPlayerTurn(true); // Il giocatore decide di stare
            }
        });


        controls.getChildren().addAll(currentPlayerLabel, drawCardButton, passTurnButton);
        return controls;
    }

    /**
     * Funzione per fare giocare il turno al bot
     */
    private void botTurn(boolean completeTurn) {
        setPlayerControlsEnabled(false); // Disabilita i controlli del giocatore
        currentPlayerLabel.setText("Bot's Turn");

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5)); // Ritardo per simulare la giocata del bot
        pause.setOnFinished(e -> {
            boolean botDecidesToHit = controller.checkCards(controller.getPlayer2(), false) < 17;

            if (botDecidesToHit) {
                controller.hitCard(false); // Il bot pesca una carta
                updateGameDisplay();
                updateScores();

                // Se il bot ha sballato
                if (controller.checkCards(controller.getPlayer2(), false) > 21) {
                    checkRoundCompletion(); // Controlla se il round è finito
                } else if (completeTurn) {
                    botTurn(true); // Continua a giocare solo se il giocatore ha premuto "Stai"
                } else {
                    startPlayerTurn(); // Torna al giocatore
                }
            } else {
                // Il bot decide di stare
                if (completeTurn) {
                    checkRoundCompletion(); // Verifica se il round è terminato
                } else {
                    startPlayerTurn(); // Torna al giocatore
                }
            }
        });

        pause.play(); // Avvia il ritardo
    }



    private void endRoundIfNecessary() {
        int player1Score = controller.checkCards(controller.getPlayer1(), false);
        int player2Score = controller.checkCards(controller.getPlayer2(), false);

        boolean player1Finished = player1Score > 21 || !isPlayerTurn; // Il giocatore ha sballato o deciso di stare
        boolean player2Finished = player2Score > 21 || controller.checkCards(controller.getPlayer2(), false) >= 17;

        if (player1Finished && player2Finished) {
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

        if (controller.checkCards(controller.getPlayer1(), false) > 21) {
            // Il giocatore ha sballato
            currentPlayerLabel.setText("Bot's Turn");
            botTurn(true); // Il bot completa tutte le sue giocate
        } else if (playerStands) {
            // Il giocatore ha deciso di stare
            currentPlayerLabel.setText("Bot's Turn");
            botTurn(true); // Il bot completa tutte le sue giocate
        } else {
            // Il giocatore pesca una carta
            currentPlayerLabel.setText("Bot's Turn");
            botTurn(false); // Il bot esegue solo una mossa
        }
    }

    /**
     * Funzione per controllare se è finito un round
     */
    private void checkRoundCompletion() {
        int player1Score = controller.checkCards(controller.getPlayer1(), false);
        int player2Score = controller.checkCards(controller.getPlayer2(), false);

        boolean player1Finished = player1Score > 21 || !isPlayerTurn; // Il giocatore ha sballato o deciso di stare
        boolean player2Finished = player2Score > 21 || controller.checkCards(controller.getPlayer2(), false) >= 17;

        if (player1Finished && player2Finished) {
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
        updateScores();
    }



    /**
     * Funzione per risolvere il  round
     */

    private void resolveRound(String message) {
        revealAllCards();

        int player1Score = controller.checkCards(controller.getPlayer1(), false);
        int player2Score = controller.checkCards(controller.getPlayer2(), false);

        if (message == null) {
            if (player1Score > 21 && player2Score > 21) {
                message = "Entrambi avete sballato!";
            } else if (player1Score > 21) {
                message = "Hai sballato! Vince il bot.";
                controller.getPlayer1().shoot(6); // Perdi una vita
            } else if (player2Score > 21) {
                message = "Il bot ha sballato! Hai vinto.";
                controller.getPlayer2().shoot(6); // Il bot perde una vita
            } else if (player1Score > player2Score) {
                message = "Hai vinto!";
                controller.getPlayer2().shoot(6);
            } else if (player1Score < player2Score) {
                message = "Hai perso!";
                controller.getPlayer1().shoot(6);
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
        controller.getPlayer2().getPlayDeck().forEach(card -> {
            opponentHandDisplay.getChildren().add(createCardView(card, true));
        });

        playerHandDisplay.getChildren().clear();
        controller.getPlayer1().getPlayDeck().forEach(card -> {
            playerHandDisplay.getChildren().add(createCardView(card, true));
        });

        updatePlayerHP();
    }

    /**
     * Funzione per mostrare il risultato della partita
     */

    private void endGame(Stage stage) {
        if(controller.getPlayer1().getHP() == 0){
            EndScreen endScreen = new EndScreen();
            endScreen.show(stage, false);
        } else if (controller.getPlayer2().getHP() == 0) {
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
        updateScores();
        updatePlayerHP();
    }




    /**
     * Funzione per aggiornare la vita dei player
     */

    private void updatePlayerHP() {
        player1HP.setText("Player 1 HP: " + controller.getPlayer1().getHP());
        player2HP.setText("Player 2 HP: " + controller.getPlayer2().getHP());

        // Controlla se il gioco deve terminare
        if (controller.getPlayer1().getHP() == 0 || controller.getPlayer2().getHP() == 0) {
            endGame((Stage) player1HP.getScene().getWindow());
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
                "-fx-background-color: rgba(40, 40, 40, 0.8);" +
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
        volumeSlider.setPrefWidth(200); // Dimensione più compatta
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

        root.getChildren().add(overlayPane); // Aggiungi il popup al layout principale
    }

    /**
     * Funzione per mostrare la schermata per arrendersi
     */

    private void surrender() {
        showCustomPopup("Surrender", "Vuoi arrenderti? Questa azione terminerà la partita.", () -> System.exit(0));
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
            startNewRound(); // Avvia un nuovo round
        });
        fadeOut.play();
    }

    /**
     * Funzione per aggiornare il punteggio ovvero la somma dei valori delle carte
     */
    private void updateScores() {
        int player1Score = controller.checkCards(controller.getPlayer1(), false);
        int player2Score = controller.checkCards(controller.getPlayer2(), false);

        player1ScoreLabel.setText("Score: " + player1Score);
        player2ScoreLabel.setText("Score: " + "X"); //
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
                "-fx-background-color: rgba(42, 42, 42, 0.9);" + //
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



}