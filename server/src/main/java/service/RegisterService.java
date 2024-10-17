package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
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

    public RegisterService(UserDAO userDao) {
        this.userDao = userDao;
    }

    public static String generateRandomString() {
        return UUID.randomUUID().toString(); // Generates a random UUID string
    }


    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if(registerRequest.email() == null || registerRequest.userName() == null || registerRequest.password() == null) {
            return new RegisterResult(null, null, "Error: couldn't register");

        } else {
            if(userDao.getUser(registerRequest.userName()) != null) {
                return new RegisterResult(null, null, "Error: username in use");

            } else {
                userDao.createUser(new UserData(registerRequest.userName(), registerRequest.password(), registerRequest.email()));
                String authToken = generateRandomString();
                //add token here
                //return result
            }

        }
    }
}
