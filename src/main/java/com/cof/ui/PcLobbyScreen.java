package com.cof.ui;

import com.cof.utils.FontUtils;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class PcLobbyScreen {
    private final DropShadow dropShadow = new DropShadow(20, Color.RED);
    private final Glow glow = new Glow(0.8);
    private final List<String> lobbies = new ArrayList<>();
    private ListView<String> lobbyListView;

    public void show(Stage primaryStage) {
        // Create main container with gradient background
        VBox mainLayout = new VBox(40);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(60));

        // Create gradient background
        Stop[] stops = new Stop[] {
                new Stop(0, Color.rgb(20, 20, 20)),
                new Stop(1, Color.rgb(40, 0, 0))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE, stops);
        mainLayout.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Title with enhanced styling
        Label title = new Label("CHAMBER OF FATE");
        title.setStyle("-fx-font-size: 56px; -fx-text-fill: linear-gradient(to bottom, #ffffff, #ff0000); -fx-font-weight: bold; -fx-font-family: '" + FontUtils.TITLE_FONT + "';");
        title.setEffect(new DropShadow(30, Color.RED));

        // Create containers for lobby sections with glass effect
        HBox sectionsContainer = new HBox(50);
        sectionsContainer.setAlignment(Pos.CENTER);

        // Create Lobby Section
        VBox createLobbyBox = createGlassPane();
        createLobbyBox.setPrefWidth(450);

        Label createSectionLabel = new Label("CREATE NEW LOBBY");
        createSectionLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: '" + FontUtils.SUBTITLE_FONT + "';");
        createSectionLabel.setEffect(new DropShadow(10, Color.RED));

        TextField lobbyNameField = createStyledTextField("Lobby Name");
        PasswordField passwordField = createStyledPasswordField();

        // Enhanced radio buttons
        VBox radioBox = new VBox(15);
        radioBox.setAlignment(Pos.CENTER);
        RadioButton publicLobby = createStyledRadioButton("Public Lobby");
        RadioButton privateLobby = createStyledRadioButton("Private Lobby");

        ToggleGroup lobbyTypeGroup = new ToggleGroup();
        publicLobby.setToggleGroup(lobbyTypeGroup);
        privateLobby.setToggleGroup(lobbyTypeGroup);
        publicLobby.setSelected(true);

        radioBox.getChildren().addAll(publicLobby, privateLobby);

        Button createLobbyButton = createStyledButton("Create Lobby");
        createLobbyButton.setOnAction(e -> {
            if (!lobbyNameField.getText().isEmpty()) {
                String lobbyType = publicLobby.isSelected() ? "[Public] " : "[Private] ";
                lobbies.add(lobbyType + lobbyNameField.getText());
                lobbyListView.getItems().setAll(lobbies);
                lobbyNameField.clear();
                passwordField.clear();
            }
        });

        createLobbyBox.getChildren().addAll(
                createSectionLabel,
                new Separator(javafx.geometry.Orientation.HORIZONTAL),
                lobbyNameField,
                passwordField,
                radioBox,
                createLobbyButton
        );

        // Join Lobby Section
        VBox joinLobbyBox = createGlassPane();
        joinLobbyBox.setPrefWidth(450);

        Label joinSectionLabel = new Label("AVAILABLE LOBBIES");
        joinSectionLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: '" + FontUtils.SUBTITLE_FONT + "';");
        joinSectionLabel.setEffect(new DropShadow(10, Color.RED));

        lobbyListView = new ListView<>();
        lobbyListView.setPrefHeight(300);
        lobbyListView.setStyle(
                "-fx-background-color: rgba(60, 60, 60, 0.5);" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-font-family: '" + FontUtils.BODY_FONT + "';"
        );

        HBox buttonBox = new HBox(25);
        buttonBox.setAlignment(Pos.CENTER);
        Button refreshButton = createStyledButton("Refresh");
        Button joinButton = createStyledButton("Join");

        refreshButton.setOnAction(e -> lobbyListView.getItems().setAll(lobbies));
        joinButton.setOnAction(e -> {
            String selected = lobbyListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.startsWith("[Private]")) {
                    showPasswordDialog();
                } else {
                    System.out.println("Joining lobby: " + selected);
                }
            }
        });

        buttonBox.getChildren().addAll(refreshButton, joinButton);

        joinLobbyBox.getChildren().addAll(
                joinSectionLabel,
                new Separator(javafx.geometry.Orientation.HORIZONTAL),
                lobbyListView,
                buttonBox
        );

        sectionsContainer.getChildren().addAll(createLobbyBox, joinLobbyBox);

        // Back button with fixed position
        Button backButton = createStyledButton("Back to Menu");
        backButton.setOnAction(e -> {
            fadeToLoadingScreen(primaryStage);
        });

        mainLayout.getChildren().addAll(title, sectionsContainer, backButton);

        // Create scene with fade-in animation
        Scene scene = new Scene(mainLayout);
        scene.setFill(Color.BLACK);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), mainLayout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        fadeIn.play();
    }

    private VBox createGlassPane() {
        VBox pane = new VBox(20);
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setPadding(new Insets(30));
        pane.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-border-width: 1px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0, 0, 0);"
        );
        return pane;
    }

    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle(
                "-fx-background-color: rgba(60, 60, 60, 0.5);" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: gray;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-border-radius: 5;" +
                        "-fx-font-family: '" + FontUtils.BODY_FONT + "';"
        );
        textField.setPrefWidth(350);
        return textField;
    }

    private PasswordField createStyledPasswordField() {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (optional)");
        passwordField.setStyle(
                "-fx-background-color: rgba(60, 60, 60, 0.5);" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: gray;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-border-radius: 5;" +
                        "-fx-font-family: '" + FontUtils.BODY_FONT + "';"
        );
        passwordField.setPrefWidth(350);
        return passwordField;
    }

    private RadioButton createStyledRadioButton(String text) {
        RadioButton radioButton = new RadioButton(text);
        radioButton.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-family: '" + FontUtils.BODY_FONT + "';"
        );
        return radioButton;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setMinWidth(140);
        button.setStyle(
                "-fx-background-color: #8B0000;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-padding: 12px 24px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5;" +
                        "-fx-font-family: '" + FontUtils.BODY_FONT + "';"
        );
        button.setEffect(dropShadow);

        // Hover effect without size change
        button.setOnMouseEntered(e -> {
            button.setEffect(glow);
            button.setStyle(
                    "-fx-background-color: #A52A2A;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-padding: 12px 24px;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;" +
                            "-fx-border-color: #FFD700;" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 5;" +
                            "-fx-font-family: '" + FontUtils.BODY_FONT + "';"
            );
        });

        button.setOnMouseExited(e -> {
            button.setEffect(dropShadow);
            button.setStyle(
                    "-fx-background-color: #8B0000;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-padding: 12px 24px;" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;" +
                            "-fx-border-color: transparent;" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 5;" +
                            "-fx-font-family: '" + FontUtils.BODY_FONT + "';"
            );
        });

        return button;
    }

    private void fadeToLoadingScreen(Stage stage) {
        VBox root = (VBox) stage.getScene().getRoot();

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            PcLoadingScreen loadingScreen = new PcLoadingScreen();
            loadingScreen.start(stage);
        });

        fadeOut.play();
    }

    private void showPasswordDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Enter Lobby Password");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.95);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);"
        );

        PasswordField passwordField = createStyledPasswordField();
        passwordField.setPromptText("Password");
        dialogPane.setContent(passwordField);

        ButtonType joinButtonType = new ButtonType("Join", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(joinButtonType, ButtonType.CANCEL);

        // Style dialog buttons
        dialogPane.lookupButton(joinButtonType).setStyle(
                "-fx-background-color: #8B0000;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-font-family: '" + FontUtils.BODY_FONT + "';"
        );
        dialogPane.lookupButton(ButtonType.CANCEL).setStyle(
                "-fx-background-color: #4a4a4a;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-font-family: '" + FontUtils.BODY_FONT + "';"
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == joinButtonType) {
                return passwordField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(password -> {
            System.out.println("Joining private lobby with password: " + password);
        });
    }
}