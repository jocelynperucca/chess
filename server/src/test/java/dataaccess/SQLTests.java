package dataaccess;

import model.*;
import org.junit.jupiter.api.*;
import passoff.server.TestServerFacade;
import server.Server;
import service.*;

import java.sql.SQLException;

public class SQLTests {

    private static TestServerFacade serverFacade;

    private static Server server;

    private static Class<?> databaseManagerClass;

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

    public SQLTests() {
    }

    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
    }

    @BeforeEach
    public void start() throws DataAccessException, SQLException {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();

        registerService = new RegisterService(userDAO, authDAO);
        loginService = new LoginService(userDAO, authDAO);
        logoutService = new LogoutService(authDAO);
        listGamesService = new ListGamesService(authDAO, gameDAO);
        createGameService = new CreateGameService(authDAO, gameDAO);
        joinGameService = new JoinGameService(authDAO,gameDAO);
        clearService = new ClearService(authDAO, gameDAO, userDAO);

        serverFacade.clear();

        RegisterRequest request = new RegisterRequest("Gerald", "geraldean", "gerald@gmail.com");
        registerService.register(request);
        RegisterRequest newRequest = new RegisterRequest("Jocelyn", "jocelynjean", "jocelyn@gmail.com");
        registerService.register(newRequest);

    }

    @Test
    @Order(1)
    @DisplayName("Register Positive")
    public void registerTest() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Jerry", "jocelyn", "jerry@gmail.com");
        RegisterResult result = registerService.register(request);
        Assertions.assertEquals("created", result.message());
    }

    @Test
    @Order(2)
    @DisplayName("Register Negative")
    public void registerNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Jerry", null, "jerry@gmail.com");
        RegisterResult result = registerService.register(request);
        Assertions.assertEquals("Error: bad request", result.message());
    }

    @Test
    @Order(3)
    @DisplayName("Login Test")
    public void loginTest() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("Gerald", "geraldean");
        LoginResult loginResult = loginService.login(loginRequest);
        Assertions.assertEquals("Logged In", loginResult.message());
        Assertions.assertEquals("Gerald", loginResult.username());
    }

    @Test
    @Order(4)
    @DisplayName("Login Negative")
    public void loginNegative() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("Gerald", "geraldean");
        loginService.login(loginRequest);
        LoginRequest loginNewRequest = new LoginRequest("Jocelyn", "jocelynJean");
        LoginResult loginNewResult = loginService.login(loginNewRequest);
        //bad password
        Assertions.assertEquals("Error: unauthorized", loginNewResult.message());
    }

    @Test
    @Order(5)
    @DisplayName("Logout Test")
    public void logoutTest() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("Gerald", "geraldean");
       //LOGIN
        LoginResult result = loginService.login(loginRequest);
        Assertions.assertEquals("Gerald",result.username());
        String logoutAuthToken = result.authToken();
        //TEST LOGOUT
        LogoutResult logoutResult = logoutService.logout(logoutAuthToken);
        Assertions.assertEquals("Logged Out", logoutResult.message());
    }

    @Test
    @Order(6)
    @DisplayName("Logout Negative Test")
    public void logoutNegative() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("Jocelyn", "jocelynjean");
        LoginResult result = loginService.login(loginRequest);
        String logoutAuthToken = "fj78-asd7-jkie6-8906y";
        //Bad authToken
        LogoutResult logoutResult = logoutService.logout(logoutAuthToken);
        Assertions.assertEquals("Error: unauthorized", logoutResult.message());
    }

}


