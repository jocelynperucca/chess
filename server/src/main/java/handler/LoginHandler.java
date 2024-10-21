package handler;

import dataaccess.DataAccessException;
import model.LoginRequest;
import model.LoginResult;
import service.LoginService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

// Handles HTTP requests for user login, invoking LoginService and setting the response status.
public class LoginHandler implements Route {

    private final LoginService loginService;
    private final Gson gson = new Gson();

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        return loginUser(request, response);
    }

    private String loginUser(Request req, Response res) throws DataAccessException {

        // Parse the request body to RegisterRequest object
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);

        // Call the register method in the service layer to get the result
        LoginResult result = loginService.login(request);

        // Set the appropriate HTTP status based on the result message
        if (result.message().equals("Logged In")) {
            res.status(200);
        } else if (result.message().contains("unauthorized")) {
            res.status(401); // Username already taken
        } else {
            res.status(500);
        }

        // Return the RegisterResult as a JSON string
        return gson.toJson(result);
    }
}
