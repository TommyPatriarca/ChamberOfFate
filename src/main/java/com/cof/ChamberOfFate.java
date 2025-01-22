package com.cof;

import com.cof.okhttp.OggettoCondiviso;
import com.cof.okhttp.Okhttp;
import com.cof.ui.PcLoadingScreen;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.security.auth.callback.Callback;
import java.util.ArrayList;

public class ChamberOfFate extends Application {
    @Override
    public void start(Stage primaryStage) {
        Okhttp ok=new Okhttp();
        System.out.println(ok.getAzione("giocatore1"));
        PcLoadingScreen loadingScreen = new PcLoadingScreen();
        loadingScreen.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}