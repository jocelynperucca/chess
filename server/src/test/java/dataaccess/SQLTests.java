package dataaccess;

import model.RegisterRequest;
import model.RegisterResult;
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

    }

    @Test
    @DisplayName("Register Positive")
    public void registerTest() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Gerald", "jocelyn", "gerald@gmail.com");
        RegisterResult result = registerService.register(request);
        Assertions.assertEquals("created", result.message());
    }

    @Test
    @DisplayName("Register Negative")
    public void registerNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Gerald", null, "gerald@gmail.com");
        RegisterResult result = registerService.register(request);
        Assertions.assertEquals("Error: bad request", result.message());
    }
}


