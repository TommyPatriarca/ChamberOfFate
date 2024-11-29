package com.cof.ui;

import com.cof.managers.MusicManager;
import com.cof.managers.SoundManager;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.util.Objects;

public class PcLoadingScreen extends Application {

    private MusicManager musicManager;
    private SoundManager soundManager;
    private ImageView muteIcon;
    private boolean alreadyStarted;
    private final Glow glow = new Glow(0.8);
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) {
        musicManager = new MusicManager();
        soundManager = new SoundManager();

        // Static background
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/LoadingBackground.jpg")));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(false);
        backgroundView.setFitWidth(1920);
        backgroundView.setFitHeight(1080);

        muteIcon = createMuteIcon();

        // Barra superiore personalizzata
        HBox titleBar = createCustomTitleBar(primaryStage);

        // Layout principale
        StackPane root = new StackPane();
        root.getChildren().add(backgroundView);

        // Posiziona la barra sopra lo sfondo
        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(titleBar, root);

        // Posiziona il bottone mute
        StackPane.setAlignment(muteIcon, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(muteIcon, new Insets(0, 20, 20, 0));
        root.getChildren().add(muteIcon);

        // Custom cursor
        Image cursorImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/crosshair.png")));
        ImageCursor customCursor = new ImageCursor(cursorImage, cursorImage.getWidth() / 2, cursorImage.getHeight() / 2);
        root.setCursor(customCursor);

        Scene scene = new Scene(mainLayout);

        // Rimuovi la barra di sistema
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        handleBackgroundResize(scene, backgroundView);

        if (!alreadyStarted) {
            musicManager.play();
            alreadyStarted = true;
        }

        // Avvio gioco al click
        root.setOnMouseClicked(event -> {
            if (!muteIcon.isHover() && !titleBar.isHover()) {
                soundManager.ShotgunSound();
                fadeToModeScreen(primaryStage);
            }
        });
    }

    private void handleBackgroundResize(Scene scene, ImageView backgroundView) {
        // Adatta lo sfondo a riempire sempre l'intera finestra
        scene.widthProperty().addListener((obs, oldVal, newVal) -> backgroundView.setFitWidth(newVal.doubleValue()));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> backgroundView.setFitHeight(newVal.doubleValue()));
    }

    private ImageView createMuteIcon() {
        // Carica le immagini per mute e unmute
        Image muteImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/mute.png")));
        Image unmuteImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/unmute.png")));

        ImageView imageView = new ImageView(MusicManager.isMuted() ? muteImage : unmuteImage);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        // Aggiungi effetto hover
        imageView.setOnMouseEntered(e -> imageView.setEffect(glow));
        imageView.setOnMouseExited(e -> imageView.setEffect(null));

        // Gestisci il click per alternare tra mute e unmute
        imageView.setOnMouseClicked(e -> {
            if (MusicManager.isMuted()) {
                MusicManager.unmute();
                imageView.setImage(unmuteImage);
            } else {
                MusicManager.mute();
                imageView.setImage(muteImage);
            }
        });

        return imageView;
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

    private void fadeToModeScreen(Stage stage) {
        VBox root = (VBox) stage.getScene().getRoot();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            // Implementa qui il passaggio alla schermata del gioco
        });
        fadeOut.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
