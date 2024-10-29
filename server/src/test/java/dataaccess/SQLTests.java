package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import passoff.server.TestServerFacade;
import server.Server;
import service.*;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQLTests {

    private static TestServerFacade serverFacade;

    private static Server server;

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
    @Order(1)
    @DisplayName("Create User")
    public void createUserTest() throws DataAccessException {
        UserData userData = new UserData("Jerry", "jocelyn", "jerry@gmail.com");
        userDAO.createUser(userData);

        //verify user is created
        UserData foundUser = userDAO.getUser("Jerry");
        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals("jerry@gmail.com", userData.getEmail());
    }

    @Test
    @Order(2)
    @DisplayName("Create User Negative")
    public void createUserNegative() throws DataAccessException {
       UserData userData = new UserData("Jocelyn", "jocelyn", null);
       DataAccessException exception = assertThrows(DataAccessException.class, () -> {
           userDAO.createUser(userData);
       });
       Assertions.assertTrue(exception.getMessage().contains("unable to add user"));
    }

    @Test
    @Order(3)
    @DisplayName("Get User Test")
    public void getUserTest() throws DataAccessException {
        UserData testUser = new UserData("Jerry", "password", "email@email.com");
        userDAO.createUser(testUser);
        UserData getUser = userDAO.getUser("Jerry");

        Assertions.assertNotNull(getUser);
        Assertions.assertEquals("email@email.com", getUser.getEmail());
    }

    @Test
    @Order(4)
    @DisplayName("Get User Negative Test")
    public void getUserNegative() throws DataAccessException {
        UserData testUser = new UserData("Jerry", "password", "email@email.com");
        userDAO.createUser(testUser);
        UserData getUser = userDAO.getUser(null);

        Assertions.assertNull(getUser);
    }

    @Test
    @Order(5)
    @DisplayName("Verify Password")
    public void verifyPasswordTest() throws DataAccessException {
        String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());
        UserData testUser = new UserData("testUser", hashedPassword, "test@example.com");
        userDAO.createUser(testUser);
        UserData verifiedUser = userDAO.verifyPassword(testUser, hashedPassword);
        Assertions.assertNotNull(verifiedUser);
    }

    @Test
    @Order(6)
    @DisplayName("Verify Password Negative")
    public void verifyPasswordNegative() throws DataAccessException {
        String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());
        UserData testUser = new UserData("testUser", hashedPassword, "test@example.com");
        userDAO.createUser(testUser);

        //not a hashed password
        UserData verifiedUser = userDAO.verifyPassword(testUser, "password");
        Assertions.assertNull(verifiedUser);
    }

    @Test
    @Order(7)
    @DisplayName("Clear User Test")
    public void clearUserTest() throws DataAccessException {
        userDAO.createUser(new UserData("party", "password", "email"));
        userDAO.createUser(new UserData("Jerry", "password", "email"));
        userDAO.createUser(new UserData("Lee", "password", "email"));

        //All users are there
        Assertions.assertNotNull(userDAO.getUser("Lee"));
        Assertions.assertNotNull(userDAO.getUser("Jerry"));
        Assertions.assertNotNull(userDAO.getUser("party"));

        userDAO.clearUsers();

        //test if user is officially gone
        Assertions.assertNull(userDAO.getUser("Lee"));
    }

    @Test
    @Order(8)
    @DisplayName("Add Games Test")
    public void addGamesTest() throws DataAccessException {
        //GameData specs
        int gameID = 1234;
        String whiteUsername = "white";
        String blackUsername = "black";
        String gameName = "gameName";
        ChessGame game = new ChessGame();

        gameDAO.addGame(new GameData(gameID, whiteUsername, blackUsername, gameName, game));

        //test if game is officialyl in database
        Assertions.assertEquals(1, gameDAO.listGames().size());

    }

    @Test
    @Order(9)
    @DisplayName("Add Game Negative Test")
    public void addGameNegative() throws DataAccessException {
        GameData firstGame = new GameData(1234, "whiteUsername", "blackUsername", "game", new ChessGame());
        gameDAO.addGame(firstGame);

        GameData duplicateGame = new GameData(1234, "player3", "player4", "testGame", new ChessGame());

        assertThrows(DataAccessException.class, () -> {
            gameDAO.addGame(duplicateGame);
        });
    }

    @Test
    @Order(10)
    @DisplayName("Find Game Test")
    public void findGameTest() throws DataAccessException {
        //GameData specs
        int gameID = 1234;
        String whiteUsername = "white";
        String blackUsername = "black";
        String gameName = "gameName";
        ChessGame game = new ChessGame();
        gameDAO.addGame(new GameData(gameID, whiteUsername, blackUsername, gameName, game));

        //find game and test if found game matches specs
        GameData gameData = gameDAO.findGame(gameID);
        Assertions.assertEquals("gameName", gameData.getGameName());
    }

    @Test
    @Order(11)
    @DisplayName("Find Game Negative Test")
    public void findGameNegative() throws DataAccessException {
        //GameData specs
        int gameID = 1234;
        String whiteUsername = "white";
        String blackUsername = "black";
        String gameName = "gameName";
        ChessGame game = new ChessGame();
        gameDAO.addGame(new GameData(gameID, whiteUsername, blackUsername, gameName, game));

        //find game and test if found game matches specs
        Assertions.assertNull(gameDAO.findGame(1235));
    }

    @Test
    @Order(12)
    @DisplayName("Join Game Negative Test")
    public void joinGameNegative() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("Jocelyn", "jocelynjean");
        LoginResult result = loginService.login(loginRequest);
        createGameService.createGame(new CreateGameRequest("gameName"), result.authToken());
        CreateGameResult createGameResult = createGameService.createGame(new CreateGameRequest("newGame"), result.authToken());
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        joinGameService.joinGame(joinGameRequest, result.authToken());
        JoinGameRequest otherJoinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        JoinGameResult joinGameResult = joinGameService.joinGame(otherJoinGameRequest, result.authToken());
        Assertions.assertEquals("Error: already taken", joinGameResult.message());
    }

    @Test
    @Order(13)
    @DisplayName("Clear Test")
    public void clearTest() throws DataAccessException {
        //Login, create and join games, then logout
        LoginRequest loginRequest = new LoginRequest("Jocelyn", "jocelynjean");
        LoginResult result = loginService.login(loginRequest);
        createGameService.createGame(new CreateGameRequest("gameName"), result.authToken());
        CreateGameResult otherResult = createGameService.createGame(new CreateGameRequest("newGame"), result.authToken());
        CreateGameResult createGameResult = createGameService.createGame(new CreateGameRequest("testGame"), result.authToken());
        joinGameService.joinGame(new JoinGameRequest("WHITE", createGameResult.gameID()), result.authToken());
        joinGameService.joinGame(new JoinGameRequest("BLACK", otherResult.gameID()), result.authToken());
        logoutService.logout(result.authToken());

        //Register more people
        registerService.register(new RegisterRequest("potato", "bag", "potatobag@gmail.com"));
        registerService.register(new RegisterRequest("lee", "jensen", "leejensen@gmail.com"));

        //verify he has been registered
        UserData userData = userDAO.getUser("lee");
        Assertions.assertNotNull(userData);

        //CLEAR
        clearService.clear();

        //verify there are no more games in the database
        Collection<GameData> listGames = gameDAO.listGames();
        Assertions.assertEquals(0, listGames.size());

        //verify he has been cleared along with the others
        Assertions.assertNull(userDAO.getUser("lee"));
        Assertions.assertNull(userDAO.getUser("Jocelyn"));
    }

    @Test
    @Order(14)
    @DisplayName("Update Test")
    public void updateGame() throws DataAccessException {
        //intialize original ChessGame data
        int testGameID = 1234;
        ChessGame initialGame = new ChessGame();
        GameData initialGameData = new GameData(testGameID,"whitePlayer", "blackPlayer", "Sample", initialGame);
        gameDAO.addGame(initialGameData);

        //create new game to update initial to
        ChessGame updatedGame = new ChessGame();
        gameDAO.updateGame(updatedGame, testGameID);

        //make sure the game is still valid
        GameData retrievedGameData = gameDAO.findGame(testGameID);
        Assertions.assertNotNull(retrievedGameData);

        //Deserialize and verify they're the same
        ChessGame retrievedGame = retrievedGameData.getGame();
        String expectedGameJson = new Gson().toJson(updatedGame);
        String actualGameJson = new Gson().toJson(retrievedGame);
        Assertions.assertEquals(expectedGameJson, actualGameJson);
    }
}


