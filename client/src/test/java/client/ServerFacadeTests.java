package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import spark.Response;
import ui.ChessClient;
import ui.ResponseException;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        ChessClient chessClient = new ChessClient("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Positive Register")
    public void registerPostive() throws Exception {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(userData);
        //ensure authToken is actually getting made
        Assertions.assertNotNull(authData.getAuthToken());
    }

    @Test
    @DisplayName("Negative Register")
    public void registerNegative() {
        UserData userData = new UserData(null, "password", "p1@email.com");

        //ensure Exception is thrown with bad information
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register(userData);
        });
    }

    @Test
    @DisplayName("Login Positive")
    public void loginPositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        facade.register(userData);
        AuthData authData = facade.login(userData);
        //ensure authToken from login is made
        Assertions.assertNotNull(authData.getAuthToken());
    }

    @Test
    @DisplayName("Login Negative")
    public void loginNegative() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");

        //try to log in without registering
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login(userData);
        });
    }

    @Test
    @DisplayName("Create Game Positive")
    public void createGamePositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        facade.register(userData);
        AuthData authData = facade.login(userData);
        GameData gameData = facade.createGame("test", authData);

        //check gameData was actually returned
        Assertions.assertNotNull(gameData);
    }

    @Test
    @DisplayName("Create Game Negative")
    public void createGameNegative() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        facade.register(userData);
        AuthData authData = facade.login(userData);

        //check gameData was actually returned
        Assertions.assertThrows(Exception.class, () -> {
            facade.createGame(null, authData);
        });
    }

    @Test
    @DisplayName("Join Game Positive")
    public void joinGamePositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        facade.register(userData);
        AuthData authData = facade.login(userData);
        GameData gameData = facade.createGame("test", authData);
        ChessGame chessGame = facade.joinGame("white", gameData.getGameID(), authData);

       //ensure chessGame was actually created and joinGame went through
        Assertions.assertNotNull(chessGame);
    }

    @Test
    @DisplayName("Join Game Negative")
    public void joinGameNegative() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        facade.register(userData);
        AuthData authData = facade.login(userData);
        GameData gameData = facade.createGame("test", authData);

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.joinGame("purple", gameData.getGameID(), authData);
        });

    }

}
