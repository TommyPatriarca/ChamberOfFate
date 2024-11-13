package com.cof.ui;

import com.cof.managers.MusicManager;
import com.cof.managers.SoundManager;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.util.Duration;

import java.util.Objects;

public class PcLoadingScreen extends Application {
    private MusicManager musicManager;
    private SoundManager soundManager;
    private Button muteButton;
    private boolean alreadyStarted;
    private final Glow glow = new Glow(0.8);
    private final DropShadow dropShadow = new DropShadow(20, Color.RED);

    @Override
    public void start(Stage primaryStage) {
        musicManager = new MusicManager();
        soundManager = new SoundManager();

        // Background statico
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/LoadingScreen.png")));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(true);

        Button startButton = createButton("Start");
        Button exitButton = createButton("Exit");
        muteButton = createMuteButton();

        // Aggiungi pulsazione ai pulsanti
        addPulseAnimation(startButton);
        addPulseAnimation(exitButton);

        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(20);

        // Box per i pulsanti principali senza sfondo
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(startButton, exitButton);

        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);

        HBox muteContainer = new HBox(muteButton);
        muteContainer.setAlignment(Pos.BOTTOM_RIGHT);
        muteContainer.setPadding(new Insets(0, 20, 20, 0));

        mainLayout.getChildren().addAll(topSpacer, buttonBox, muteContainer);

        // Aggiungi effetto nebbia/particelle
        Region fogEffect = createFogEffect();

        StackPane root = new StackPane(backgroundView, fogEffect, mainLayout);

        Scene scene = new Scene(root);

        // Gestione del ridimensionamento
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            backgroundView.setFitWidth(newVal.doubleValue());
            fogEffect.setPrefWidth(newVal.doubleValue());
            double newButtonSize = Math.min(newVal.doubleValue() * 0.15, 200);
            startButton.setPrefWidth(newButtonSize);
            exitButton.setPrefWidth(newButtonSize);
            muteButton.setPrefWidth(newButtonSize * 0.8);
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            backgroundView.setFitHeight(newVal.doubleValue());
            fogEffect.setPrefHeight(newVal.doubleValue());
            double newButtonHeight = Math.min(newVal.doubleValue() * 0.1, 80);
            startButton.setPrefHeight(newButtonHeight);
            exitButton.setPrefHeight(newButtonHeight);
            muteButton.setPrefHeight(newButtonHeight * 0.8);
        });

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        if(alreadyStarted==false)
        {
            musicManager.play();
            alreadyStarted=true;
        }
    }

    private Region createFogEffect() {
        Region fog = new Region();
        fog.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.1);" +
                        "-fx-effect: dropshadow(gaussian, rgba(255,002,0.3), 50, 0, 0, 0);"
        );

        // Animazione della nebbia
        Timeline fogAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(fog.opacityProperty(), 0.1)),
                new KeyFrame(Duration.seconds(3), new KeyValue(fog.opacityProperty(), 0.3)),
                new KeyFrame(Duration.seconds(6), new KeyValue(fog.opacityProperty(), 0.1))
        );
        fogAnimation.setCycleCount(Timeline.INDEFINITE);
        fogAnimation.play();

        return fog;
    }

    private void addPulseAnimation(Button button) {
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), button);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        styleButton(button);

        button.setOnMouseEntered(e -> {
            button.setEffect(glow);
            button.setStyle(getHoverStyle());
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });

        button.setOnMouseExited(e -> {
            button.setEffect(dropShadow);
            styleButton(button);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        button.setOnMousePressed(e -> {
            button.setStyle(getPressedStyle());
        });

        if (text.equals("Exit")) {
            button.setOnAction(e -> System.exit(0));
        } else if (text.equals("Start")) {
            button.setOnAction(e -> {
                soundManager.ShotgunSound();
                playStartGameAnimation(button);
            });
        }

        return button;
    }

    private void playStartGameAnimation(Button button) {
        FadeTransition fade = new FadeTransition(Duration.seconds(1), button.getScene().getRoot());
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            PcLobbyScreen lobbyScreen = new PcLobbyScreen();
            lobbyScreen.show((Stage) button.getScene().getWindow());
        });
        fade.play();
    }



    private String getHoverStyle() {
        return "-fx-background-color: #A52A2A;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-padding: 15px 30px;" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: #FFD700;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 10px;";
    }

    private String getPressedStyle() {
        return "-fx-background-color: #800000;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-padding: 15px 30px;" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: #FFA500;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 10px;";
    }

    private Button createMuteButton() {
        Button button = new Button("Mute");
        styleButton(button);

        button.setOnMouseEntered(e -> {
            button.setEffect(glow);
            button.setStyle(getHoverStyle());
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });

        button.setOnMouseExited(e -> {
            button.setEffect(null);
            styleButton(button);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        button.setOnAction(e -> {
            toggleMusicState();
            button.setText(MusicManager.isMuted() ? "Unmute" : "Mute");
        });

        return button;
    }

    private void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: #8B0000;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-padding: 15px 30px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-cursor: hand;"
        );
        button.setEffect(dropShadow);
        button.setMinWidth(100);
        button.setMinHeight(40);
    }

    private void toggleMusicState() {
        if (MusicManager.isMuted()) {
            MusicManager.unmute();
        } else {
            MusicManager.mute();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}