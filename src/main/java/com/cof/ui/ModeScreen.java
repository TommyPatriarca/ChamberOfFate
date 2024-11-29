package com.cof.ui;

import com.cof.utils.FontUtils;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class ModeScreen {

    private double xOffset = 0;
    private double yOffset = 0;

    public void show(Stage primaryStage) {
        // Barra superiore
        HBox titleBar = createCustomTitleBar(primaryStage);

        // Layout principale
        VBox mainLayout = new VBox(30); // Spaziatura tra i pulsanti
        mainLayout.setAlignment(Pos.CENTER); // Allinea inizialmente al centro
        mainLayout.setPadding(new Insets(50, 0, 0, 0)); // Aggiungi un margine superiore di 50px per spostare tutto più in basso
        
        mainLayout.setPadding(new Insets(50, 0, 0, 0)); // Margine superiore per ulteriore spazio

        Button onlineButton = createButton("Online");
        Button offlineButton = createButton("Offline");

        mainLayout.getChildren().addAll(onlineButton, offlineButton);

        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/ModeBackground.jpg")));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(true);
        backgroundView.setFitWidth(primaryStage.getWidth());
        backgroundView.setFitHeight(primaryStage.getHeight());

        StackPane root = new StackPane(backgroundView, mainLayout);

        // Aggiungere la barra superiore al layout principale
        VBox layoutWithBar = new VBox(titleBar, root);

        Scene scene = new Scene(layoutWithBar, primaryStage.getWidth(), primaryStage.getHeight(), Color.BLACK);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        primaryStage.setScene(scene); // Reuse the same Stage
        fadeIn.play();
    }

    private HBox createCustomTitleBar(Stage stage) {
        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setStyle("-fx-background-color: black; -fx-padding: 2;");
        titleBar.setPrefHeight(20);

        Button closeButton = new Button("X");
        closeButton.setStyle(
                "-fx-background-color: red;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> System.exit(0));

        titleBar.getChildren().add(closeButton);
        HBox.setMargin(closeButton, new Insets(0, 10, 0, 10));

        // Aggiungi drag event per spostare la finestra
        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });

        return titleBar;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        styleButton(button);

        button.setOnMouseEntered(e -> button.setStyle(getHoverStyle()));
        button.setOnMouseExited(e -> styleButton(button));
        button.setOnMousePressed(e -> button.setStyle(getPressedStyle()));
        button.setOnMouseReleased(e -> styleButton(button));

        return button;
    }

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
