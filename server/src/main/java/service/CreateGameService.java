package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import requestsAndResults.CreateGameRequest;
import requestsAndResults.CreateGameResult;
import model.GameData;
import java.util.concurrent.ThreadLocalRandom;

public class CreateGameService {

    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public CreateGameService(AuthDAO authDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public static int generateGameID() {
        // Generate a random 4-digit number between 1000 and 9999
        return ThreadLocalRandom.current().nextInt(1000, 10000);
    }

    //function to create game if authorized
    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws DataAccessException {
        String createGameName = createGameRequest.gameName();

        if (authDao.getAuthToken(authToken) == null) {
            return new CreateGameResult(null,"Error: unauthorized");

        } else if(createGameName == null || createGameName.isEmpty()) {
            return new CreateGameResult(null, "Error: bad request");

        } else {
            try {
                int gameID = generateGameID();
                GameData newGame = new GameData(gameID, null, null, createGameName, null);
                gameDao.addGame(newGame);
                return new CreateGameResult(gameID, "Created Game");

            } catch (DataAccessException e) {
                throw new DataAccessException("Error: couldn't create game");
            }
        }
    }
}
