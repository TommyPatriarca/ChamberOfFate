package com.cof.ui;

import com.cof.utils.FontUtils;
import com.controller.Controller;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
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

/**
 * La classe dell'interfaccia grafica per la selezione della modalità di gioco
 */
public class ModeScreen {

    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Configura la finestra principale
     * @param primaryStage la finestra  principale
     */
    public void show(Stage primaryStage) {
        // Barra superiore
        HBox titleBar = createCustomTitleBar(primaryStage);

        // Layout principale
        VBox mainLayout = new VBox(30); // Spaziatura tra i pulsanti
        mainLayout.setAlignment(Pos.CENTER); // Allinea inizialmente al centro
        mainLayout.setPadding(new Insets(550, 0, 0, 0));

        Button onlineButton = createButton("Online");
        Button offlineButton = createButton("Offline");

        mainLayout.getChildren().addAll(onlineButton, offlineButton);

        // Immagine di sfondo
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/ModeBackground.jpg")));
        ImageView backgroundView = new ImageView(backgroundImage);


        backgroundView.setPreserveRatio(false);
        backgroundView.setFitWidth(primaryStage.getWidth());
        backgroundView.setFitHeight(primaryStage.getHeight());


        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> backgroundView.setFitWidth(newVal.doubleValue()));
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> backgroundView.setFitHeight(newVal.doubleValue()));

        StackPane root = new StackPane(backgroundView, mainLayout);

        // Aggiungere la barra superiore al layout principale
        VBox layoutWithBar = new VBox(titleBar, root);

        Scene scene = new Scene(layoutWithBar, primaryStage.getWidth(), primaryStage.getHeight(), Color.BLACK);

        // Transizione di fade-in
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);
        fadeIn.setInterpolator(Interpolator.EASE_BOTH);

        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        primaryStage.setScene(scene); // Reuse the same Stage
        fadeIn.play();
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

    /**
     * Crea un pulsante
     * @param text il testo nel pulsante
     * @return il pulsante configurato
     */
    private Button createButton(String text) {
        Button button = new Button(text);
        styleButton(button);

        button.setOnMouseEntered(e -> {
            button.setScaleX(1.05);
            button.setScaleY(1.05);
            button.setStyle(getHoverStyle());
        });
        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            styleButton(button);
        });

        button.setOnMousePressed(e -> button.setStyle(getPressedStyle()));
        button.setOnMouseReleased(e -> styleButton(button));

        // Action for Offline Button
        if ("Offline".equals(text)) {
            button.setOnAction(e -> {
                Controller controller = new Controller(false); // Initialize in offline mode
                OfflineGameScreen offlineGameScreen = new OfflineGameScreen(controller); // Pass the controller
                offlineGameScreen.show((Stage) button.getScene().getWindow()); // Use the same Stage
            });
        }
        else if("Online".equals(text)){
            button.setOnAction(e -> {
                PcLobbyScreen pcLobbyScreen = new PcLobbyScreen();
                pcLobbyScreen.show((Stage) button.getScene().getWindow()); // Use the same Stage
            });
        }

        return button;
    }
    /**
     * Imposta lo stile del pulsante
     * @param button il pulsante
     */
    private void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: #2A2A2A;" +
                        "-fx-text-fill: #DDDDDD;" +
                        "-fx-padding: 12px;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #666666;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, #000000, 10, 0.8, 0, 2);" +
                        "-fx-cursor: hand;");
        button.setFont(FontUtils.PIXEL_HORROR);
        button.setMinWidth(240);
        button.setMinHeight(70);
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
}
