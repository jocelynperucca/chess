package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException, SQLException {
        configureDatabase();
    }

    public void addGame(GameData gameData) {

    }

    public GameData findGame(int gameID) {

    }

    public Collection<GameData> listGames() {

    }

    public void updateGameData(int gameID, String playerColor, String username) {

    }

    public void clearGames() {

    }


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `chessGame` JSON
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new SQLException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }


}
