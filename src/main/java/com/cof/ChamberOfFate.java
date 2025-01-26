package com.cof;

import com.cof.okhttp.Okhttp;
import com.cof.ui.PcLoadingScreen;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ChamberOfFate extends Application {
    /**
     * Fa iniziare il gioco
     * @param primaryStage la finestra principale
     */
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