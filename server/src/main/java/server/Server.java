package server;

import service.RegisterService;
import spark.*;

import java.util.logging.Handler;

public class Server {

    private RegisterService registerService;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (request, response) -> "delete db");


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.post("/user", (request, response) -> "register user");

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String delete(Request req, Response res)  throws Exception{
        registerService.delete();
        return "cow";
    }

    private String post(Request req, Response res) throws Exception {
        registerService.post();
        return "cows";

    }
}
