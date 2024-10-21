package server;

import dataaccess.*;
import server.Handler.*;
import service.*;
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
    JoinGameService joinGameService = new JoinGameService(authDAO,gameDAO);
    ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);

    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.delete("/db", new ClearHandler(clearService));
        Spark.post("/user", new RegisterHandler(registerService));
        Spark.post("/session", new LoginHandler(loginService)) ;
        Spark.delete("/session", new LogoutHandler(logoutService));
        Spark.get("/game", new ListGamesHandler(listGamesService));
        Spark.post("/game", new CreateGameHandler(createGameService));
        Spark.put("/game", new JoinGameHandler(joinGameService));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }




}
