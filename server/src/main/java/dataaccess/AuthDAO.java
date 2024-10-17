package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void saveAuthToken(AuthData authData) throws DataAccessException;

    void getAuthToken(String authToken) throws DataAccessException;

    void deleteAuthToken(String authToken) throws DataAccessException;
}
