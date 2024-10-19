package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.ClearResult;


public class ClearService {

    private final AuthDAO authDao;
    private final GameDAO gameDao;
    private final UserDAO userDao;

    public ClearService(AuthDAO authDao, GameDAO gameDao, UserDAO userDao) {
        this.authDao = authDao;
        this.gameDao = gameDao;
        this.userDao = userDao;
    }

    public ClearResult clear() throws DataAccessException {

    }
}
