package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void saveAuthToken(AuthData authData) throws DataAccessException;

    public AuthData getAuthToken(String authToken) throws DataAccessException;

    void deleteAuthToken(String authToken) throws DataAccessException;
}
