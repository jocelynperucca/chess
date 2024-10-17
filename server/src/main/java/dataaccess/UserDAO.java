package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import javax.xml.crypto.Data;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String userName) throws DataAccessException;

    UserData verifyPassword(UserData userData, String password) throws DataAccessException;

    //UserData insertUser(UserData userData) throws DataAccessException;





}
