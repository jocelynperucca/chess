package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
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

    public CreateGameResult createGame(String authToken) throws DataAccessException {
        if (authDao.getAuthToken(authToken) == null) {
            return new CreateGameResult(null,"Error: unauthorized");
        } else {
            int gameID = generateGameID();
            gameDao.addGame();
            return new CreateGameResult(gameID, "Created Game");
        }

    }
}
