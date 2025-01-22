package com.cof.okhttp;

import javafx.application.Platform;
import okhttp3.*;

import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Okhttp {

    private OggettoCondiviso og = new OggettoCondiviso();
    private static final String SERVER_URL_1="http://chamberoffate.altervista.org/lobbyCreator.php";
    private static final String SERVER_URL_2="http://chamberoffate.altervista.org/gestoreLobby.php";
    private static String LOBBY_NAME="";
    private OkHttpClient client = new OkHttpClient();

    public boolean createLobby(String lobbyName) {

        /*
        * Needed for this call:
        *
        * - lobbyName -> given from the input of the method
        * - azione -> given inside the method (create)
        * - server url used -> SERVER_URL_1
        *
        */

        LOBBY_NAME=lobbyName;

        //Create the lobby using url 1--------------------------------------------------------------------
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
        //Create the lobby using url 1--------------------------------------------------------------------
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //Create the lobby using url 2--------------------------------------------------------------------
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
        //Create the lobby using url 2--------------------------------------------------------------------

        return true;
    }

    public boolean joinLobby(String lobbyName) {

        String action = "join";
        LOBBY_NAME=lobbyName;

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

    public void getLobbyListAndUpdateUI(Consumer<ArrayList<String>> callback) {
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
                    ArrayList<String> lobbies = parseLobbies(responseBody);
                    Platform.runLater(() -> callback.accept(lobbies));
                } else {
                    System.out.println("Errore del server: " + response.message());
                }
            } catch (IOException ex) {
                System.out.println("Errore di connessione: " + ex.getMessage());
            }
        }).start();
    }

    private ArrayList<String> parseLobbies(String responseBody) {
        // Supponiamo che la risposta sia separata da righe
        ArrayList<String> list = new ArrayList<>();
        // Pattern per trovare le parole tra virgolette
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher matcher = pattern.matcher(responseBody);

        while (matcher.find()) {
            list.add(matcher.group(1)); // Aggiunge il contenuto tra le virgolette
        }

        return list;
    }

    //NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA
    public void getMazzo(String playerKey, String nomeFile) {
        String instruction = "getMazzo";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("playerKey", playerKey)
                .add("nomeFile",nomeFile /*LOBBY_NAME*/)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_2)
                .post(formBody)
                .build();


        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();

                // Mostra la risposta nella text area
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println("Risposta del server: \n" + responseBody);
                    og.setString(responseBody);
                } else {
                    System.out.println("Errore del server: \n" + response.message());
                }
            } catch (IOException ex) {
                // Mostra un errore in caso di problemi con la connessione
                System.out.println("Errore di connessione: \n" + ex.getMessage());
            }
        }).start();

        String string=og.getString();
        System.out.println(string);
        ArrayList<String> result = new ArrayList<>();
        // Espressione regolare per trovare contenuti tra virgolette
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher matcher;
        matcher = pattern.matcher(string);

        // Cerca tutte le occorrenze
        while (matcher.find()) {
            // Aggiungi la sottostringa trovata alla lista
            result.add(matcher.group(1));
        }
        for (String s: result){
            System.out.println(s);
        }
    }
    //NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA - NON FUNZIONA

    public void addCarta(String carta, String playerKey){
        String instruction = "addCarta";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("playerKey", playerKey)
                .add("nomeFile",LOBBY_NAME)
                .add("carta", carta)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_2)
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
    }

    public void decreaseHealth(String playerKey){
        String instruction = "decreaseHealth";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("playerKey", playerKey)
                .add("nomeFile",LOBBY_NAME)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_2)
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
    }

    public String getHealth(String playerKey){
        String instruction = "getHealth";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("playerKey", playerKey)
                .add("nomeFile","bozo")
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_2)
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();

            // Mostra la risposta nella text area
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                return  responseBody;
                //System.out.println("Risposta del server: \n" + responseBody);
            } else {
                System.out.println("Errore del server: \n" + response.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }
        return null;
    }
}
