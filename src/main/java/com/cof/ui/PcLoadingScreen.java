package com.cof.ui;

import com.cof.managers.MusicManager;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Objects;

public class PcLoadingScreen extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a full-screen background image
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/LoadingScreen.png")));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(true);

        // Create the buttons
        Button startButton = createButton("Start");
        Button exitButton = createButton("Exit");
        Button muteButton = createMuteButton();

        // Create an HBox for the top buttons
        HBox topButtonBox = new HBox(20, startButton, exitButton); // 20 pixels of spacing
        topButtonBox.setAlignment(Pos.CENTER);

        // Create an HBox for the mute button
        HBox muteButtonBox = new HBox(muteButton);
        muteButtonBox.setAlignment(Pos.BOTTOM_RIGHT);

        // Create the overlay pane
        StackPane overlay = new StackPane(backgroundView, topButtonBox, muteButtonBox);
        overlay.setStyle("-fx-background-color: rgba(75, 133, 194, 0.7);");

        // Adjust positions when scene is ready
        Scene scene = new Scene(overlay, 800, 600); // Set initial dimensions
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        // Adjust the background and button positions based on actual scene size
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            backgroundView.setFitWidth(newVal.doubleValue());
            topButtonBox.setTranslateY(-scene.getHeight() * 0.3);
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            backgroundView.setFitHeight(newVal.doubleValue());
            muteButtonBox.setTranslateY(-20); // Keep mute button padding from bottom
        });

        // Initialize the music player
        MusicManager musicManager = new MusicManager();
        musicManager.play();
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        styleButton(button);

        button.setOnMouseEntered(event -> {
            button.setScaleX(1.1);
            button.setScaleY(1.1);
            button.setStyle("-fx-background-color: #A52A2A; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 15px; -fx-background-radius: 10px;");
        });

        button.setOnMouseExited(event -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            styleButton(button); // Reset style
        });

        if (text.equals("Exit")) {
            button.setOnAction(event -> System.exit(0));
        } else if (text.equals("Start")) {
            button.setOnAction(event -> {
                // Logica per avviare l'applicazione (se necessario)
            });
        }

        return button;
    }

    private Button createMuteButton() {
        Button muteButton = new Button("Mute");
        styleButton(muteButton);

        muteButton.setOnMouseEntered(event -> {
            muteButton.setScaleX(1.3);
            muteButton.setScaleY(1.3);
            muteButton.setStyle("-fx-background-color: #A52A2A; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px; -fx-background-radius: 10px;");
        });

        muteButton.setOnMouseExited(event -> {
            muteButton.setScaleX(1.0);
            muteButton.setScaleY(1.0);
            styleButton(muteButton); // Reset style
        });

        muteButton.setOnMouseClicked(event -> toggleMusicState());

        return muteButton;
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 15px; -fx-background-radius: 10px;");
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
