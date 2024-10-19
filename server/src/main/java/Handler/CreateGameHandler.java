package Handler;

import dataaccess.DataAccessException;
import model.*;
import service.CreateGameService;
import service.ListGamesService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {

    private final CreateGameService createGameService;
    private final Gson gson = new Gson(); // Initialize Gson here

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    @Override
    public Object handle(Request sparkRequest, Response response) throws DataAccessException {

        CreateGameResult createGameResult;

        try {
            String authToken = sparkRequest.headers("Authorization");
            createGameResult = createGameService.createGame(authToken);
            if (listGamesResult.listGamesMessage().contains("Listed Games")) {
                response.status(200);
            } else if (listGamesResult.listGamesMessage().contains("Unauthorized")) {
                response.status(401);
            }
        } catch (DataAccessException e) {
            listGamesResult = new ListGamesResult(null, e.getMessage());
            response.status(500);

        }

        response.type("application/json");
        return gson.toJson(listGamesResult);
    }
}
