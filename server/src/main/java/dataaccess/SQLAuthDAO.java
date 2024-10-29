package dataaccess;

import model.AuthData;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLAuthDAO implements AuthDAO {

    //creates auth database based on createStatements down below
    public SQLAuthDAO() throws DataAccessException, SQLException {
        ConfigureDatabase.configureDatabase(createStatements);
    }

    //inserts new authToken into auth database
    public void saveAuthToken(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";

        //get all AuthData information
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

    //retrieves authData given a set authToken
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

    //deletes just on row in auth database
    public void deleteAuthToken(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("unable to delete authToken: %s", e.getMessage()));
        }
    }

    //clears everything in auth database
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

    //SQL to create auth database
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
