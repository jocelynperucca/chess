package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException, SQLException {
        configureDatabase();
    }

    public void saveAuthToken(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        String username = authData.getUsername();
        String authToken = authData.getAuthToken();

        try (var connection = DatabaseManager.getConnection()) {
            try (var fullStatement = connection.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                fullStatement.setString(1, username);
                fullStatement.setString(2, authToken);
                fullStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(String.format("unable to save authToken: %s", e.getMessage()));
        }
    }

    public AuthData getAuthToken(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM auth WHERE authToken=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return new AuthData(rs.getString("userName"), rs.getString("authToken"));
                    }
                }
            }

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("unable to find authToken: %s", e.getMessage()));
        }

        return null;
    }

    public void deleteAuthToken(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
            }

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("unable to delete authToken: %s", e.getMessage()));
        }
    }

    public void clearAuthTokens() throws DataAccessException {
        var statement = "TRUNCATE auth";
        try(var conn = DatabaseManager.getConnection()) {
            try(var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to clear tokens: %s", e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
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
