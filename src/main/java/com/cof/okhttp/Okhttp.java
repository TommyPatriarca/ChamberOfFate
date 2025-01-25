package com.cof.okhttp;

import com.google.gson.Gson;
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

    private static final String SERVER_URL_1="http://chamberoffate.altervista.org/lobbyCreator-DO-NOT-DELETE.php";
    private static final String SERVER_URL_2="http://chamberoffate.altervista.org/gestoreLobby-DO-NOT-DELETE.php";
    private static String LOBBY_NAME;
    private OkHttpClient client = new OkHttpClient();

    //CREATE AND JOIN =================================================================================================
    //CREATE AND JOIN =================================================================================================
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

        //Create the lobby using url 1
        String action = "create";

        RequestBody formBody = new FormBody.Builder()
                .add("azione", action)
                .add("lobbyName", lobbyName)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_1)
                .post(formBody)
                .build();

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
        //Create the lobby using url 1

        /*
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        */


        //Create the lobby using url 2
        RequestBody formBody2 = new FormBody.Builder()
                .add("instruction", action)
                .add("nomeFile", lobbyName)
                .build();

        Request request2 = new Request.Builder()
                .url(SERVER_URL_2)
                .post(formBody2)
                .build();

        try {
            Response response2 = client.newCall(request2).execute();

            // Mostra la risposta nella text area
            if (response2.isSuccessful()) {
                String responseBody = response2.body().string();
                System.out.println("Risposta del server: \n" + responseBody);
            } else {
                System.out.println("Errore del server: \n" + response2.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }
        //Create the lobby using url 2

        return true;
    }

    public boolean joinLobby(String lobbyName) {

        String action = "join";
        LOBBY_NAME=lobbyName;

        //Request for first url
        RequestBody formBody = new FormBody.Builder()
                .add("azione", action)
                .add("lobbyName", lobbyName)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_1)
                .post(formBody)
                .build();

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
        //Request for first url

        //Request for second url
        RequestBody formBody2 = new FormBody.Builder()
                .add("instruction", action)
                .add("nomeFile", lobbyName)
                .build();

        Request request2 = new Request.Builder()
                .url(SERVER_URL_2)
                .post(formBody2)
                .build();

        try {
            Response response2 = client.newCall(request2).execute();

            // Mostra la risposta nella text area
            if (response2.isSuccessful()) {
                String responseBody = response2.body().string();
                System.out.println("Risposta del server: \n" + responseBody);
            } else {
                System.out.println("Errore del server: \n" + response2.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }
        //Request for second url
        return true;
    }
    //CREATE AND JOIN =================================================================================================
    //CREATE AND JOIN =================================================================================================

    //GETTERS =========================================================================================================
    //GETTERS =========================================================================================================
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

    public ArrayList<String> getLobbyList() { // ! PER IL CAPITANO !
        String instruction = "getLista";

        RequestBody formBody = new FormBody.Builder()
                .add("azione", instruction)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_1)
                .post(formBody)
                .build();

        String resopnseList="";

        try {
            Response response = client.newCall(request).execute();

            // Mostra la risposta nella text area
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                resopnseList=responseBody;
            } else {
                System.out.println("Errore del server: \n" + response.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }

        ArrayList<String> result = new ArrayList<>();
        // Espressione regolare per trovare contenuti tra virgolette
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher matcher;
        matcher = pattern.matcher(resopnseList);

        // Cerca tutte le occorrenze
        while (matcher.find()) {
            // Aggiungi la sottostringa trovata alla lista
            result.add(matcher.group(1));
        }

        return result;
    }

    public ArrayList<String> getMazzo(String playerKey) {
        String instruction = "getMazzo";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("playerKey", playerKey)
                .add("nomeFile",LOBBY_NAME)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_2)
                .post(formBody)
                .build();

        String resopnseList="";

        try {
            Response response = client.newCall(request).execute();

            // Mostra la risposta nella text area
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                resopnseList=responseBody;
            } else {
                System.out.println("Errore del server: \n" + response.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }

        ArrayList<String> result = new ArrayList<>();
        // Espressione regolare per trovare contenuti tra virgolette
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher matcher;
        matcher = pattern.matcher(resopnseList);

        // Cerca tutte le occorrenze
        while (matcher.find()) {
            // Aggiungi la sottostringa trovata alla lista
            result.add(matcher.group(1));
        }

        return result;
    }

    public String getAzione(String playerKey){
        String instruction = "getAzione";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("playerKey", playerKey)
                .add("nomeFile",LOBBY_NAME)
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

    public String getHealth(String playerKey){
        String instruction = "getHealth";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("playerKey", playerKey)
                .add("nomeFile",LOBBY_NAME)
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

    public String countPlayers(){
        String instruction = "countPlayers";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("nomeFile",LOBBY_NAME)
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

    public ArrayList<String> getDeck() {
        String instruction = "getDeck";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("nomeFile",LOBBY_NAME)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_2)
                .post(formBody)
                .build();

        String resopnseList="";

        try {
            Response response = client.newCall(request).execute();

            // Mostra la risposta nella text area
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                resopnseList=responseBody;
            } else {
                System.out.println("Errore del server: \n" + response.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }

        ArrayList<String> result = new ArrayList<>();
        // Espressione regolare per trovare contenuti tra virgolette
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher matcher;
        matcher = pattern.matcher(resopnseList);

        // Cerca tutte le occorrenze
        while (matcher.find()) {
            // Aggiungi la sottostringa trovata alla lista
            result.add(matcher.group(1));
        }

        return result;
    }
    //GETTERS =========================================================================================================
    //GETTERS =========================================================================================================

    //SETTERS AND MODIFIERS ===========================================================================================
    //SETTERS AND MODIFIERS ===========================================================================================

    public void setDeck(){
        String instruction = "deckGenerator";
        ArrayList<String> test=new ArrayList<>();
        test.add("nigga1");
        test.add("nigga2");
        String deckJson = new Gson().toJson(test);

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("deck", deckJson)
                .add("nomeFile",LOBBY_NAME)
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
                System.out.println("Risposta del server: \n" + responseBody);
            } else {
                System.out.println("Errore del server: \n" + response.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }
    }

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
    }

    public void setGameStarted() {
        String instruction = "startGame";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("nomeFile", LOBBY_NAME)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL_2)
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                System.out.println("Partita avviata con successo!");
            } else {
                System.out.println("Errore nell'avvio della partita: " + response.message());
            }
        } catch (IOException ex) {
            System.out.println("Errore di connessione: " + ex.getMessage());
        }
    }


    public void setAzioneDraw(String playerKey){
        String instruction = "setAzione";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("playerKey", playerKey)
                .add("nomeFile",LOBBY_NAME)
                .add("azione", "draw")
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
                System.out.println("Risposta del server: \n" + responseBody);
            } else {
                System.out.println("Errore del server: \n" + response.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }
    }

    public void setAzioneStand(String playerKey){
        String instruction = "setAzione";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("playerKey", playerKey)
                .add("nomeFile",LOBBY_NAME)
                .add("azione", "stand")
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
                System.out.println("Risposta del server: \n" + responseBody);
            } else {
                System.out.println("Errore del server: \n" + response.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }
    }

    public void clearAzione(String playerKey){
        String instruction = "clearAzione";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("playerKey", playerKey)
                .add("nomeFile",LOBBY_NAME)
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
                System.out.println("Risposta del server: \n" + responseBody);
            } else {
                System.out.println("Errore del server: \n" + response.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }
    }

    public void clearMazzo(String playerKey){
        String instruction = "clearMazzo";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("playerKey", playerKey)
                .add("nomeFile",LOBBY_NAME)
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
                System.out.println("Risposta del server: \n" + responseBody);
            } else {
                System.out.println("Errore del server: \n" + response.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }
    }
    //SETTERS AND MODIFIERS ===========================================================================================
    //SETTERS AND MODIFIERS ===========================================================================================

    //DELETE LOBBY ====================================================================================================
    //DELETE LOBBY ====================================================================================================
    public void deleteLobby(){
        String instruction = "deleteLobby";

        RequestBody formBody = new FormBody.Builder()
                .add("instruction", instruction)
                .add("nomeFile",LOBBY_NAME)
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
                System.out.println("Risposta del server: \n" + responseBody);
            } else {
                System.out.println("Errore del server: \n" + response.message());
            }
        } catch (IOException ex) {
            // Mostra un errore in caso di problemi con la connessione
            System.out.println("Errore di connessione: \n" + ex.getMessage());
        }
    }
    //DELETE LOBBY ====================================================================================================
    //DELETE LOBBY ====================================================================================================
}
