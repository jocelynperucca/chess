package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Objects;

// In-memory implementation of UserDAO for managing user data using a HashMap.
public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData userData) {
        users.put(userData.getUsername(), userData);
    }

    public UserData getUser(String userName) throws DataAccessException {
        try {
            UserData user = users.get(userName);
            return user; // This will return null if the user is not found
        } catch (Exception e) {
            throw new DataAccessException("Could not access user data");
        }
    }

    public UserData verifyPassword(UserData userData, String password) throws DataAccessException {

        //initialize testUser to see if is in UserDAO
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

    //clear ALL UserDAO
    @Override
    public void clearUsers() throws DataAccessException {
        users.clear();
    }
}
