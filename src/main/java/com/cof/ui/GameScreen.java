package com.cof.ui;

import com.cof.utils.FontUtils;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
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

    public void show(Stage primaryStage) {
        StackPane root = new StackPane();

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

        MenuBar menuBar = createMenuBar();
        HBox topRightContainer = new HBox();
        topRightContainer.setAlignment(Pos.CENTER_RIGHT);
        topRightContainer.getChildren().add(menuBar);
        gameLayout.setTop(topRightContainer);

        VBox gameArea = createGameArea();
        gameLayout.setCenter(gameArea);

        HBox bottomControls = createBottomControls();
        gameLayout.setBottom(bottomControls);

        root.getChildren().add(gameLayout);

        Scene scene = new Scene(root);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        fadeIn.play();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.8);" +
                        "-fx-text-fill: white;"
        );

        Menu gameMenu = new Menu("Game");
        MenuItem surrender = new MenuItem("Surrender");
        MenuItem settings = new MenuItem("Settings");
        MenuItem exitToLobby = new MenuItem("Exit to Lobby");
        MenuItem rules = new MenuItem("Game Rules");

        surrender.setOnAction(e -> showSurrenderDialog());
        settings.setOnAction(e -> showSettingsDialog());
        exitToLobby.setOnAction(e -> showExitConfirmation());
        rules.setOnAction(e -> showRules());

        gameMenu.getItems().addAll(rules, settings, new SeparatorMenuItem(), surrender, exitToLobby);
        menuBar.getMenus().add(gameMenu);

        return menuBar;
    }

    private VBox createGameArea() {
        VBox gameArea = new VBox(20);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(20));

        // Placeholder for opponent area
        VBox opponentArea = new VBox(10);
        opponentArea.setAlignment(Pos.CENTER);
        opponentArea.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.3);" +
                        "-fx-padding: 20px;" +
                        "-fx-min-height: 200px;"
        );
        Label opponentLabel = new Label("Opponent's Area");
        opponentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        opponentArea.getChildren().add(opponentLabel);

        VBox tableArea = new VBox(10);
        tableArea.setAlignment(Pos.CENTER);
        tableArea.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.3);" +
                        "-fx-padding: 20px;" +
                        "-fx-min-height: 200px;"
        );
        Label tableLabel = new Label("Game Table");
        tableLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        tableArea.getChildren().add(tableLabel);

        VBox playerArea = new VBox(10);
        playerArea.setAlignment(Pos.CENTER);
        playerArea.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.3);" +
                        "-fx-padding: 20px;" +
                        "-fx-min-height: 200px;"
        );
        Label playerLabel = new Label("Player's Area (Max 5 cards)");
        playerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        playerArea.getChildren().add(playerLabel);

        gameArea.getChildren().addAll(opponentArea, tableArea, playerArea);
        return gameArea;
    }

    private HBox createBottomControls() {
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.BOTTOM_RIGHT);
        controls.setPadding(new Insets(20));

        Button muteButton = new Button();
        ImageView soundIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/GameBackground.png")))); // mettere un immagine
        soundIcon.setFitWidth(30);
        soundIcon.setFitHeight(30);
        muteButton.setGraphic(soundIcon);
        muteButton.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.8);" +
                        "-fx-background-radius: 30;" +
                        "-fx-min-width: 60px;" +
                        "-fx-min-height: 60px;"
        );
        muteButton.setEffect(dropShadow);

        muteButton.setOnAction(e -> {
            isMuted = !isMuted;
            ImageView newIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/GameBackground.png"))));
            newIcon.setFitWidth(30);
            newIcon.setFitHeight(30);
            muteButton.setGraphic(newIcon);
            // roba per mutare
        });

        controls.getChildren().add(muteButton);
        return controls;
    }

    private void showSurrenderDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Surrender");
        alert.setHeaderText("Are you sure you want to surrender?");
        alert.setContentText("This will count as a loss.");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.95);" +
                        "-fx-text-fill: white;"
        );

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // logica per arrendersi
            }
        });
    }

    private void showSettingsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.95);" +
                        "-fx-text-fill: white;"
        );

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        //da mettere le vcrie impostazioni

        dialogPane.setContent(content);
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        dialog.show();
    }

    private void showExitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit to Lobby");
        alert.setHeaderText("Are you sure you want to exit to lobby?");
        alert.setContentText("Current game progress will be lost.");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.95);" +
                        "-fx-text-fill: white;"
        );

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // logica per tornare indietro alla lobby
            }
        });
    }

    private void showRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Rules");
        alert.setHeaderText("Basic Rules");
        alert.setContentText("• Each player can have maximum 5 cards\n" +
                "• More rules to be added...");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.95);" +
                        "-fx-text-fill: white;"
        );

        alert.show();
    }
}