package model;

//STORAGE OF USER INFORMATION AND GETTERS TO OBTAIN THAT INFORMATION
public class UserData {
    String username;
    String password;
    String email;

    public UserData (String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    //GETTERS
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
