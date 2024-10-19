package Handler;

import dataaccess.DataAccessException;
import model.JoinGameRequest;
import model.JoinGameResult;
import model.LoginRequest;
import model.LoginResult;
import service.JoinGameService;
import service.LoginService;
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
        // Call the register method in the service layer to get the result
        JoinGameResult result = joinGameService.joinGame(joinGameRequest);

        // Set the appropriate HTTP status based on the result message
        if (result.joinGameMessage().equals("Joined Game")) {
            res.status(200); // Success
        } else if (result.joinGameMessage().contains("Unauthorized")) {
            res.status(401);
        } else if (result.joinGameMessage().contains("bad request")) {
            res.status(400); // Bad request (missing or invalid fields)
        } else if (result.joinGameMessage().contains("already taken")) {
            res.status(403);
        } else {
            res.status(500); // Internal server error or unexpected case
        }

        // Return the RegisterResult as a JSON string
        return gson.toJson(result);
    }
}
