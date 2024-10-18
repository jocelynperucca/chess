package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {

    void addGame(GameData gameData) throws DataAccessException;

    GameData findGame(int gameID) throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;




}
