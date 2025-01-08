package com.cof.okhttp;

import javafx.application.Platform;
import okhttp3.*;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;


public class Okhttp {

    private static final String SERVER_URL_1="http://chamberoffate.altervista.org/lobbyCreator.php";
    private static final String SERVER_URL_2="http://chamberoffate.altervista.org/gestoreLobby.php";
    private OkHttpClient client = new OkHttpClient();
    private String id=null;
    private String password=null;

    public boolean createLobby(String lobbyName) {

        /*
        * Needed for this call:
        *
        * - lobbyName -> given from the input of the method
        * - azione -> given inside the method (create)
        * - server url used -> SERVER_URL_1
        *
        */

        String action = "create";

        RequestBody formBody = new FormBody.Builder()
                .add("azione", action)
                .add("lobbyName", lobbyName)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_1)
                .post(formBody)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();

                // Mostra la risposta nella text area
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println("Risposta del server: \n" + responseBody);
                } else {
                    System.out.println("Errore del server: \n" + response.message());
                }
            } catch (IOException ex) {
                // Mostra un errore in caso di problemi con la connessione
                System.out.println("Errore di connessione: \n" + ex.getMessage());
            }
        }).start();
        return true;
    }

    public boolean getLobbyList() {

        /*
        * Needed for this call:
        *
        * - azione -> given inside the method (getLista)
        * - server url used -> SERVER_URL_1
        *
        */

        String action = "getLista";

        RequestBody formBody = new FormBody.Builder()
                .add("azione", action)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_1)
                .post(formBody)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();

                // Mostra la risposta nella text area
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println("Risposta del server: \n" + responseBody);
                } else {
                    System.out.println("Errore del server: \n" + response.message());
                }
            } catch (IOException ex) {
                // Mostra un errore in caso di problemi con la connessione
                System.out.println("Errore di connessione: \n" + ex.getMessage());
            }
        }).start();
        return true;
    }

    public boolean joinLobby(String lobbyName) {

        String action = "join";

        RequestBody formBody = new FormBody.Builder()
                .add("azione", action)
                .add("lobbyName", lobbyName)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_1)
                .post(formBody)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();

                // Mostra la risposta nella text area
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println("Risposta del server: \n" + responseBody);
                } else {
                    System.out.println("Errore del server: \n" + response.message());
                }
            } catch (IOException ex) {
                // Mostra un errore in caso di problemi con la connessione
                System.out.println("Errore di connessione: \n" + ex.getMessage());
            }
        }).start();
        return true;
    }

    public void getLobbyListAndUpdateUI(Consumer<List<String>> callback) {
        String action = "getLista";

        RequestBody formBody = new FormBody.Builder()
                .add("azione", action)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_1)
                .post(formBody)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println("Lobby ricevute dal server: " + responseBody);

                    // Parso la risposta
                    List<String> lobbies = parseLobbies(responseBody);
                    Platform.runLater(() -> callback.accept(lobbies));
                } else {
                    System.out.println("Errore del server: " + response.message());
                }
            } catch (IOException ex) {
                System.out.println("Errore di connessione: " + ex.getMessage());
            }
        }).start();
    }

    private List<String> parseLobbies(String responseBody) {
        // Supponiamo che la risposta sia separata da righe
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return List.of();
        }
        return List.of(responseBody.split("\n"));
    }

}
