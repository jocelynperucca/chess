package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import requestsAndResults.LoginRequest;
import requestsAndResults.LoginResult;
import model.UserData;
import java.util.UUID;

//Handles user authentication by verifying credentials and generating an authorization token upon successful login.
public class LoginService {

    private final UserDAO userDao;
    private final AuthDAO authDao;

    public LoginService(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    // Generates a random UUID string for an authToken
    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    //Handles login requests
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();
        UserData userData = userDao.getUser(username);

        if(userData == null) {
            return new LoginResult(null, null, "Error: unauthorized");

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
