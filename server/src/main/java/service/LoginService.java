package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.eclipse.jetty.util.log.Log;

import java.util.UUID;

public class LoginService {

    private final UserDAO userDao;
    private final AuthDAO authDao;

    public LoginService(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString(); // Generates a random UUID string
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();
        UserData userData = userDao.getUser(username);

        if(userData == null) {
            return new LoginResult(null, null, "Error: no user found");
        } else {
            if (userDao.verifyPassword(userData, password) == null) {
                return new LoginResult(null, null, "Error: unauthorized");
            } else {
                String authToken = generateAuthToken();
                AuthData authUserData = new AuthData(username, authToken);
                authDao.saveAuthToken(authUserData);
                return new LoginResult(username, authToken, "Logged In");

            }
        }

    }

}
