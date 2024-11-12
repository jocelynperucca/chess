package server;

import handler.*;
import dataaccess.*;
import service.*;
import spark.*;

import java.sql.SQLException;

public class Server {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    //RUN SERVER AND ENDPOINTS
    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        //implements SQL DAO's as the databases
        try {
            DatabaseManager.createDatabase();
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        //initialize all services with SQL DAO's
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

    public void clearData() {
        try {
            ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
            clearService.clear(); // Clear all data from database tables
        } catch (DataAccessException e) {
            System.err.println("Failed to clear database: " + e.getMessage());
        }
    }

}
