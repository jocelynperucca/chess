package Handler;

import dataaccess.DataAccessException;
import model.*;
import service.ListGamesService;
import service.LogoutService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {

    private final ListGamesService listGamesService;
    private final Gson gson = new Gson(); // Initialize Gson here

    public ListGamesHandler(ListGamesService listGamesService) {
        this.listGamesService = listGamesService;
    }

    @Override
    public Object handle(Request sparkRequest, Response response) throws DataAccessException {

        ListGamesResult listGamesResult;
        try {
            String authToken = sparkRequest.headers("Authorization");
            listGamesResult = listGamesService.listGames(authToken);
            if (listGamesResult.listGamesMessage().contains("Listed Games")) {
                response.status(200);
            } else if (listGamesResult.listGamesMessage().contains("Unauthorized")) {
                response.status(401);
            }


        } catch (DataAccessException e) {
            listGamesResult = new ListGamesResult(null, e.getMessage());
            response.status(500);

            //throw new RuntimeException(e);

        }

        response.type("application/json");
        return gson.toJson(listGamesResult);

        //return logoutUser(authToken, response); // Call the refactored method
    }
}
