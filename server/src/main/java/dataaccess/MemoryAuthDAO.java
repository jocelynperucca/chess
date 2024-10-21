package dataaccess;

import model.AuthData;
import java.util.HashMap;

// In-memory implementation of AuthDAO for managing authentication tokens using a HashMap.
public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    //Save object in memory
    public void saveAuthToken(AuthData authData) {
        authTokens.put(authData.getAuthToken(), authData);
    }

    //getter
    public AuthData getAuthToken(String authToken) {
        if(authTokens.get(authToken) == null) {
            return null;
        }
        return authTokens.get(authToken);
    }

    //delete just one authToken
    public void deleteAuthToken(String authToken) throws DataAccessException {
        if(authTokens.remove(authToken) == null) {
            throw new DataAccessException("Error: couldn't delete because doesn't exist");
        } else {
            authTokens.remove(authToken);
        }
    }

    //clear ALL of authDAO
    public void clearAuthTokens() {
        authTokens.clear();
    }
}
