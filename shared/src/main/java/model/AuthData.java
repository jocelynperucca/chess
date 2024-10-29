package model;

import java.util.Objects;

//STORAGE OF USERNAMES AND AUTHTOKENS TO VERIFY AUTHORIZATION
public class AuthData {

    String username;
    String authToken;

    public AuthData (String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

//GETTERS
    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthData authData = (AuthData) o;
        return Objects.equals(username, authData.username) && Objects.equals(authToken, authData.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authToken);
    }
}
