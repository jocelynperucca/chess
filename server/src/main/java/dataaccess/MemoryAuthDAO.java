package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    public void saveAuthToken(AuthData authData) {
        //AuthData authData = new AuthData(authToken, user)
        authTokens.put(authData.getAuthToken(), authData);
    }

    public AuthData getAuthToken(String authToken) throws DataAccessException{
        if(authTokens.get(authToken) == null) {
            return null;
        }
        return authTokens.get(authToken);
    }

    public void deleteAuthToken(String authToken) throws DataAccessException {
        if(authTokens.remove(authToken) == null) {
            throw new DataAccessException("Error: couldn't delete because doesn't exist");
        } else {
            authTokens.remove(authToken);
        }
    }

    public void clearAuthTokens() {
        authTokens.clear();
    }
}
