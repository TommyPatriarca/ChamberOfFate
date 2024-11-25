package com.cof.ui;

import com.cof.managers.MusicManager;
import com.cof.managers.SoundManager;
import com.cof.utils.FontUtils;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
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
    private final DropShadow dropShadow = new DropShadow(20, Color.DARKRED);

    @Override
    public void start(Stage primaryStage) {
        musicManager = new MusicManager();
        soundManager = new SoundManager();

        // Static background
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/LoadingScreen.png")));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(true);

        Button startButton = createButton("Start");
        Button exitButton = createButton("Exit");
        muteButton = createMuteButton();

        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(20);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(startButton, exitButton);

        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);

        HBox muteContainer = new HBox(muteButton);
        muteContainer.setAlignment(Pos.BOTTOM_RIGHT);
        muteContainer.setPadding(new Insets(0, 20, 20, 0));

        mainLayout.getChildren().addAll(topSpacer, buttonBox, muteContainer);

        StackPane root = new StackPane(backgroundView, mainLayout);

        // Custom cursor
        Image cursorImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/crosshair.png")));
        ImageCursor customCursor = new ImageCursor(cursorImage, cursorImage.getWidth() / 2, cursorImage.getHeight() / 2);
        root.setCursor(customCursor);

        Scene scene = new Scene(root);

        handleResponsiveness(scene, backgroundView, startButton, exitButton, muteButton);

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        if (!alreadyStarted) {
            musicManager.play();
            alreadyStarted = true;
        }
    }

    private void handleResponsiveness(Scene scene, ImageView backgroundView, Button... buttons) {
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            backgroundView.setFitWidth(newVal.doubleValue());
            adjustButtonSize(buttons, newVal.doubleValue(), scene.getHeight());
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            backgroundView.setFitHeight(newVal.doubleValue());
            adjustButtonSize(buttons, scene.getWidth(), newVal.doubleValue());
        });
    }

    private void adjustButtonSize(Button[] buttons, double width, double height) {
        double newButtonWidth = Math.min(width * 0.15, 200);
        double newButtonHeight = Math.min(height * 0.1, 80);

        for (Button button : buttons) {
            button.setPrefWidth(newButtonWidth);
            button.setPrefHeight(newButtonHeight);
        }
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        styleButton(button);

        button.setOnMouseEntered(e -> {
            button.setEffect(glow);
            button.setStyle(getHoverStyle());
            smoothScale(button, 1.1);
        });

        button.setOnMouseExited(e -> {
            button.setEffect(dropShadow);
            styleButton(button);
            smoothScale(button, 1.0);
        });

        button.setOnMousePressed(e -> button.setStyle(getPressedStyle()));
        button.setOnMouseReleased(e -> styleButton(button));

        if (text.equalsIgnoreCase("Exit")) {
            button.setOnAction(e -> System.exit(0));
        } else if (text.equalsIgnoreCase("Start")) {
            button.setOnAction(e -> {
                soundManager.ShotgunSound();
                playStartGameAnimation(button);
            });
        }

        return button;
    }

    private void smoothScale(Button button, double scale) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
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

    private Button createMuteButton() {
        Button button = new Button("Mute");
        styleButton(button);

        button.setOnMouseEntered(e -> {
            button.setEffect(glow);
            button.setStyle(getHoverStyle());
            smoothScale(button, 1.1);
        });

        button.setOnMouseExited(e -> {
            button.setEffect(null);
            styleButton(button);
            smoothScale(button, 1.0);
        });

        button.setOnAction(e -> {
            toggleMusicState();
            button.setText(MusicManager.isMuted() ? "Unmute" : "Mute");
        });

        return button;
    }

    private void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: crimson;" +
                        "-fx-padding: 10px;" +
                        "-fx-background-radius: 8;" + // Slightly rounded for a pixelated look
                        "-fx-border-color: darkred;" +
                        "-fx-border-width: 4px;" +
                        "-fx-border-radius: 8;" + // Matches the background radius for consistency
                        "-fx-cursor: hand;"
        );
        button.setFont(FontUtils.PIXEL_HORROR); // Apply the custom horror font
        button.setMinWidth(120);
        button.setMinHeight(50);
    }

    private String getHoverStyle() {
        return "-fx-background-color: darkred;" +
                "-fx-text-fill: black;" +
                "-fx-padding: 10px;" +
                "-fx-background-radius: 8;" + // Matches the rounded pixel style
                "-fx-border-color: crimson;" +
                "-fx-border-width: 4px;" +
                "-fx-border-radius: 8;" +
                "-fx-effect: dropshadow(gaussian, crimson, 20, 0.8, 0, 0);"; // Pulsating glow effect
    }

    private String getPressedStyle() {
        return "-fx-background-color: crimson;" +
                "-fx-text-fill: black;" +
                "-fx-padding: 10px;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 4px;" +
                "-fx-border-radius: 8;" +
                "-fx-effect: dropshadow(gaussian, red, 30, 1.0, 0, 0);"; // Intensifies for a pressed look
    }





    private void toggleMusicState() {
        if (MusicManager.isMuted()) {
            MusicManager.unmute();
        } else {
            MusicManager.mute();
        }
    }
}
