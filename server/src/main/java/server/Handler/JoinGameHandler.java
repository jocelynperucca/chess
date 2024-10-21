package server.Handler;

import dataaccess.DataAccessException;
import requestsAndResults.JoinGameRequest;
import requestsAndResults.JoinGameResult;
import service.JoinGameService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

// Handles HTTP requests to join a game, invoking JoinGameService and setting the appropriate response status.
public class JoinGameHandler implements Route {

    private final JoinGameService joinGameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        return joinGame(request, response);
    }

    private String joinGame(Request req, Response res) throws DataAccessException {

        // Parse the request body to joinGameRequest object
        JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
        String authToken = req.headers("Authorization");

        //Initialize result to be read HTTP status
        JoinGameResult result = joinGameService.joinGame(joinGameRequest, authToken);

        // Set the appropriate HTTP status based on the result message
        if (result.message().equals("Joined Game")) {
            res.status(200);
        } else if (result.message().contains("unauthorized")) {
            res.status(401);
        } else if (result.message().contains("bad request")) {
            res.status(400); // Bad request (missing or invalid fields)
        } else if (result.message().contains("already taken")) {
            res.status(403);
        } else {
            res.status(500);
        }

        // Return the result
        return gson.toJson(result);
    }
}
