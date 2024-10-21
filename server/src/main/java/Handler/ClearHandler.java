package Handler;

import dataaccess.DataAccessException;
import model.ClearResult;
import service.ClearService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

//RETURNS RESPONSE STATUS BASED ON CLEAR MESSAGE
public class ClearHandler implements Route {

    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public Object handle(Request Request, Response response){

        //INITIALIZE CLEAR RESULT
        ClearResult clearResult;

        //SET RESULT BASED ON MESSAGE GIVEN IN HANDLER
        try {
            clearResult = clearService.clear();
            if (clearResult.message().contains("Cleared")) {
                response.status(200);
            }

        } catch (DataAccessException e) {
            clearResult = new ClearResult(e.getMessage());
            response.status(500);
        }

        response.type("application/json");
        return gson.toJson(clearResult);
    }
}
