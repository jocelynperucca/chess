package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLAuthDAO implements AuthDAO {

    public void saveAuthToken(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?, ?)";
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
            var statement = "SELECT username, authToken FROM user WHERE authToken=?";
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

    public void deleteAuthToken(String authToken) {

    }

    public void clearAuthTokens(){

    }

}
