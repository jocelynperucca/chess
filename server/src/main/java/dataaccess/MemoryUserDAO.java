package dataaccess;


import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.HashMap;
import java.util.Objects;

abstract class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData userData) {
        users.put(userData.getUsername(), userData);
    }

    public UserData getUser(String userName) {
        UserData user = users.get(userName);
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }

    public UserData verifyPassword(UserData userData, String password) {
        UserData testUser = getUser(userData.getUsername());
        if (testUser == null) {
            return null;
        } else {
            if(Objects.equals(testUser.getPassword(), password)) {
                return testUser;
            } else {
                return null;
            }
        }

    }

}
