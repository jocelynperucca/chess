package server;

import Handler.*;
import dataaccess.*;
import service.*;
import spark.*;

public class Server {

    //INITIALIZE ALL CLASSES AND VARIABLES
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();

    RegisterService registerService = new RegisterService(userDAO, authDAO);
    LoginService loginService = new LoginService(userDAO, authDAO);
    LogoutService logoutService = new LogoutService(authDAO);
    ListGamesService listGamesService = new ListGamesService(authDAO, gameDAO);
    CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
    JoinGameService joinGameService = new JoinGameService(authDAO,gameDAO);
    ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);

    //RUN SERVER AND ENDPOINTS
    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        //ENDPOINTS
        Spark.delete("/db", new ClearHandler(clearService));
        Spark.post("/user", new RegisterHandler(registerService));
        Spark.post("/session", new LoginHandler(loginService)) ;
        Spark.delete("/session", new LogoutHandler(logoutService));
        Spark.get("/game", new ListGamesHandler(listGamesService));
        Spark.post("/game", new CreateGameHandler(createGameService));
        Spark.put("/game", new JoinGameHandler(joinGameService));

        //START SERVER
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }




}
