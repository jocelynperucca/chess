package Handler;

import dataaccess.DataAccessException;
import model.*;
import service.LogoutService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {

    private final LogoutService logoutService;
    private final Gson gson = new Gson(); // Initialize Gson here

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @Override
    public Object handle(Request Request, Response response) throws DataAccessException {

        LogoutResult logoutResult;
        try {
            String authToken = Request.headers("Authorization");
            logoutResult = logoutService.logout(authToken);
            if (logoutResult.message().contains("Logged Out")) {
                response.status(200);
            } else if (logoutResult.message().contains("unauthorized")) {
                response.status(401);
            }


        } catch (DataAccessException e) {
            logoutResult = new  LogoutResult(e.getMessage());
            response.status(500);

            //throw new RuntimeException(e);

        }

        response.type("application/json");
        return gson.toJson(logoutResult);

        //return logoutUser(authToken, response); // Call the refactored method
    }
}
