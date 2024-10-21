package dataaccess;

import model.AuthData;

// Defines methods for managing authentication tokens, including saving, retrieving, deleting, and clearing tokens.
public interface AuthDAO {
    void saveAuthToken(AuthData authData) throws DataAccessException;

    AuthData getAuthToken(String authToken) throws DataAccessException;

    void deleteAuthToken(String authToken) throws DataAccessException;

    void clearAuthTokens() throws DataAccessException;
}
