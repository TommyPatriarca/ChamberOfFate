package com.cof.okhttp;

import okhttp3.*;

import java.io.IOException;


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

        //Request for first url---------------------------------------------------------------
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
        //Request for first url---------------------------------------------------------------

        //Request for second url---------------------------------------------------------------
        RequestBody formBody2 = new FormBody.Builder()
                .add("instruction", action)
                .add("nomeFile", lobbyName)
                .build();

        Request request2 = new Request.Builder()
                .url(SERVER_URL_2)
                .post(formBody2)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request2).execute();

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
        //Request for second url---------------------------------------------------------------
        return true;
    }
}
