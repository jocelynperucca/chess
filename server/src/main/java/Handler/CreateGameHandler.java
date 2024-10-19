package Handler;

import dataaccess.DataAccessException;
import model.*;
import service.CreateGameService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {

    private final CreateGameService createGameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {

        CreateGameResult createGameResult;

        try {
            String authToken = request.headers("Authorization");
            CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
            createGameResult = createGameService.createGame(createGameRequest, authToken);

            if (createGameResult.message().contains("Created Game")) {
                response.status(200);
            } else if (createGameResult.message().contains("Unauthorized")) {
                response.status(401);
            } else {
                response.status(400);
            }
        } catch (DataAccessException e) {
            createGameResult = new CreateGameResult(null, e.getMessage());
            response.status(500);

        }

        response.type("application/json");
        return gson.toJson(createGameResult);
    }
}
