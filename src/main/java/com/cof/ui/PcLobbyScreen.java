package com.cof.ui;

import com.cof.utils.FontUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PcLobbyScreen {

    private final List<String> lobbies = new ArrayList<>();
    private ListView<String> lobbyListView;
    private StackPane overlayPane; // For popups over the content

    public void show(Stage primaryStage) {
        // Background image
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/LobbyBackground.jpg")));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(false);
        backgroundView.setFitWidth(primaryStage.getWidth());
        backgroundView.setFitHeight(primaryStage.getHeight());

        // Dynamic resizing of the background
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> backgroundView.setFitWidth(newVal.doubleValue()));
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> backgroundView.setFitHeight(newVal.doubleValue()));

        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        // Lobby Label
        Label lobbyLabel = new Label("Available Lobbies");
        lobbyLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: '" + FontUtils.SUBTITLE_FONT + "';");

// Lobby List View
        lobbyListView = new ListView<>();
        lobbyListView.setPrefHeight(300);
        lobbyListView.setStyle(
                "-fx-background-color: rgba(40, 40, 40, 0.8);" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-border-color: #555;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: '" + FontUtils.BODY_FONT + "';" +
                        "-fx-font-size: 16px;"
        );

// Add hover effect and selection effect to list items
        lobbyListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                // Reset cell style
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                } else {
                    setText(item);

                    // Check if the cell is selected
                    if (lobbyListView.getSelectionModel().getSelectedItems().contains(item)) {
                        setStyle("-fx-background-color: rgba(240, 149, 14, 0.7); -fx-text-fill: white; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                    }

                    // Hover effect
                    setOnMouseEntered(e -> {
                        if (!lobbyListView.getSelectionModel().getSelectedItems().contains(item)) {
                            setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white;");
                        }
                    });

                    setOnMouseExited(e -> {
                        if (!lobbyListView.getSelectionModel().getSelectedItems().contains(item)) {
                            setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                        }
                    });
                }
            }
        });

        // Buttons
        Button createLobbyButton = createStyledButton("Create Lobby");
        Button joinLobbyButton = createStyledButton("Join Lobby");
        joinLobbyButton.setDisable(true);

        // Enable "Join Lobby" button only when a lobby is selected
        lobbyListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            joinLobbyButton.setDisable(newSelection == null);
        });

        // Button Actions
        createLobbyButton.setOnAction(e -> showCreateLobbyDialog());
        joinLobbyButton.setOnAction(e -> {
            String selectedLobby = lobbyListView.getSelectionModel().getSelectedItem();
            if (selectedLobby != null) {
                System.out.println("Selected Lobby: " + selectedLobby); // Debugging
                if (selectedLobby.startsWith("[Private]")) {
                    showPasswordDialog(); // Show the password dialog for private lobbies
                } else {
                    startGame(primaryStage); // Start the game for public lobbies
                }
            } else {
                System.out.println("No lobby selected."); // Debugging message
            }
        });

        HBox buttonBox = new HBox(20, createLobbyButton, joinLobbyButton);
        buttonBox.setAlignment(Pos.CENTER);

        mainLayout.getChildren().addAll(lobbyLabel, lobbyListView, buttonBox);

        overlayPane = new StackPane(); // For popups
        overlayPane.setMouseTransparent(true); // By default, passes events to below layers
        overlayPane.setPickOnBounds(false); // Only interact with visible children

        StackPane root = new StackPane(backgroundView, mainLayout, overlayPane);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    private void showCreateLobbyDialog() {
        // Create the dimmed overlay
        Pane dimBackground = new Pane();
        dimBackground.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        // Bind to the scene's width and height to ensure proper resizing
        Scene scene = overlayPane.getScene();
        if (scene != null) {
            dimBackground.prefWidthProperty().bind(scene.widthProperty());
            dimBackground.prefHeightProperty().bind(scene.heightProperty());
        } else {
            dimBackground.setPrefSize(overlayPane.getWidth(), overlayPane.getHeight());
        }

        VBox popup = new VBox(10);
        popup.setAlignment(Pos.CENTER);
        popup.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-padding: 20; -fx-background-radius: 10;");
        popup.setMaxWidth(300);

        Label titleLabel = new Label("Create Lobby");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

        TextField lobbyNameField = new TextField();
        lobbyNameField.setPromptText("Lobby Name");
        lobbyNameField.setStyle("-fx-font-size: 16px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (optional)");
        passwordField.setStyle("-fx-font-size: 16px;");

        Button createButton = new Button("Create");
        createButton.setStyle("-fx-font-size: 16px; -fx-background-color: #555; -fx-text-fill: white;");
        createButton.setOnAction(e -> {
            String lobbyType = passwordField.getText().isEmpty() ? "[Public] " : "[Private] ";
            lobbies.add(lobbyType + lobbyNameField.getText());
            lobbyListView.getItems().setAll(lobbies);
            overlayPane.getChildren().removeAll(dimBackground, popup);
            overlayPane.setMouseTransparent(true); // Re-enable interactions
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-font-size: 16px; -fx-background-color: #555; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> {
            overlayPane.getChildren().removeAll(dimBackground, popup);
            overlayPane.setMouseTransparent(true); // Re-enable interactions
        });

        HBox buttonBox = new HBox(10, createButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        popup.getChildren().addAll(titleLabel, lobbyNameField, passwordField, buttonBox);

        overlayPane.getChildren().addAll(dimBackground, popup);
        overlayPane.setMouseTransparent(false); // Block interactions below the popup
    }

    private void showPasswordDialog() {
        // Create the dimmed overlay
        Pane dimBackground = new Pane();
        dimBackground.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        // Bind to the scene's width and height to ensure proper resizing
        Scene scene = overlayPane.getScene();
        if (scene != null) {
            dimBackground.prefWidthProperty().bind(scene.widthProperty());
            dimBackground.prefHeightProperty().bind(scene.heightProperty());
        } else {
            dimBackground.setPrefSize(overlayPane.getWidth(), overlayPane.getHeight());
        }

        VBox popup = new VBox(10);
        popup.setAlignment(Pos.CENTER);
        popup.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-padding: 20; -fx-background-radius: 10;");
        popup.setMaxWidth(300);

        Label titleLabel = new Label("Enter Password");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 16px;");

        Button joinButton = new Button("Join");
        joinButton.setStyle("-fx-font-size: 16px; -fx-background-color: #555; -fx-text-fill: white;");
        joinButton.setOnAction(e -> {
            // Placeholder for password validation
            System.out.println("Password entered: " + passwordField.getText());
            overlayPane.getChildren().removeAll(dimBackground, popup); // Remove popup and dimmed background
            overlayPane.setMouseTransparent(true); // Re-enable interactions
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-font-size: 16px; -fx-background-color: #555; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> {
            overlayPane.getChildren().removeAll(dimBackground, popup);
            overlayPane.setMouseTransparent(true); // Re-enable interactions
        });

        HBox buttonBox = new HBox(10, joinButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        popup.getChildren().addAll(titleLabel, passwordField, buttonBox);

        overlayPane.getChildren().addAll(dimBackground, popup);
        overlayPane.setMouseTransparent(false); // Block interactions below the popup
    }

    private void startGame(Stage primaryStage) {
        System.out.println("Game starting...");
    }

    private Button createStyledButton(String text) {
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
