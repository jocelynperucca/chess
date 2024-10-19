package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public void addGame(GameData gameData) {
        games.put(gameData.getGameID(), gameData);
    }

    public GameData findGame(int gameID) {
        GameData game = games.get(gameID);
        if (game != null) {
            return game;
        } else {
            return null;
        }
    }

    public Collection<GameData> listGames() {
        return games.values();
    }


    public void updateGameData(int gameID, String playerColor, String username) throws DataAccessException {
        GameData game = games.get(gameID);

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
}
