package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public interface GameDAO {

    public void addGame(GameData gameData) throws DataAccessException;

    public GameData findGame(int gameID) throws DataAccessException;

    public Collection<GameData> listGames() throws DataAccessException;

    public void updateGameData(int gameID, String playerColor, String username) throws DataAccessException;

    public void clearGames() throws DataAccessException;




}
