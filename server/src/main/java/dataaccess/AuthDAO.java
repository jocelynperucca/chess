package dataaccess;

public interface AuthDAO {
    void saveAuthToken(String authToken) throws DataAccessException;

    void getAuthToken(String authToken) throws DataAccessException;

    void deleteAuthToken(String authToken) throws DataAccessException;
}
