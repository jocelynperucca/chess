package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;


public class SQLUserDAO implements UserDAO {

    public void createUser(UserData userData) {
        String username = userData.getUsername();
        String password = hashPassword(userData.getPassword());
        String email = userData.getEmail();


    }

    public UserData getUser(String userName) {

    }

    public UserData verifyPassword(UserData userData, String password) {

    }

    public void clearUsers() {

    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}

