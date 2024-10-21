package Handler;

import dataaccess.DataAccessException;
import model.CreateGameRequest;
import model.CreateGameResult;
import service.CreateGameService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;


//Uses CreateGameService to get result message and set response status
public class CreateGameHandler implements Route {

    private final CreateGameService createGameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    @Override
    public Object handle(Request request, Response response) {

        //Initialize createGameResult to be changed based on createGameService
        CreateGameResult createGameResult;

        // Handles the game creation request by invoking the service and setting the appropriate response status
        try {
            String authToken = request.headers("Authorization");
            CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
            createGameResult = createGameService.createGame(createGameRequest, authToken);

            if (createGameResult.message().contains("Created Game")) {
                response.status(200);
            } else if (createGameResult.message().contains("unauthorized")) {
                response.status(401);
            } else {
                response.status(400);
            }

            //catch if something doesn't go through
        } catch (DataAccessException e) {
            createGameResult = new CreateGameResult(null, e.getMessage());
            response.status(500);
        }

        //return result and ensure it's json
        response.type("application/json");
        return gson.toJson(createGameResult);
    }
}
