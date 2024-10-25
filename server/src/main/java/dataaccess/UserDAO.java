package dataaccess;

import model.UserData;

import java.sql.SQLException;

// Defines methods for managing user data, including user creation, retrieval, password verification, and clearing users.
public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String userName) throws DataAccessException;

    UserData verifyPassword(UserData userData, String password) throws DataAccessException;

    void clearUsers() throws DataAccessException;




}
