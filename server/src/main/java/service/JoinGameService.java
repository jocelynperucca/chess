package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import model.JoinGameRequest;
import model.JoinGameResult;


//Handles game joining logic, verifying authorization and player color, while updating game data accordingly.
public class JoinGameService {

    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public JoinGameService(AuthDAO authDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    //function to attempt to join a game based on color and authorization
    public JoinGameResult joinGame(JoinGameRequest joinGameRequest, String authToken) throws DataAccessException {
        if (authDao.getAuthToken(authToken) == null) {
            return new JoinGameResult("Error: unauthorized");
        }

        //get GameData
        int joinGameID = joinGameRequest.gameID();
        GameData gameData = gameDao.findGame(joinGameID);
        JoinGameResult joinGameResult;

        if (gameData == null) {
            return new JoinGameResult("Error: bad request");
        } else {
            //get requested color and username to join
            String playerColor = joinGameRequest.playerColor();
            String user = authDao.getAuthToken(authToken).getUsername();
            joinGameResult = new JoinGameResult("");

            if (playerColor == null || (!playerColor.equals("BLACK") && !playerColor.equals("WHITE"))) {
                joinGameResult = new JoinGameResult("Error: bad request");

            } else if (playerColor.equalsIgnoreCase("WHITE") && gameData.getWhiteUsername() != null) {
                joinGameResult = new JoinGameResult("Error: already taken");

            } else if (playerColor.equalsIgnoreCase("BLACK") && gameData.getBlackUsername() != null) {
                joinGameResult = new JoinGameResult("Error: already taken");

            } else if (playerColor.equalsIgnoreCase("WHITE")) {
                gameDao.updateGameData(joinGameID, "WHITE", user);
                joinGameResult = new JoinGameResult("Joined Game");

            } else if (playerColor.equalsIgnoreCase("BLACK")) {
                gameDao.updateGameData(joinGameID, "BLACK", user);
                joinGameResult = new JoinGameResult("Joined Game");
            }
        }

        //return message
        return joinGameResult;
    }
}
