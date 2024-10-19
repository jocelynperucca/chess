package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.RegisterRequest;
import model.RegisterResult;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Handler;
import service.*;
import Handler.*;
import spark.*;

public class Server {

    //private RegisterService registerService;
    //private Gson gson = new Gson();

    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();

    RegisterService registerService = new RegisterService(userDAO, authDAO);
    LoginService loginService = new LoginService(userDAO, authDAO);
    LogoutService logoutService = new LogoutService(authDAO);
    ListGamesService listGamesService = new ListGamesService(authDAO, gameDAO);
    CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);

    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.post("/user", new RegisterHandler(registerService));
        Spark.post("/session", new LoginHandler(loginService)) ;
        Spark.delete("/session", new LogoutHandler(logoutService));
        Spark.get("/game", new ListGamesHandler(listGamesService));
        Spark.post("/game", new CreateGameHandler(createGameService));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }




}
