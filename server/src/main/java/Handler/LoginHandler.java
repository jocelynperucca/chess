package Handler;

import dataaccess.DataAccessException;
import model.LoginRequest;
import model.LoginResult;
import service.LoginService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;


public class LoginHandler implements Route {

    private final LoginService loginService;
    private final Gson gson = new Gson(); // Initialize Gson here

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Object handle(Request sparkRequest, Response response) throws DataAccessException {
        return loginUser(sparkRequest, response); // Call the refactored method
    }

    private String loginUser(Request req, Response res) throws DataAccessException {
        // Parse the request body to RegisterRequest object
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
        // Call the register method in the service layer to get the result
        LoginResult result = loginService.login(request);

        // Set the appropriate HTTP status based on the result message
        if (result.loginMessage().equals("Logged In")) {
            res.status(200); // Success
        } else if (result.loginMessage().contains("unauthorized")) {
            res.status(401); // Username already taken
        } else if (result.loginMessage().contains("no user found")) {
            res.status(500); // Bad request (missing or invalid fields)
        } else {
            res.status(500); // Internal server error or unexpected case
        }

        // Return the RegisterResult as a JSON string
        return gson.toJson(result);
    }
}
