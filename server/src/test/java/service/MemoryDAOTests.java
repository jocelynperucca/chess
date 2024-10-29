package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.Collection;

public class MemoryDAOTests {

    //INITIALIZERS
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    RegisterService registerService;
    LoginService loginService;
    LogoutService logoutService;
    ListGamesService listGamesService;
    CreateGameService createGameService;
    JoinGameService joinGameService;
    ClearService clearService;

    @BeforeEach
    public void start() throws DataAccessException {

        //INITIALIZE AND CLEAR
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        registerService = new RegisterService(userDAO, authDAO);
        loginService = new LoginService(userDAO, authDAO);
        logoutService = new LogoutService(authDAO);
        listGamesService = new ListGamesService(authDAO, gameDAO);
        createGameService = new CreateGameService(authDAO, gameDAO);
        joinGameService = new JoinGameService(authDAO,gameDAO);
        clearService = new ClearService(authDAO, gameDAO, userDAO);

        clearService.clear();
    }

    @Test
    @DisplayName("Register Positive")
    public void registerTest() throws DataAccessException {
        //successful registration
        RegisterRequest request = new RegisterRequest("jocelynp", "jocelyn", "jocelynperucca@gmail.com");
        RegisterResult result = registerService.register(request);
        Assertions.assertEquals("created", result.message());
    }

    @Test
    @DisplayName("Register Negative")
    public void registerNegativeTest() throws DataAccessException {
        //unsuccessful registration because of null username
        RegisterRequest request = new RegisterRequest(null, "jocelyn", "jocelynperucca@gmail.com");
        RegisterResult result = registerService.register(request);
        Assertions.assertEquals("Error: bad request", result.message());
    }

    @Test
    @DisplayName("Login Test")
    public void loginTest() throws DataAccessException {
        //registers user
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));

        //Logs in successful user
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        Assertions.assertEquals("Logged In", result.message());
    }

    @Test
    @DisplayName("Login Negative Test")
    public void loginNegativeTest() throws DataAccessException {
        //tries to log in without registering
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        Assertions.assertEquals("Error: unauthorized", result.message());
    }

    @Test
    @DisplayName("Logout Test")
    public void logoutTest() throws DataAccessException {
        //registers user
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));

        //successful login
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);

        //obtain authToken
        String authToken = result.authToken();

        //use authToken for successful logout
        LogoutResult logoutResult = logoutService.logout(authToken);
        Assertions.assertEquals("Logged Out", logoutResult.message());
    }

    @Test
    @DisplayName("Logout Negative Test")
    public void logoutNegativeTest() throws DataAccessException {
        //registers
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));

        //tries to log out but cannot because has not logged in
        LogoutResult logoutResult = logoutService.logout("badAuth");
        Assertions.assertEquals("Error: unauthorized", logoutResult.message());
    }

    @Test
    @DisplayName("List Games Test")
    public void listGamesTest() throws DataAccessException {
        //registers
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));

        //logs in
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);

        //obtains authToken
        String authToken = result.authToken();

        //lists all games in database with authToken
        ListGamesResult listGamesResult = listGamesService.listGames(authToken);
        Assertions.assertEquals("Listed Games", listGamesResult.message());
    }

    @Test
    @DisplayName("List Games Negative Test")
    public void listGamesNegativeTest() throws DataAccessException {
        //registers user
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));

        //logs in user
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        loginService.login(request);

        //tries to list game with bad authToken
        ListGamesResult listGamesResult = listGamesService.listGames("BLARGH");
        Assertions.assertEquals("Error: unauthorized", listGamesResult.message());
    }

    @Test
    @DisplayName("Create Game Test")
    public void createGameTest() throws DataAccessException {
        //registers user
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));

        //logs in user
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);

        //obtain authToken
        String authToken = result.authToken();

        //create game with authToken
        CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
        CreateGameResult createGameResult = createGameService.createGame(createGameRequest, authToken);
        Assertions.assertEquals("Created Game", createGameResult.message());
    }

    @Test
    @DisplayName("Create Game Negative Test")
    public void createGameNegativeTest() throws DataAccessException {
        //registers user
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));

        //logs in user
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        loginService.login(request);

        //tries to create a game with a bad authToken
        CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
        CreateGameResult createGameResult = createGameService.createGame(createGameRequest, "badAuth");
        Assertions.assertEquals("Error: unauthorized", createGameResult.message());
    }

    @Test
    @DisplayName("Join Game Test")
    public void joinGameTest() throws DataAccessException {
        //registers user
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));

        //logs in user
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);

        //obtains authToken from log in
        String authToken = result.authToken();

        //successfully create a game
        CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
        CreateGameResult createGameResult = createGameService.createGame(createGameRequest, authToken);

        //obtain gameID from successful result
        int gameID = createGameResult.gameID();

        //successfully join game with game ID
        JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK" , gameID);
        JoinGameResult joinGameResult = joinGameService.joinGame(joinGameRequest, authToken);
        Assertions.assertEquals("Joined Game", joinGameResult.message());
    }

    @Test
    @DisplayName("Join Game Negative Test")
    public void joinGameNegativeTest() throws DataAccessException {
        //registers user
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));

        //logs in user
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);

        //obtains authToken from login result
        String authToken = result.authToken();

        //creates a game
        CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
        CreateGameResult createGameResult = createGameService.createGame(createGameRequest, authToken);

        //obtain gameID from createGame result
        int gameID = createGameResult.gameID();

        //tries to join game with invalid color choice
        JoinGameRequest joinGameRequest = new JoinGameRequest("Purple" , gameID);
        JoinGameResult joinGameResult = joinGameService.joinGame(joinGameRequest, authToken);
        Assertions.assertEquals("Error: bad request", joinGameResult.message());
    }

    @Test
    @DisplayName("Clear Game Test")
    public void clearGameTest() throws DataAccessException {
        //registers multiple users
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));
        registerService.register(new RegisterRequest("jerry", "titus", "jerrydeantitus@gmail.com"));

        //logins in one user
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);

        //obtains authToken from login result
        String authToken = result.authToken();

        //CLEARS EVERYTHING IN DATABASES
        clearService.clear();

        //checks if all games are gone
        Collection<GameData> listGames = gameDAO.listGames();
        Assertions.assertEquals(0, listGames.size());

        //checks if users are gone
        Assertions.assertNull(userDAO.getUser("jocelyn"));

        //checks if authTokens are gone
        Assertions.assertNull(authDAO.getAuthToken(authToken));
    }
}
