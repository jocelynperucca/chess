package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;


public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException, SQLException {
            ConfigureDatabase.configureDatabase(createStatements);
    }

    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String username = userData.getUsername();
        String password = hashPassword(userData.getPassword());
        String email = userData.getEmail();
        //var json = new Gson().toJson(userData);
        //var id = executeUpdate(statement, username, password, email);

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


    public UserData verifyPassword(UserData userData, String password) throws DataAccessException {
        String username = userData.getUsername();
        if (getUser(username) == null) {
            throw new DataAccessException("User not found");
        }

        String query = "SELECT username, password FROM user WHERE username = ?";
        try (var connection = DatabaseManager.getConnection();
             var ps = connection.prepareStatement(query)) {

            ps.setString(1, username);
            var rs = ps.executeQuery();

            if (!rs.next()) {
                return null;
            }

            String storedPassword = rs.getString("password");
            if (BCrypt.checkpw(password, storedPassword)) {
                return userData;
            }

        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to verify user: %s", e.getMessage()));
        }

        return null;
    }


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



    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


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

