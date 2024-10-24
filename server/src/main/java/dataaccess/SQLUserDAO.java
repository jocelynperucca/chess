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
            throw new DataAccessException(String.format("unable to add user: %s", e.getMessage()));
        }
    }

    public UserData getUser(String userName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM user WHERE username=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1, userName);
                try (var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return new UserData(rs.getString(userName), rs.getString("password"), rs.getString("email"));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public UserData verifyPassword(UserData userData, String password) throws DataAccessException {
        String username = userData.getUsername();
        UserData user = getUser(username);

        String query = "SELECT username, password FROM user WHERE username = ?";

        try (var connection = DatabaseManager.getConnection()) {
            try (var ps = connection.prepareStatement(query)) {
                ps.setString(1, username);

                try(var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        String storedPassword = rs.getString("password");
                        if (BCrypt.checkpw(password, storedPassword)) {
                            return userData;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to verify user: %s", e.getMessage()));
        }

        return null;
    }


    public void clearUsers() {

    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new SQLException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

