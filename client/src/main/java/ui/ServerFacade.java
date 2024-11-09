package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userData) throws ResponseException {
        var path = "/user";
        var request = new RegisterRequest(userData.getUsername(), userData.getPassword(), userData.getEmail());
        return this.makeRequest("POST", path, request, AuthData.class, null);
    }

    public AuthData login(UserData userData) throws ResponseException {
        var path = "/session";
        var request = new LoginRequest(userData.getUsername(), userData.getPassword());
        return this.makeRequest("POST", path, request, AuthData.class, null);
    }

    public GameData createGame(String gameName, AuthData authToken) throws ResponseException {
        var path = "/game";
        var request = new CreateGameRequest(gameName);
        return this.makeRequest("POST", path, request, GameData.class, authToken);
    }

    public ChessGame joinGame(String playerColor, int gameID, AuthData authToken) throws ResponseException {
        var path = "/game";
        var request = new JoinGameRequest(playerColor,gameID);
        return this.makeRequest("PUT", path, request, ChessGame.class, authToken);
    }

    public Collection<GameData> listGames(AuthData authToken) throws ResponseException {
        var path = "/game";
        record listGamesResponse(Collection<GameData> games) {}
        var response = this.makeRequest("GET", path, null, listGamesResponse.class, authToken);
        return response.games;
    }

    public void logout(AuthData authData) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, authData);
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);

    }



    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, AuthData auth) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            // Set Authorization header if auth token is provided
            if (auth != null) {
                http.setRequestProperty("Authorization", auth.getAuthToken());
            }

            http.setDoOutput(true);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
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

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
