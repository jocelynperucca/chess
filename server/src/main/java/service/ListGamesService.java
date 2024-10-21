package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import requestsAndResults.ListGamesResult;

import java.util.Collection;

public class ListGamesService {

    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public ListGamesService(AuthDAO authDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        if (authDao.getAuthToken(authToken) == null) {
            return new ListGamesResult(null,"Error: unauthorized");
        } else {
            Collection<GameData> listGames = gameDao.listGames();
            return new ListGamesResult(listGames, "Listed Games");
        }

    }
}
