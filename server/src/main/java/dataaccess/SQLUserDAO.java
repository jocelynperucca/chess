package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLUserDAO implements UserDAO {

    //creates user database based on createStatements down below
    public SQLUserDAO() throws DataAccessException, SQLException {
            ConfigureDatabase.configureDatabase(createStatements);
    }

    //creates user by using given UserData to insert into user database
    public void createUser(UserData userData) throws DataAccessException {
        //set query and obtain userData information
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String username = userData.getUsername();
        String password = hashPassword(userData.getPassword());
        String email = userData.getEmail();

        try (var connection = DatabaseManager.getConnection()) {
            try (var fullStatement = connection.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                fullStatement.setString(1, username);
                fullStatement.setString(2, password);
                fullStatement.setString(3, email);
                fullStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to add user: %s", e.getMessage()));
        }
    }

    //retrieves user by using given username to search database with
    public UserData getUser(String userName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1, userName);
                try (var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return new UserData(rs.getString("userName"), rs.getString("password"), rs.getString("email"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to find user: %s", e.getMessage()));
        }
        return null;
    }

    //verifies password by decrypting stored password and comparing it to the given password
    public UserData verifyPassword(UserData userData, String password) throws DataAccessException {
        //verifies is user actually exists
        String username = userData.getUsername();
        if (getUser(username) == null) {
            throw new DataAccessException("User not found");
        }

        //Establishes query to obtain userData and verifies connection to execute it
        String query = "SELECT username, password FROM user WHERE username = ?";
        try (var connection = DatabaseManager.getConnection(); var ps = connection.prepareStatement(query)) {
            //sets username in query and finds selects UserData
            ps.setString(1, username);
            var rs = ps.executeQuery();

            if (!rs.next()) {
                return null;
            }
            //retrieves password to decrypt and to verify
            String storedPassword = rs.getString("password");
            if (BCrypt.checkpw(password, storedPassword)) {
                return userData;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to verify user: %s", e.getMessage()));
        }
        return null;
    }

    //clears everything from user database
    public void clearUsers() throws DataAccessException {
        var statement = "TRUNCATE user";
        try(var conn = DatabaseManager.getConnection()) {
            try(var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to clear users: %s", e.getMessage()));
        }
    }

    //encrypts password to store in database
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    //SQL to create user database
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            );
            """
    };
}

