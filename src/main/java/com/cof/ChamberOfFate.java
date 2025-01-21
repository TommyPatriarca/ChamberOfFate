package com.cof;

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
        ok.getHealth("giocatore1", new ok.Callback() {

            public void onResponse(String response) {
                System.out.println("Risposta del server: \n" + response);
            }

            public void onFailure(Exception e) {
                System.out.println("Errore: \n" + e.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}