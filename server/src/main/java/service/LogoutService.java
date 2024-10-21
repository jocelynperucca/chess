package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import requestsAndResults.LogoutResult;


//Manages user logout functionality by validating the authorization token and removing it from the data store upon successful logout.
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
