package com.cof.ui;

import com.cof.managers.MusicManager;
import com.cof.utils.FontUtils;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class GameScreen {
    private boolean isMuted = false;
    private final DropShadow dropShadow = new DropShadow(10, Color.BLACK);
    private StackPane root;
    private VBox menuOverlay;
    private boolean isMenuOpen = false;

    public void show(Stage primaryStage) {
        root = new StackPane();

        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/GameBackground.png")));
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)
        );
        root.setBackground(new Background(background));

        BorderPane gameLayout = new BorderPane();

        Button menuButton = createMenuButton();
        HBox topRightContainer = new HBox(10);
        topRightContainer.setAlignment(Pos.TOP_RIGHT);
        topRightContainer.setPadding(new Insets(20));
        topRightContainer.getChildren().add(menuButton);
        gameLayout.setTop(topRightContainer);

        VBox gameArea = createGameArea();
        gameLayout.setCenter(gameArea);

        HBox bottomControls = createBottomControls();
        gameLayout.setBottom(bottomControls);

        menuOverlay = createMenuOverlay();
        menuOverlay.setVisible(false);
        menuOverlay.setMouseTransparent(true); // Correzione: setManageableWhen non esiste

        root.getChildren().addAll(gameLayout, menuOverlay);

        Scene scene = new Scene(root);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        fadeIn.play();
    }

    private VBox createGameArea() {
        VBox gameArea = new VBox(20);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(20));

        // Opponent area
        VBox opponentArea = createGameSection("Opponent's Area");

        // Table area
        VBox tableArea = createGameSection("Game Table");

        // Player area
        VBox playerArea = createGameSection("Player's Area (Max 5 cards)");

        gameArea.getChildren().addAll(opponentArea, tableArea, playerArea);
        return gameArea;
    }

    private VBox createGameSection(String title) {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);
        section.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.3);" +
                        "-fx-padding: 20px;" +
                        "-fx-min-height: 200px;" +
                        "-fx-background-radius: 10px;" + // Aggiunto bordi arrotondati
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0, 0, 0);" // Aggiunto ombra
        );

        Label label = new Label(title);
        label.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold"
        );

        section.getChildren().add(label);
        return section;
    }

    private HBox createBottomControls() {
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.BOTTOM_RIGHT);
        controls.setPadding(new Insets(20));

        Button muteButton = new Button();
        ImageView soundIcon = new ImageView(getClass().getResource("/images/GameBackground.png").toExternalForm());
        soundIcon.setFitWidth(30);
        soundIcon.setFitHeight(30);
        muteButton.setGraphic(soundIcon);
        muteButton.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.8);" +
                        "-fx-background-radius: 30;" +
                        "-fx-min-width: 60px;" +
                        "-fx-min-height: 60px;" +
                        "-fx-cursor: hand"
        );
        muteButton.setEffect(dropShadow);

        muteButton.setOnAction(e -> {
            isMuted = !isMuted;
            ImageView newIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/GameBackground.png"))));
            newIcon.setFitWidth(30);
            newIcon.setFitHeight(30);
            muteButton.setGraphic(newIcon);
        });

        controls.getChildren().add(muteButton);
        return controls;
    }

    private Button createMenuButton() {
        Button menuButton = new Button("☰");
        menuButton.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.8);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 20px;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-min-width: 50px;" +
                        "-fx-min-height: 50px;" +
                        "-fx-cursor: hand"
        );
        menuButton.setEffect(dropShadow);
        menuButton.setOnAction(e -> toggleMenu());
        return menuButton;
    }

    private VBox createMenuOverlay() {
        VBox overlay = new VBox(15);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.85);" +
                        "-fx-padding: 20px"
        );

        overlay.getChildren().addAll(
                createMenuButton("Game Rules", this::showRules),
                createMenuButton("Settings", this::showSettingsDialog),
                createMenuButton("Surrender", this::showSurrenderDialog),
                createMenuButton("Exit to Lobby", this::showExitConfirmation),
                createMenuButton("Close Menu", () -> toggleMenu())
        );

        return overlay;
    }

    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);

        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-min-width: 200px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10px;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(button.getStyle() + "-fx-background-color: rgba(255, 255, 255, 0.1);")
        );

        button.setOnMouseExited(e ->
                button.setStyle(button.getStyle() + "-fx-background-color: transparent;")
        );

        button.setOnAction(e -> action.run());
        return button;
    }

    private void toggleMenu() {
        if (!isMenuOpen) {
            root.getChildren().get(0).setEffect(new GaussianBlur(10));
            menuOverlay.setVisible(true);
            menuOverlay.setMouseTransparent(false);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), menuOverlay);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        } else {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), menuOverlay);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                menuOverlay.setVisible(false);
                menuOverlay.setMouseTransparent(true);
                root.getChildren().get(0).setEffect(null);
            });
            fadeOut.play();
        }
        isMenuOpen = !isMenuOpen;
    }

    private enum DialogType {
        CONFIRMATION,
        INFORMATION
    }

    private void showDialog(String title, String content, DialogType type) {
        if (isMenuOpen) toggleMenu();

        VBox dialogBox = new VBox(20);
        dialogBox.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.95);" +
                        "-fx-padding: 30px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-max-width: 400px;" +
                        "-fx-max-height: 400px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 20, 0, 0, 0)"
        );
        dialogBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold"
        );

        Label contentLabel = new Label(content);
        contentLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-wrap-text: true"
        );

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(20, 0, 0, 0));

        if (type == DialogType.CONFIRMATION) {
            Button confirmButton = new Button("Confirm");
            Button cancelButton = new Button("Cancel");
            styleDialogButton(confirmButton, true);
            styleDialogButton(cancelButton, false);

            confirmButton.setOnAction(e -> closeDialog(dialogBox));
            cancelButton.setOnAction(e -> closeDialog(dialogBox));

            buttons.getChildren().addAll(confirmButton, cancelButton);
        } else {
            Button closeButton = new Button("Close");
            styleDialogButton(closeButton, false);
            closeButton.setOnAction(e -> closeDialog(dialogBox));
            buttons.getChildren().add(closeButton);
        }

        dialogBox.getChildren().addAll(titleLabel, contentLabel, buttons);

        root.getChildren().get(0).setEffect(new GaussianBlur(10));
        dialogBox.setOpacity(0);
        root.getChildren().add(dialogBox);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), dialogBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void styleDialogButton(Button button, boolean isPrimary) {
        String baseColor = isPrimary ? "#4CAF50" : "#757575";
        String hoverColor = isPrimary ? "#45a049" : "#666666";

        button.setStyle(
                "-fx-background-color: " + baseColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-cursor: hand"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                button.getStyle().replace(baseColor, hoverColor)
        ));

        button.setOnMouseExited(e -> button.setStyle(
                button.getStyle().replace(hoverColor, baseColor)
        ));
    }

    private void closeDialog(VBox dialogBox) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), dialogBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            root.getChildren().remove(dialogBox);
            root.getChildren().get(0).setEffect(null);
        });
        fadeOut.play();
    }

    private void showSurrenderDialog() {

            VBox dialogBox = new VBox(20);
            dialogBox.setStyle(
                    "-fx-background-color: rgba(40, 40, 40, 0.95);" +
                            "-fx-padding: 30px;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-max-width: 400px;" +
                            "-fx-max-height: 400px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 20, 0, 0, 0)"
            );
            dialogBox.setAlignment(Pos.CENTER);

            Label titleLabel = new Label("Surrender");
            titleLabel.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-font-size: 24px;" +
                            "-fx-font-weight: bold"
            );

            Label messageLabel = new Label("Are you sure you want to surrender? This will count as a loss.");
            messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Button confirmButton = new Button("Confirm");
            Button cancelButton = new Button("Cancel");
            styleDialogButton(confirmButton, true);
            styleDialogButton(cancelButton, false);

            confirmButton.setOnAction(e -> {
                // Logic to handle surrendering
                System.exit(0); // Placeholder for surrender logic
            });

            cancelButton.setOnAction(e -> closeDialog(dialogBox));

            HBox buttons = new HBox(10, confirmButton, cancelButton);
            buttons.setAlignment(Pos.CENTER);

            dialogBox.getChildren().addAll(titleLabel, messageLabel, buttons);

            root.getChildren().get(0).setEffect(new GaussianBlur(10));
            dialogBox.setOpacity(0);
            root.getChildren().add(dialogBox);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), dialogBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
    }

    private void showSettingsDialog() {
        VBox dialogBox = new VBox(20);
        dialogBox.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.95);" +
                        "-fx-padding: 30px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-max-width: 400px;" +
                        "-fx-max-height: 400px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 20, 0, 0, 0)"
        );
        dialogBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Settings");
        titleLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold"
        );

        Label volumeLabel = new Label("Volume:");
        volumeLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;"
        );

        Slider volumeSlider = new Slider(0, 1, MusicManager.getVolume());
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(0.5);
        volumeSlider.setMinorTickCount(4);
        volumeSlider.setBlockIncrement(0.1);

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            MusicManager.setVolume(newValue.doubleValue());
        });

        Button closeButton = new Button("Close");
        styleDialogButton(closeButton, false);
        closeButton.setOnAction(e -> closeDialog(dialogBox));

        dialogBox.getChildren().addAll(titleLabel, volumeLabel, volumeSlider, closeButton);

        root.getChildren().get(0).setEffect(new GaussianBlur(10));
        dialogBox.setOpacity(0);
        root.getChildren().add(dialogBox);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), dialogBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }


    private void showExitConfirmation() {

            VBox dialogBox = new VBox(20);
            dialogBox.setStyle(
                    "-fx-background-color: rgba(40, 40, 40, 0.95);" +
                            "-fx-padding: 30px;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-max-width: 400px;" +
                            "-fx-max-height: 400px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 20, 0, 0, 0)"
            );
            dialogBox.setAlignment(Pos.CENTER);

            Label titleLabel = new Label("Exit to Lobby");
            titleLabel.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-font-size: 24px;" +
                            "-fx-font-weight: bold"
            );

            Label messageLabel = new Label("Are you sure you want to exit to lobby? Current game progress will be lost.");
            messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Button confirmButton = new Button("Confirm");
            Button cancelButton = new Button("Cancel");
            styleDialogButton(confirmButton, true);
            styleDialogButton(cancelButton, false);

            confirmButton.setOnAction(e -> {
                // Logic to handle exiting to the lobby
                System.exit(0); // Placeholder for exit logic
            });

            cancelButton.setOnAction(e -> closeDialog(dialogBox));

            HBox buttons = new HBox(10, confirmButton, cancelButton);
            buttons.setAlignment(Pos.CENTER);

            dialogBox.getChildren().addAll(titleLabel, messageLabel, buttons);

            root.getChildren().get(0).setEffect(new GaussianBlur(10));
            dialogBox.setOpacity(0);
            root.getChildren().add(dialogBox);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), dialogBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
    }

    private void showRules() {
        showDialog(
                "Game Rules",
                "• Each player can have maximum 5 cards\n• Yf suttix is gay sutti gay",
                DialogType.INFORMATION
        );
    }
}