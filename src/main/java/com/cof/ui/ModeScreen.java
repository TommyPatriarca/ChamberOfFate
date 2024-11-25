package com.cof.ui;

import com.cof.utils.FontUtils;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class ModeScreen {

    public void show(Stage primaryStage) {
        VBox mainLayout = new VBox(30);
        mainLayout.setAlignment(Pos.CENTER);

        Button onlineButton = createButton("Online");
        Button offlineButton = createButton("Offline");

        mainLayout.getChildren().addAll(onlineButton, offlineButton);

        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/LoadingScreen.png")));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(true);
        backgroundView.setFitWidth(primaryStage.getWidth());
        backgroundView.setFitHeight(primaryStage.getHeight());

        StackPane root = new StackPane(backgroundView, mainLayout);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight(), Color.BLACK);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        primaryStage.setScene(scene); // Reuse the same Stage
        fadeIn.play();
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
                "-fx-background-color: black;" +
                        "-fx-text-fill: crimson;" +
                        "-fx-padding: 10px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: darkred;" +
                        "-fx-border-width: 4px;" +
                        "-fx-border-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        button.setFont(FontUtils.PIXEL_HORROR);
        button.setMinWidth(200);
        button.setMinHeight(50);
    }

    private String getHoverStyle() {
        return "-fx-background-color: darkred;" +
                "-fx-text-fill: black;" +
                "-fx-padding: 10px;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: crimson;" +
                "-fx-border-width: 4px;" +
                "-fx-border-radius: 8;" +
                "-fx-effect: dropshadow(gaussian, crimson, 20, 0.8, 0, 0);";
    }

    private String getPressedStyle() {
        return "-fx-background-color: crimson;" +
                "-fx-text-fill: black;" +
                "-fx-padding: 10px;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 4px;" +
                "-fx-border-radius: 8;" +
                "-fx-effect: dropshadow(gaussian, red, 30, 1.0, 0, 0);";
    }

    private void fadeToLobbyScreen(Stage stage) {
        StackPane root = (StackPane) stage.getScene().getRoot();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            PcLobbyScreen lobbyScreen = new PcLobbyScreen();
            lobbyScreen.show(stage);
        });
        fadeOut.play();
    }
}
