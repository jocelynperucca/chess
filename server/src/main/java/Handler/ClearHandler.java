package Handler;

import dataaccess.DataAccessException;
import model.*;
import service.ClearService;
import service.LogoutService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {

    private final ClearService clearService;
    private final Gson gson = new Gson(); // Initialize Gson here

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public Object handle(Request Request, Response response) throws DataAccessException {

        ClearResult clearResult;
        try {
            String authToken = Request.headers("Authorization");
            clearResult = clearService.clear();
            if (clearResult.clearMessage().contains("Cleared")) {
                response.status(200);
            } else if (clearResult.clearMessage().contains("Unauthorized")) {
                response.status(401);
            }


        } catch (DataAccessException e) {
            clearResult = new ClearResult(e.getMessage());
            response.status(500);

        }

        response.type("application/json");
        return gson.toJson(clearResult);

    }
}
