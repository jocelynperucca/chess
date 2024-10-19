package Handler;

import dataaccess.DataAccessException;
import service.RegisterService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import model.RegisterRequest;
import model.RegisterResult;

public class RegisterHandler implements Route {

    private final RegisterService registerService;
    private final Gson gson = new Gson(); // Initialize Gson here

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public Object handle(Request sparkRequest, Response response) throws DataAccessException {
        return registerUser(sparkRequest, response); // Call the refactored method
    }

    private String registerUser(Request req, Response res) throws DataAccessException {
        // Parse the request body to RegisterRequest object
        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
        // Call the register method in the service layer to get the result
        RegisterResult result = registerService.register(request);

        // Set the appropriate HTTP status based on the result message
        if (result.message().equals("created")) {
            res.status(200); // Success
        } else if (result.message().contains("already taken")) {
            res.status(403); // Username already taken
        } else if (result.message().contains("bad request")) {
            res.status(400); // Bad request (missing or invalid fields)
        } else {
            res.status(500); // Internal server error or unexpected case
        }

        // Return the RegisterResult as a JSON string
        return gson.toJson(result);
    }
}
