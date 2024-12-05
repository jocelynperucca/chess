package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;

// In-memory implementation of GameDAO for managing game data using a HashMap.
public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();

    //Add game to memory
    public void addGame(GameData gameData) {
        games.put(gameData.getGameID(), gameData);
    }

    //Find game in memory based on ID
    public GameData findGame(int gameID) {
        GameData game = games.get(gameID);
        if (game != null) {
            return game;
        } else {
            return null;
        }
    }

    //get the whole list of games in memory
    public Collection<GameData> listGames() {
        return games.values();
    }

    public void updateGameData(int gameID, String playerColor, String username) throws DataAccessException {

        //initialize game with gameID
        GameData game = games.get(gameID);

        // see if game exists and insert user as white or black user
        if (game == null) {
            throw new DataAccessException("Game doesn't exist");
        } else if (playerColor.equalsIgnoreCase("WHITE")) {
            game.setWhiteUsername(username);
            games.put(gameID, game);
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            game.setBlackUsername(username);
            games.put(gameID, game);
        }
    }

    public void updateGame(ChessGame game, int gameID) {
        }

    //clear all games in GameDAO
    public void clearGames() {
        games.clear();
    }

    public void removePlayer(int gameID, String playerColor) throws DataAccessException {

    }

    }
