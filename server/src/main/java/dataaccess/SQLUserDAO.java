package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.sql.Statement;


public class SQLUserDAO implements UserDAO {

    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String username = userData.getUsername();
        String password = hashPassword(userData.getPassword());
        String email = userData.getEmail();
        var json = new Gson().toJson(userData);
        //var id = executeUpdate(statement, username, password, email);

        try (var connection = DatabaseManager.getConnection()) {
            try (var fullStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                fullStatement.setString(1, username);
                fullStatement.setString(2, password);
                fullStatement.setString(3, email);
                fullStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException( String.format("unable to add user: %s, %s", statement, e.getMessage()));
        }
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

