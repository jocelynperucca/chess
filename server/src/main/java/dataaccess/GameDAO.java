package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {

    void addGame(GameData gameData) throws DataAccessException;

    GameData findGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGameData(int gameID, String playerColor, String username) throws DataAccessException;

    void clearGames() throws DataAccessException;




}
