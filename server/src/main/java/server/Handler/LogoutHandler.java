package server.Handler;

import dataaccess.DataAccessException;
import requestsAndResults.LogoutResult;
import service.LogoutService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

// Handles HTTP requests for user logout, invoking LogoutService and setting the appropriate response status.
public class LogoutHandler implements Route {

    private final LogoutService logoutService;
    private final Gson gson = new Gson();

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @Override
    public Object handle(Request request, Response response) {

        //Initialize logoutResult to be updated as depending on the message
        LogoutResult logoutResult;

        // Handle logout request, set appropriate HTTP status based on service result
        try {
            String authToken = request.headers("Authorization");
            logoutResult = logoutService.logout(authToken);
            if (logoutResult.message().contains("Logged Out")) {
                response.status(200);
            } else if (logoutResult.message().contains("unauthorized")) {
                response.status(401);
            }

        } catch (DataAccessException e) {
            logoutResult = new  LogoutResult(e.getMessage());
            response.status(500);
        }

        return gson.toJson(logoutResult);
    }
}
