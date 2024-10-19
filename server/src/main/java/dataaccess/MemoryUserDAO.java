package dataaccess;


import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.HashMap;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData userData) throws DataAccessException{
        users.put(userData.getUsername(), userData);
    }

    public UserData getUser(String userName) throws DataAccessException {
        try {
            UserData user = users.get(userName);
            return user; // This will return null if the user is not found
        } catch (Exception e) {
            // If you had a different logic that could throw an exception
            throw new DataAccessException("Could not access user data");
        }
    }

    public UserData verifyPassword(UserData userData, String password) throws DataAccessException{
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

    @Override
    public void clearUsers() throws DataAccessException {
        users.clear();
    }
}
