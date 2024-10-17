package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

import java.util.UUID;

public class RegisterService {
//    public void delete() throws Exception {
//        throw new Exception("test delete");
//    }
//
//    public void post() throws Exception {
//        throw new Exception("test");
//    }

    private final UserDAO userDao;
    private final AuthDAO authDao;

    public RegisterService(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString(); // Generates a random UUID string
    }


    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if(registerRequest.email() == null || registerRequest.userName() == null || registerRequest.password() == null) {
            return new RegisterResult(null, null, "Error: couldn't register, missing info");

        } else {
            if(userDao.getUser(registerRequest.userName()) != null) {
                return new RegisterResult(null, null, "Error: username in use");

            } else {
                userDao.createUser(new UserData(registerRequest.userName(), registerRequest.password(), registerRequest.email()));
                String authToken = generateAuthToken();
                AuthData userAuthData = new AuthData(registerRequest.userName(), authToken);
                authDao.saveAuthToken(userAuthData);

                //successfully registered
                return new RegisterResult(registerRequest.userName(), authToken, "created");
            }

        }
    }
}
