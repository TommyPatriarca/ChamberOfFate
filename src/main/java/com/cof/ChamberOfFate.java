package com.cof;

import com.cof.okhttp.Okhttp;
import com.cof.ui.PcLoadingScreen;
import javafx.application.Application;
import javafx.stage.Stage;

public class ChamberOfFate extends Application {
    @Override
    public void start(Stage primaryStage) {
        Okhttp ok=new Okhttp();
        PcLoadingScreen loadingScreen = new PcLoadingScreen();
        loadingScreen.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}