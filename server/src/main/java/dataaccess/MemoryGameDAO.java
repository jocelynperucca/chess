package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO {
    final private HashMap<String, GameData> games = new HashMap<>();

    void addGame(GameData gameData) {
        games.put(gameData.getGameName(), gameData);
    }

    GameData findGame(int gameID) {
        GameData game = games.get(gameID);
        if (game != null) {
            return game;
        } else {
            return null;
        }
    }

    Collection<GameData> listGames() {
        return games.values();
    }


}
