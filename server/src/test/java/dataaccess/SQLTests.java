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
    public void createUserNegative() {
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
    @DisplayName("List Games Test")
    public void listGamesTest() throws DataAccessException {
        //GameData specs
        int gameID = 1234;
        String whiteUsername = "white";
        String blackUsername = "black";
        String gameName = "gameName";
        ChessGame game = new ChessGame();
        gameDAO.addGame(new GameData(gameID, whiteUsername, blackUsername, gameName, game));

        Assertions.assertEquals(1, gameDAO.listGames().size());
    }

    @Test
    @Order(13)
    @DisplayName("List Games Negative Test")
    public void listGamesNegative() throws DataAccessException {
        //GameData specs
        int gameID = 1234;
        String whiteUsername = "white";
        String blackUsername = "black";
        String gameName = "gameName";
        ChessGame game = new ChessGame();
        gameDAO.addGame(new GameData(gameID, whiteUsername, blackUsername, gameName, game));

        Assertions.assertNotEquals(2, gameDAO.listGames().size());
    }

    @Test
    @Order(14)
    @DisplayName("Update Game Data Test")
    public void updateGameDataTest() throws DataAccessException {
        gameDAO.addGame(new GameData(1234, "whiteUsername", "blackUsername", "gameName", new ChessGame()));

        //Change whiteUsername
        gameDAO.updateGameData(1234, "WHITE", "Jocelyn");
        GameData gameData = gameDAO.findGame(1234);

        Assertions.assertEquals("Jocelyn", gameData.getWhiteUsername());
    }

    @Test
    @Order(15)
    @DisplayName("Update Game Data Negative Test")
    public void updateGameDataNegative() throws DataAccessException {
        gameDAO.addGame(new GameData(1234, "whiteUsername", "blackUsername", "gameName", new ChessGame()));

        //Change whiteUsername with invalid color
        assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGameData(1234, "PURPLE", "Jocelyn");
        });
    }

    @Test
    @Order(16)
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

    @Test
    @Order(17)
    @DisplayName("Update Negative Test")
    public void updateGameNegative() throws DataAccessException {
        //intialize original ChessGame data
        int testGameID = 1234;
        ChessGame initialGame = new ChessGame();
        GameData initialGameData = new GameData(testGameID,"whitePlayer", "blackPlayer", "Sample", initialGame);
        gameDAO.addGame(initialGameData);

        //create new game to update initial to
        ChessGame updatedGame = new ChessGame();

        //bad game ID
        assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(updatedGame, 1235);
        });
    }

    @Test
    @Order(18)
    @DisplayName("Clear Games Test")
    public void clearGamesTest() throws DataAccessException {
        gameDAO.addGame(new GameData(1234, "whiteUsername", "blackUsername", "gameName", new ChessGame()));
        gameDAO.addGame(new GameData(1235, "whiteUsername", "blackUsername", "otherGame", new ChessGame()));

        //Verify both games were added to database
        Assertions.assertEquals(2, gameDAO.listGames().size());

        //CLEAR
        gameDAO.clearGames();

        //Verify ALL game were removed from database
        Assertions.assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    @Order(19)
    @DisplayName("Save AuthToken Test")
    public void saveAuthTest() throws DataAccessException {
        AuthData authData = new AuthData("Jocelyn", "authToken");
        //Inserts authToken into Database
        authDAO.saveAuthToken(authData);

        //gets authData from authToken and compare to see if username comes back the same
        AuthData result = authDAO.getAuthToken("authToken");
        Assertions.assertEquals("Jocelyn", result.getUsername());
    }

    @Test
    @Order(20)
    @DisplayName("Save AuthToken Negative Test")
    public void saveAuthNegativeTest() throws DataAccessException {
        AuthData authData = new AuthData("Jocelyn", null);

        //Attempts to insert authToken into Database but can't because null
        assertThrows(DataAccessException.class, () -> {
            authDAO.saveAuthToken(authData);
        });
    }

    @Test
    @Order(21)
    @DisplayName("Get AuthToken Test")
    public void getAuthTest() throws DataAccessException {
        AuthData authData = new AuthData("Jocelyn", "authToken");
        //Inserts authToken into Database
        authDAO.saveAuthToken(authData);

        //gets authData from authToken and compares to see if the authdata previously initialized
        //is the same
        AuthData result = authDAO.getAuthToken("authToken");
        Assertions.assertEquals(authData, result);
    }

    @Test
    @Order(22)
    @DisplayName("Get AuthToken Negative Test")
    public void getAuthNegativeTest() throws DataAccessException {
        AuthData authData = new AuthData("Jocelyn", "authToken");

        //Inserts authToken into Database
        authDAO.saveAuthToken(authData);

        //With a non-existent authToken, should return null
        Assertions.assertNull(authDAO.getAuthToken("badAuth"));
    }

    @Test
    @Order(23)
    @DisplayName("Delete AuthToken Test")
    public void deleteAuthTest() throws DataAccessException {
        AuthData authData = new AuthData("Jocelyn", "authToken");
        //Inserts authToken into Database
        authDAO.saveAuthToken(authData);

        //Verify authToken made it to the database
        Assertions.assertNotNull(authDAO.getAuthToken("authToken"));

        //Delete authToken and make sure it's not in Database
        authDAO.deleteAuthToken("authToken");
        Assertions.assertNull(authDAO.getAuthToken("authToken"));
    }

    @Test
    @Order(24)
    @DisplayName("Delete AuthToken Negative Test")
    public void deleteAuthNegativeTest() throws DataAccessException {
        AuthData authData = new AuthData("Jocelyn", "authToken");
        //Inserts authToken into Database
        authDAO.saveAuthToken(authData);

        //Verify authToken made it to the database
        Assertions.assertNotNull(authDAO.getAuthToken("authToken"));

        //Delete authToken and make sure it's not in Database
        authDAO.deleteAuthToken("authToken");

        //Tries to delete it again, but is not found in database
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuthToken("authToken"));
    }

    @Test
    @Order(25)
    @DisplayName("Clear AuthTokens Test")
    public void clearAuthTest() throws DataAccessException {
        AuthData authData = new AuthData("Jocelyn", "authToken");
        AuthData newAuthData = new AuthData("Jerry", "JerryAuth");

        //Inserts authToken into Database
        authDAO.saveAuthToken(authData);
        authDAO.saveAuthToken(newAuthData);

        //verify both authTokens are in the database
        Assertions.assertNotNull(authDAO.getAuthToken("authToken"));
        Assertions.assertNotNull(authDAO.getAuthToken("JerryAuth"));

        //CLEAR
        authDAO.clearAuthTokens();

        //Verify both authTokens have been cleared
        Assertions.assertNull(authDAO.getAuthToken("authToken"));
        Assertions.assertNull(authDAO.getAuthToken("JerryAuth"));
    }



}


