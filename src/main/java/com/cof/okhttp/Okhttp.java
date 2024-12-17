package com.cof.okhttp;

import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class Okhttp {

    private static final String SERVER_URL="http://chamberoffate.altervista.org/WS.php";
    private OkHttpClient client = new OkHttpClient();
    private String id=null;
    private String password=null;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    private boolean getIds(){
        //get the ids from json file to see if the id given to a new game is already used
        return true;
    }

    public boolean createLobby(String lobbyName) {

        String action = "create";
        int id = (int) (Math.random() * 1000);

        RequestBody formBody = new FormBody.Builder()
                .add("azione", action)
                .add("lobbyName", lobbyName)
                .add("id", String.valueOf(id))
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL)
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

    public void getLobbies(Consumer<List<String>> callback) {
        Request request = new Request.Builder()
                .url(SERVER_URL + "?azione=getLobbies")
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    List<String> lobbies = new Gson().fromJson(responseBody, new TypeToken<List<String>>() {}.getType());
                    callback.accept(lobbies);
                } else {
                    System.out.println("Errore del server: \n" + response.message());
                }
            } catch (IOException ex) {
                System.out.println("Errore di connessione: \n" + ex.getMessage());
            }
        }).start();
    }

}
