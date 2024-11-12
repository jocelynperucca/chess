package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ResponseException;
import ui.ServerFacade;
import java.util.Collection;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @Order(1)
    @DisplayName("Positive Register")
    public void registerPostive() throws Exception {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(userData);
        //ensure authToken is actually getting made
        Assertions.assertNotNull(authData.getAuthToken());
    }

    @Test
    @Order(2)
    @DisplayName("Negative Register")
    public void registerNegative() {
        UserData userData = new UserData(null, "password", "p1@email.com");

        //ensure Exception is thrown with bad information
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register(userData);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Login Positive")
    public void loginPositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.login(userData);
        //ensure authToken from login is made
        Assertions.assertNotNull(authData.getAuthToken());
        facade.logout(authData);
    }

    @Test
    @Order(4)
    @DisplayName("Login Negative")
    public void loginNegative() throws ResponseException {
        UserData userData = new UserData(null, "password", "p1@email.com");

        //try to log in without registering
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login(userData);
        });
    }

    @Test
    @Order(5)
    @DisplayName("Create Game Positive")
    public void createGamePositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.login(userData);
        GameData gameData = facade.createGame("test", authData);

        //check gameData was actually returned
        Assertions.assertNotNull(gameData);

        facade.logout(authData);
    }

    @Test
    @Order(6)
    @DisplayName("Create Game Negative")
    public void createGameNegative() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.login(userData);

        //check gameData was actually returned
        Assertions.assertThrows(Exception.class, () -> {
            facade.createGame(null, authData);
        });

        facade.logout(authData);
    }

    @Test
    @Order(7)
    @DisplayName("Join Game Positive")
    public void joinGamePositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.login(userData);
        GameData gameData = facade.createGame("testing", authData);
        ChessGame chessGame = facade.joinGame("white", gameData.getGameID(), authData);

       //ensure chessGame was actually created and joinGame went through
        Assertions.assertNotNull(chessGame);

        facade.logout(authData);
    }

    @Test
    @Order(8)
    @DisplayName("Join Game Negative")
    public void joinGameNegative() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.login(userData);
        GameData gameData = facade.createGame("test", authData);

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.joinGame("purple", gameData.getGameID(), authData);
        });

        facade.logout(authData);
    }

    @Test
    @Order(9)
    @DisplayName("List Games Positive")
    public void listGamesPositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.login(userData);
        facade.createGame("tests", authData);
        facade.createGame("newTest", authData);

        Collection<GameData> gameList = facade.listGames(authData);
        //ensure all games are present in list from before and now
        Assertions.assertEquals(gameList.size(), 5);

        facade.logout(authData);
    }

    @Test
    @Order(10)
    @DisplayName("List Games Negative")
    public void listGamesNegative() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.login(userData);
        AuthData badAuth = new AuthData("username", "badAuth");

        //should not be able to list games with bad authData
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.listGames(badAuth);
        });

        facade.logout(authData);
    }

    @Test
    @Order(11)
    @DisplayName("Logout Postive")
    public void logoutPositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = facade.login(userData);
        facade.logout(authData);

        //This should not let you create a game since you're logged out
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.createGame("test", authData);
        });
    }

    @Test
    @Order(12)
    @DisplayName("Logout Negative")
    public void logoutNegative() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        facade.login(userData);
        AuthData badAuth = new AuthData(userData.getUsername(), "badAuth");

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.logout(badAuth);
        });
    }
}
