package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import requestsAndResults.LogoutResult;

public class LogoutService {

    private final AuthDAO authDao;

    public LogoutService(AuthDAO authDao) {
        this.authDao = authDao;
    }

    public LogoutResult logout(String authToken) throws DataAccessException {
        if (authDao.getAuthToken(authToken) == null) {
            return new LogoutResult("Error: unauthorized");
        } else {
            authDao.deleteAuthToken(authToken);
            return new LogoutResult("Logged Out");
        }

    }
}
