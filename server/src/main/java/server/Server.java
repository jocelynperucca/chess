package server;

import handler.*;
import dataaccess.*;
import service.*;
import spark.*;

import java.sql.SQLException;

public class Server {

    //INITIALIZE ALL CLASSES AND VARIABLES
//    UserDAO userDAO = new MemoryUserDAO();
//    AuthDAO authDAO = new MemoryAuthDAO();
//    GameDAO gameDAO = new MemoryGameDAO();

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

//    RegisterService registerService = new RegisterService(userDAO, authDAO);
//    LoginService loginService = new LoginService(userDAO, authDAO);
//    LogoutService logoutService = new LogoutService(authDAO);
//    ListGamesService listGamesService = new ListGamesService(authDAO, gameDAO);
//    CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
//    JoinGameService joinGameService = new JoinGameService(authDAO,gameDAO);
//    ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);

    //RUN SERVER AND ENDPOINTS
    public int run(int desiredPort) throws DataAccessException {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");


        try {
            DatabaseManager.createDatabase();
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        RegisterService registerService = new RegisterService(userDAO, authDAO);
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        ListGamesService listGamesService = new ListGamesService(authDAO, gameDAO);
        CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
        JoinGameService joinGameService = new JoinGameService(authDAO,gameDAO);
        ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);


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
