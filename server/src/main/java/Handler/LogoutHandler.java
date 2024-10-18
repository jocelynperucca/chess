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
    public Object handle(Request sparkRequest, Response response) throws DataAccessException {

        LogoutResult logoutResult;
        try {
            String authToken = sparkRequest.headers("Authorization");
            logoutResult = logoutService.logout(authToken);
            if (logoutResult.logoutMessage().contains("Logged Out")) {
                response.status(200);
            } else if (logoutResult.logoutMessage().contains("Unauthorized")) {
                response.status(401);
            }


        } catch (DataAccessException e) {
            logoutResult = new  LogoutResult("Error:" + e.getMessage());
            response.status(500);

            //throw new RuntimeException(e);

        }

        response.type("application/json");
        return gson.toJson(logoutResult);

        //return logoutUser(authToken, response); // Call the refactored method
    }
}

//    private String logoutUser(String authToken, Response res) throws DataAccessException {
//        // Parse the request body to RegisterRequest object
//        //LogoutRequest request = gson.fromJson(req.body(), LogoutRequest.class);
//        // Call the register method in the service layer to get the result
//        LogoutResult result = logoutService.logout(authToken);
//
//         //Set the appropriate HTTP status based on the result message
//        if (result.logoutMessage().equals("Logged Out")) {
//            res.status(200); // Success
//        } else if (result.logoutMessage().contains("unauthorized")) {
//            res.status(401); // Username already taken
//        } else {
//            res.status(500); // Internal server error or unexpected case
//        }
//
//        // Return the RegisterResult as a JSON string
//        return gson.toJson(result);
//    }
//}
