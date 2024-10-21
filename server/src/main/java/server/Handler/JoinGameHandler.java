package server.Handler;

import dataaccess.DataAccessException;
import requestsAndResults.JoinGameRequest;
import requestsAndResults.JoinGameResult;
import service.JoinGameService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;


public class JoinGameHandler implements Route {

    private final JoinGameService joinGameService;
    private final Gson gson = new Gson(); // Initialize Gson here

    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        return joinGame(request, response); // Call the refactored method
    }

    private String joinGame(Request req, Response res) throws DataAccessException {
        // Parse the request body to RegisterRequest object
        JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);

        String authToken = req.headers("Authorization");

        JoinGameResult result = joinGameService.joinGame(joinGameRequest, authToken);

        // Set the appropriate HTTP status based on the result message
        if (result.message().equals("Joined Game")) {
            res.status(200); // Success
        } else if (result.message().contains("unauthorized")) {
            res.status(401);
        } else if (result.message().contains("bad request")) {
            res.status(400); // Bad request (missing or invalid fields)
        } else if (result.message().contains("already taken")) {
            res.status(403);
        } else {
            res.status(500); // Internal server error or unexpected case
        }

        // Return the RegisterResult as a JSON string
        return gson.toJson(result);
    }
}
