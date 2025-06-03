package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.LoginRequest;
import model.UserData;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;


public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData data) throws Exception {
        var path = "/user";
        return this.makeRequest("POST", path, data, AuthData.class, null);
    }

    public AuthData login(String username, String password) throws Exception {
        var path = "/session";
        var request = new LoginRequest(username, password);
        return this.makeRequest("POST", path, request, AuthData.class, null);
    }

    public void logout(String auth) throws Exception {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, auth);
    }

    public Collection<GameData> listGames(String auth) throws Exception {
        var path = "/game";
        record listGameResponse(Collection<GameData> games) {}
        var response = this.makeRequest("GET", path, null, listGameResponse.class, auth);
        return response.games();
    }

    public GameData createGame(String auth, String gameName) throws Exception {
        var path = "/game";
        record createGameRequest(String gameName) {}
        var request = new createGameRequest(gameName);
        return this.makeRequest("POST", path, request, GameData.class, auth);
    }

    public void joinGame(String auth, String gameID, ChessGame.TeamColor color) throws Exception {
        var path = "/game";
        record joinGameRequest(String gameID, ChessGame.TeamColor playerColor) {}
        var request = new joinGameRequest(gameID, color);
        this.makeRequest("PUT", path, request, null, auth);
    }

    public void clear() throws Exception {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String auth)
            throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (auth != null) {
                http.setRequestProperty("authorization", auth);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw ex;
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw new Exception("Error");
                }
            }

            throw new Exception("Error");
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
