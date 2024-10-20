package service;

import Handler.LoginHandler;
import dataaccess.*;
import model.LoginRequest;
import model.LoginResult;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.*;

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


}
