package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;

// Defines methods for managing games, including saving, retrieving, deleting, updating, and clearing gameData.
public interface GameDAO {

    void addGame(GameData gameData) throws DataAccessException;

    GameData findGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGameData(int gameID, String playerColor, String username) throws DataAccessException;

    void clearGames() throws DataAccessException;

    void updateGame(ChessGame game, int gameID) throws DataAccessException;
}
