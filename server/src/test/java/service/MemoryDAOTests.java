package service;

import dataaccess.*;

public class MemoryDAOTests {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    RegisterService registerService = new RegisterService(userDAO, authDAO);
    LoginService loginService = new LoginService(userDAO, authDAO);
    LogoutService logoutService = new LogoutService(authDAO);
    ListGamesService listGamesService = new ListGamesService(authDAO, gameDAO);
    CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
    JoinGameService joinGameService = new JoinGameService(authDAO,gameDAO);
    ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
}
