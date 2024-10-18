package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.RegisterRequest;
import model.RegisterResult;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Handler;
import service.LoginService;
import service.LogoutService;
import service.RegisterService;
import Handler.RegisterHandler;
import Handler.LoginHandler;
import Handler.LogoutHandler;
import spark.*;

public class Server {

    //private RegisterService registerService;
    private Gson gson = new Gson();

    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();

    RegisterService registerService = new RegisterService(userDAO, authDAO);
    LoginService loginService = new LoginService(userDAO, authDAO);
    LogoutService logoutService = new LogoutService(authDAO);

    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.post("/user", new RegisterHandler(registerService));
        Spark.post("/session", new LoginHandler(loginService)) ;
        Spark.delete("/session", new LogoutHandler(logoutService));
        Spark.get("/game", new ListGameHandler(listGameService));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }




}
