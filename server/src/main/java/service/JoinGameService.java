package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import model.JoinGameRequest;
import model.JoinGameResult;
import model.ListGamesResult;

import java.util.Collection;

public class JoinGameService {

    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public JoinGameService(AuthDAO authDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest, String authToken) throws DataAccessException {
        if (authDao.getAuthToken(authToken) == null) {
            return new JoinGameResult("Error: unauthorized");
        }

       int joinGameID = joinGameRequest.gameID();
       GameData gameData = gameDao.findGame(joinGameID);

       if(gameData == null) {
           return new JoinGameResult("Error: bad request");
       } else {
           String playerColor = joinGameRequest.playerColor();
           String user = authDao.getAuthToken(authToken).getUsername();

           if (playerColor == null) {
               //what happens if it's null?
           } else if (playerColor.equalsIgnoreCase("WHITE") && gameData.getWhiteUsername() != null) {
               return new JoinGameResult("Error: already taken");
           } else if (playerColor.equalsIgnoreCase("BLACK") && gameData.getBlackUsername() != null) {
               return new JoinGameResult("Error: already taken");
           } else {

           }

       }

    }
}
