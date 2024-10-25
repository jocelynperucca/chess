package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException, SQLException {
        configureDatabase();
    }

    public void addGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game (gameId, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        int gameID = gameData.getGameID();
        String whiteUsername = gameData.getWhiteUsername();
        String blackUsername = gameData.getBlackUsername();
        String gameName = gameData.getGameName();
        ChessGame game = gameData.getGame();
        //var json = new Gson().toJson(userData);
        //var id = executeUpdate(statement, username, password, email);

        try (var connection = DatabaseManager.getConnection()) {
            try (var fullStatement = connection.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                fullStatement.setInt(1, gameID);
                fullStatement.setString(2, whiteUsername);
                fullStatement.setString(3, blackUsername);
                fullStatement.setString(4, gameName);
                fullStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to add game: %s", e.getMessage()));
        }

    }

    public GameData findGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGameJson FROM game WHERE gameID=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        //serialize chessGame
                        String chessGameJson = rs.getString("chessGameJson");
                        ChessGame chessGame = new Gson().fromJson(chessGameJson, ChessGame.class);
                        return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), chessGame);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to find user: %s", e.getMessage()));
        }
        return null;
    }


    public Collection<GameData> listGames() {
        var result = new

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
              `chessGame` JSON,
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
