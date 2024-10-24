package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.Collection;

public class MemoryDAOTests {

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
        RegisterRequest request = new RegisterRequest("jocelynp", "jocelyn", "jocelynperucca@gmail.com");
        RegisterResult result = registerService.register(request);
        Assertions.assertEquals("created", result.message());
    }

    @Test
    @DisplayName("Register Negative")
    public void registerNegativeTest() throws DataAccessException {
        RegisterRequest request = new RegisterRequest(null, "jocelyn", "jocelynperucca@gmail.com");
        RegisterResult result = registerService.register(request);
        Assertions.assertEquals("Error: bad request", result.message());

    }

    @Test
    @DisplayName("Login Test")
    public void loginTest() throws DataAccessException {
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        Assertions.assertEquals("Logged In", result.message());

    }

    @Test
    @DisplayName("Login Negative Test")
    public void loginNegativeTest() throws DataAccessException {
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        Assertions.assertEquals("Error: unauthorized", result.message());

    }

    @Test
    @DisplayName("Logout Test")
    public void logoutTest() throws DataAccessException {
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        String authToken = result.authToken();
        LogoutResult logoutResult = logoutService.logout(authToken);
        Assertions.assertEquals("Logged Out", logoutResult.message());

    }

    @Test
    @DisplayName("Logout Negative Test")
    public void logoutNegativeTest() throws DataAccessException {
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LogoutResult logoutResult = logoutService.logout("badAuth");
        Assertions.assertEquals("Error: unauthorized", logoutResult.message());

    }

    @Test
    @DisplayName("List Games Test")
    public void listGamesTest() throws DataAccessException {
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        String authToken = result.authToken();
        ListGamesResult listGamesResult = listGamesService.listGames(authToken);
        Assertions.assertEquals("Listed Games", listGamesResult.message());
    }

    @Test
    @DisplayName("List Games Negative Test")
    public void listGamesNegativeTest() throws DataAccessException {
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        ListGamesResult listGamesResult = listGamesService.listGames("BLARGH");
        Assertions.assertEquals("Error: unauthorized", listGamesResult.message());
    }

    @Test
    @DisplayName("Create Game Test")
    public void createGameTest() throws DataAccessException {
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        String authToken = result.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
        CreateGameResult createGameResult = createGameService.createGame(createGameRequest, authToken);
        Assertions.assertEquals("Created Game", createGameResult.message());
    }

    @Test
    @DisplayName("Create Game Negative Test")
    public void createGameNegativeTest() throws DataAccessException {
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
        CreateGameResult createGameResult = createGameService.createGame(createGameRequest, "badAuth");
        Assertions.assertEquals("Error: unauthorized", createGameResult.message());
    }

    @Test
    @DisplayName("Join Game Test")
    public void joinGameTest() throws DataAccessException {
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        String authToken = result.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
        CreateGameResult createGameResult = createGameService.createGame(createGameRequest, authToken);
        int gameID = createGameResult.gameID();
        JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK" , gameID);
        JoinGameResult joinGameResult = joinGameService.joinGame(joinGameRequest, authToken);
        Assertions.assertEquals("Joined Game", joinGameResult.message());
    }

    @Test
    @DisplayName("Join Game Negative Test")
    public void joinGameNegativeTest() throws DataAccessException {
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        String authToken = result.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
        CreateGameResult createGameResult = createGameService.createGame(createGameRequest, authToken);
        int gameID = createGameResult.gameID();
        JoinGameRequest joinGameRequest = new JoinGameRequest("Purple" , gameID);
        JoinGameResult joinGameResult = joinGameService.joinGame(joinGameRequest, authToken);
        Assertions.assertEquals("Error: bad request", joinGameResult.message());
    }

    @Test
    @DisplayName("Clear Game Test")
    public void clearGameTest() throws DataAccessException {
        registerService.register(new RegisterRequest("jocelyn", "perucca", "jocelynperucca@gmail.com"));
        registerService.register(new RegisterRequest("jerry", "titus", "jerrydeantitus@gmail.com"));
        LoginRequest request = new LoginRequest("jocelyn", "perucca");
        LoginResult result = loginService.login(request);
        String authToken = result.authToken();
        CreateGameRequest newCreateGameRequest = new CreateGameRequest("Fun Game");
        clearService.clear();
        Collection<GameData> listGames = gameDAO.listGames();
        Assertions.assertEquals(0, listGames.size());
        Assertions.assertNull(userDAO.getUser("jocelyn"));
        Assertions.assertNull(authDAO.getAuthToken(authToken));

    }


}
