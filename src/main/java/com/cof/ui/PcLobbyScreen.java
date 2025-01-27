package com.cof.ui;

import com.cof.okhttp.Okhttp;
import com.cof.utils.FontUtils;
import com.controller.Controller;
import com.controller.ControllerOnline;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.prefs.Preferences;

/**
 * La scermata del gioco con le lobby
 */
public class PcLobbyScreen {

    private double xOffset = 0;
    private double yOffset = 0;

    private Timeline refreshLobbiesTimeline;

    private final List<String> lobbies = new ArrayList<>();
    private ListView<String> lobbyListView;
    private StackPane overlayPane;

    // Preferences to store selected lobby
    private final Preferences preferences = Preferences.userNodeForPackage(PcLobbyScreen.class);

    /**
     * Configura la finestra principale
     * @param primaryStage la finestra  principale
     */
    public void show(Stage primaryStage) {

        //Barra superiore
        HBox titleBar = createCustomTitleBar(primaryStage);

        // Background image
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/LobbyBackground.jpg")));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(false);
        backgroundView.setFitWidth(primaryStage.getWidth());
        backgroundView.setFitHeight(primaryStage.getHeight());

        // Dynamic resizing of the background
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> backgroundView.setFitWidth(newVal.doubleValue()));
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> backgroundView.setFitHeight(newVal.doubleValue()));

        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        // Lobby Label
        Label lobbyLabel = new Label("Available Lobbies");
        lobbyLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: '" + FontUtils.SUBTITLE_FONT + "';");

        // Lobby List View
        lobbyListView = new ListView<>();
        lobbyListView.setPrefHeight(400);
        lobbyListView.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.8);" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-border-color: #555;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: '" + FontUtils.BODY_FONT + "';" +
                        "-fx-font-size: 16px;"
        );

        // Add hover effect and selection effect to list items
        lobbyListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                // Reset cell style
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                } else {
                    setText(item);

                    // Check if the cell is selected
                    if (lobbyListView.getSelectionModel().getSelectedItems().contains(item)) {
                        setStyle("-fx-background-color: rgba(240, 149, 14, 0.7); -fx-text-fill: white; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                    }

                    // Hover effect
                    setOnMouseEntered(e -> {
                        if (!lobbyListView.getSelectionModel().getSelectedItems().contains(item)) {
                            setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white;");
                        }
                    });

                    setOnMouseExited(e -> {
                        if (!lobbyListView.getSelectionModel().getSelectedItems().contains(item)) {
                            setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                        }
                    });
                }
            }
        });

        // Buttons
        Button createLobbyButton = createStyledButton("Create Lobby");
        Button joinLobbyButton = createStyledButton("Join Lobby");
        Button backButton = createStyledButton("Back");
        joinLobbyButton.setDisable(true);

        // Enable "Join Lobby" button only when a lobby is selected
        lobbyListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            joinLobbyButton.setDisable(newSelection == null);

            // Save the selected lobby to preferences
            if (newSelection != null) {
                preferences.put("selectedLobby", newSelection);
            } else {
                preferences.remove("selectedLobby");
            }
        });

        // Button Actions
        createLobbyButton.setOnAction(e -> showCreateLobbyDialog(primaryStage));
        joinLobbyButton.setOnAction(e -> {
            String selectedLobby = lobbyListView.getSelectionModel().getSelectedItem();
            if (selectedLobby != null) {
                System.out.println("Selected Lobby: " + selectedLobby);
                Okhttp okhttp = new Okhttp();
                okhttp.joinLobby(selectedLobby);

                startGame();  // Chiamata senza bisogno di passare primaryStage
            } else {
                System.out.println("No lobby selected.");
            }
        });


        backButton.setOnAction(e -> {
            if (refreshLobbiesTimeline != null) {
                refreshLobbiesTimeline.stop(); // Ferma il ciclo di aggiornamento
            }
            ModeScreen modeScreen = new ModeScreen();
            modeScreen.show(primaryStage);
        });

        HBox buttonBox = new HBox(20, createLobbyButton, joinLobbyButton);
        buttonBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(backgroundView);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(mainLayout);
        mainPane.setBottom(backButton);

        BorderPane.setAlignment(backButton, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(backButton, new Insets(50));

        mainLayout.getChildren().addAll(lobbyLabel, lobbyListView, buttonBox);

        overlayPane = new StackPane(); // For popups
        overlayPane.setMouseTransparent(true); // By default, passes events to below layers
        overlayPane.setPickOnBounds(false); // Only interact with visible children

        root.getChildren().addAll(mainPane, overlayPane);

        // Aggiungere la barra superiore al layout principale
        VBox layoutWithBar = new VBox(titleBar, root);
        Scene scene = new Scene(layoutWithBar, primaryStage.getWidth(), primaryStage.getHeight(), Color.BLACK);

        primaryStage.setScene(scene);

        Okhttp okhttp = new Okhttp(); // Istanza per gestire le richieste

        refreshLobbiesTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            okhttp.getLobbyListAndUpdateUI(updatedLobbies -> {
                String savedLobby = preferences.get("selectedLobby", null); // Retrieve saved lobby first
                lobbies.clear();
                lobbies.addAll(updatedLobbies);
                lobbyListView.getItems().setAll(lobbies);

                // Restore the selected lobby if it exists in the updated list
                if (savedLobby != null && lobbies.contains(savedLobby)) {
                    lobbyListView.getSelectionModel().select(savedLobby);
                }
            });
        }));
        refreshLobbiesTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshLobbiesTimeline.play();
    }

    /**
     * Fa iniziare il gioco
     */
    private void startGame() {
        Stage stage = (Stage) lobbyListView.getScene().getWindow(); // Ottieni la finestra attuale

        ControllerOnline controller = new ControllerOnline(true, "giocatore2");
        controller.startGame("giocatore2");

        // Aggiungi la logica di stampa per testare la CLI temporanea
        System.out.println("Gioco online avviato per Player 2.");
        controller.startGame("giocatore2");
        while (!controller.checkGameOver()) {
            if (controller.isMyTurn()) {
                System.out.println("Your turn! Press 'h' to hit or 's' to stand: ");
                Scanner scanner = new Scanner(System.in);
                String action = scanner.next();
                if (action.equalsIgnoreCase("h")) {
                    controller.hitCard(true);
                    System.out.println("You drew a card!");
                } else if (action.equalsIgnoreCase("s")) {
                    controller.endTurn();
                    System.out.println("You ended your turn.");
                }
            } else {
                System.out.println("Waiting for opponent...Non è il mio turno");
                try {
                    Thread.sleep(5000); // Aggiunto ritardo per ridurre le richieste al server
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Game over!");
    }

    /**
     * Crea una finestra di dialogo per permettere all'utente di creare una nuova lobby
     * @param primaryStage la finestra principale
     */
    private void showCreateLobbyDialog(Stage primaryStage) {
        // Ferma l'aggiornamento delle lobby quando si apre la finestra di creazione
        if (refreshLobbiesTimeline != null) {
            refreshLobbiesTimeline.stop();
        }

        Pane dimBackground = new Pane();
        dimBackground.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        Scene scene = overlayPane.getScene();
        if (scene != null) {
            dimBackground.prefWidthProperty().bind(scene.widthProperty());
            dimBackground.prefHeightProperty().bind(scene.heightProperty());
        } else {
            dimBackground.setPrefSize(overlayPane.getWidth(), overlayPane.getHeight());
        }

        VBox popup = new VBox(10);
        popup.setAlignment(Pos.CENTER);
        popup.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-padding: 20; -fx-background-radius: 10;");
        popup.setMaxWidth(300);

        Label titleLabel = new Label("Create Lobby");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

        TextField lobbyNameField = new TextField();
        lobbyNameField.setPromptText("Lobby Name");
        lobbyNameField.setStyle("-fx-font-size: 16px; -fx-border-width: 2px;");

        Button createButton = new Button("Create");
        createButton.setStyle("-fx-font-size: 16px; -fx-background-color: #555; -fx-text-fill: white;");
        createButton.setDisable(true);

        lobbyNameField.textProperty().addListener((obs, oldText, newText) -> {
            boolean isValidName = newText.matches("[a-zA-Z0-9 ]+");
            createButton.setDisable(!isValidName || newText.isEmpty());
        });

        createButton.setOnAction(e -> {
            Okhttp okhttp = new Okhttp();
            okhttp.createLobby(lobbyNameField.getText());

            overlayPane.getChildren().removeAll(dimBackground, popup);
            overlayPane.setMouseTransparent(true);

            waitForPlayer(primaryStage);
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-font-size: 16px; -fx-background-color: #555; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> {
            overlayPane.getChildren().removeAll(dimBackground, popup);
            overlayPane.setMouseTransparent(true);

            // Riavvia l'aggiornamento delle lobby se la creazione è annullata
            refreshLobbiesTimeline.play();
        });

        HBox buttonBox = new HBox(10, createButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        popup.getChildren().addAll(titleLabel, lobbyNameField, buttonBox);
        overlayPane.getChildren().addAll(dimBackground, popup);
        overlayPane.setMouseTransparent(false);
    }

    /**
     * Gestisce la finestra d'attesa per l'utente
     * @param primaryStage la finestra d'attesa
     */
    private void waitForPlayer(Stage primaryStage) {
        WaitingScreen waitingScreen = new WaitingScreen();
        waitingScreen.show(primaryStage);
    }

    /**
     * Crea un pulsante
     * @param text il testo nel pulsante
     * @return il pulsante configurato
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        styleButton(button);
        button.setOnMouseEntered(e -> button.setStyle(getHoverStyle()));
        button.setOnMouseExited(e -> styleButton(button));
        button.setOnMousePressed(e -> button.setStyle(getPressedStyle()));
        button.setOnMouseReleased(e -> styleButton(button));
        return button;
    }

    /**
     * Imposta lo stile del pulsante
     * @param button il pulsante
     */
    private void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: #333333;" +
                        "-fx-text-fill: #cccccc;" +
                        "-fx-padding: 12px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #555555;" +
                        "-fx-border-width: 3px;" +
                        "-fx-border-radius: 6;" +
                        "-fx-effect: dropshadow(gaussian, #222222, 15, 0.5, 0, 0);" +
                        "-fx-cursor: hand;"
        );
        button.setFont(FontUtils.PIXEL_HORROR);
        button.setMinWidth(220);
        button.setMinHeight(60);
    }

    /**
     * Ritorna lo stile del pulsante quando il cursore del mouse passa sopra
     * @return lo stile del pulsante quando il cursore del mouse passa sopra
     */
    private String getHoverStyle() {
        return "-fx-background-color: #555555;" +
                "-fx-text-fill: #ffffff;" +
                "-fx-padding: 12px;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: #888888;" +
                "-fx-border-width: 3px;" +
                "-fx-border-radius: 6;" +
                "-fx-effect: dropshadow(gaussian, #444444, 20, 0.8, 0, 0);";
    }

    /**
     * Ritorna lo stile del pulsante quando il cursore del mouse lo preme
     * @return lo stile del pulsante quando il cursore del mouse lo preme
     */
    private String getPressedStyle() {
        return "-fx-background-color: #222222;" +
                "-fx-text-fill: #aaaaaa;" +
                "-fx-padding: 12px;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: #444444;" +
                "-fx-border-width: 3px;" +
                "-fx-border-radius: 6;" +
                "-fx-effect: dropshadow(gaussian, #111111, 30, 1.0, 0, 0);";
    }

    /**
     * Crea un HBox per il titolo nella finestra principale
     * @param stage la finestra principale
     * @return HBox contenente il titolo personalizzato
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

}
