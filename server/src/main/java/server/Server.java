package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.RegisterRequest;
import model.RegisterResult;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Handler;
import service.RegisterService;
import Handler.RegisterHandler;
import spark.*;

public class Server {

    //private RegisterService registerService;
    private Gson gson = new Gson();

    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();

    RegisterService registerService = new RegisterService(userDAO, authDAO);

    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //Spark.delete("/db", (request, response) -> "delete db");


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.post("/user", new RegisterHandler(registerService));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }




}
