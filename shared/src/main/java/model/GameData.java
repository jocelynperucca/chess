package model;

import chess.ChessGame;

//STORAGE OF EVERYTHING PERTAINING TO A GAME AND FUNCTIONS THAT GET AND SET THESE VARIABLES
public class GameData {
    int gameID;
    String whiteUsername;
    String blackUsername;
    String gameName;
    ChessGame game;

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    //GETTERS
    public int getGameID(){
        return gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public ChessGame getGame() {
        return game;
    }

    //SET USERNAME BASED ON INPUT
    public void setWhiteUsername(String username) {
        whiteUsername = username;
    }

    public void setBlackUsername(String username) {
        blackUsername = username;
    }
}
