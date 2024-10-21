package model;

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
}
