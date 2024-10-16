package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import javax.xml.crypto.Data;

public interface UserDAO {
    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(UserData userData) throws DataAccessException;

    UserData verifyPassword(UserData userData) throws DataAccessException;





}
