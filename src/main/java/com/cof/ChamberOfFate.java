package com.cof;

import com.cof.ui.PcLoadingScreen;
import javafx.application.Application;
import javafx.stage.Stage;

public class ChamberOfFate extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        PcLoadingScreen loadingScreen = new PcLoadingScreen();
        loadingScreen.start(primaryStage);
    }
}