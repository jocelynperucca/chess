package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

import java.util.UUID;

//Manages user registration by validating input, creating new user accounts, and generating authorization tokens upon successful registration.
public class RegisterService {

    private final UserDAO userDao;
    private final AuthDAO authDao;

    public RegisterService(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString(); // Generates a random UUID string for an authToken
    }


    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {

        //checks if all variables are filled out
        if(registerRequest.email() == null || registerRequest.username() == null || registerRequest.password() == null
                || registerRequest.username().isEmpty() || registerRequest.password().isEmpty() || registerRequest.email().isEmpty()) {
            return new RegisterResult(null, null, "Error: bad request");

        } else {
            if(userDao.getUser(registerRequest.username()) != null) {
                return new RegisterResult(null, null, "Error: already taken");

            } else {
                userDao.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
                String authToken = generateAuthToken();
                AuthData userAuthData = new AuthData(registerRequest.username(), authToken);
                authDao.saveAuthToken(userAuthData);

                //successfully registered
                return new RegisterResult(registerRequest.username(), authToken, "created");
            }
        }
    }
}
