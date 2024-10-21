package server.Handler;

import dataaccess.DataAccessException;
import requestsAndResults.ListGamesResult;
import service.ListGamesService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

// Handles HTTP requests to list available games, invoking ListGamesService and setting the response status.
public class ListGamesHandler implements Route {

    private final ListGamesService listGamesService;
    private final Gson gson = new Gson();

    public ListGamesHandler(ListGamesService listGamesService) {
        this.listGamesService = listGamesService;
    }

    @Override
    public Object handle(Request request, Response response) {

        //Initialize listGamesResult to be changed based on message
        ListGamesResult listGamesResult;

        // Handle request to list games, set appropriate status based on authorization and service result
        try {
            String authToken = request.headers("Authorization");
            listGamesResult = listGamesService.listGames(authToken);

            if (listGamesResult.message().contains("Listed Games")) {
                response.status(200);
            } else if (listGamesResult.message().contains("unauthorized")) {
                response.status(401);
            }

        } catch (DataAccessException e) {
            listGamesResult = new ListGamesResult(null, e.getMessage());
            response.status(500);
        }

        //Return result and status
        return gson.toJson(listGamesResult);
    }
}
