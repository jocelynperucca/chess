package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;
import model.ListGamesResult;

import java.util.Collection;
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

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws DataAccessException {
        String createGameName = createGameRequest.gameName();

        if (authDao.getAuthToken(authToken) == null) {
            return new CreateGameResult(null,"Error: Unauthorized");
        } else if(createGameName == null || createGameName.isEmpty()) {
            return new CreateGameResult(null, "Error: bad request");
        } else {
            try {
                int gameID = generateGameID();
                GameData newGame = new GameData(gameID, authDao.getAuthToken(authToken).getUsername(), null, createGameName, null);
                gameDao.addGame(newGame);
                return new CreateGameResult(gameID, "Created Game");

            } catch (DataAccessException e) {
                throw new DataAccessException("Error: couldn't create game");
            }

        }

    }
}
