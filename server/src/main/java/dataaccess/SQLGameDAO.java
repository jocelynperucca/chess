package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAO implements GameDAO {

    //creates game database based on createStatements down below
    public SQLGameDAO() throws DataAccessException, SQLException {
       ConfigureDatabase.configureDatabase(createStatements);
    }

    //adds a game to the database based on predetermined gameData
    public void addGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game (gameId, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";

        //get all GameData information
        int gameID = gameData.getGameID();
        String whiteUsername = gameData.getWhiteUsername();
        String blackUsername = gameData.getBlackUsername();
        String gameName = gameData.getGameName();
        ChessGame game = gameData.getGame();
        String gameJson = new Gson().toJson(game);

        try (var connection = DatabaseManager.getConnection()) {
            try (var fullStatement = connection.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                fullStatement.setInt(1, gameID);
                fullStatement.setString(2, whiteUsername);
                fullStatement.setString(3, blackUsername);
                fullStatement.setString(4, gameName);
                fullStatement.setString(5, gameJson);
                fullStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to add game: %s", e.getMessage()));
        }
    }

    //finds a specific game based on given gameID and returns all GameData
    public GameData findGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        //serialize chessGame
                        String chessGameJson = rs.getString("game");
                        ChessGame chessGame = new Gson().fromJson(chessGameJson, ChessGame.class);
                        return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                                rs.getString("blackUsername"), rs.getString("gameName"), chessGame);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to find user: %s", e.getMessage()));
        }
        return null;
    }

    //Goes through every game in the database and lists the GameData for each one
    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to find games: %s", e.getMessage()));
        }

        return result;
    }

    //updates different usernames within the game database once a user has claimed a spot and color
    public void updateGameData(int gameID, String playerColor, String username) throws DataAccessException {
        var statement = "UPDATE game SET ";
        GameData game = findGame(gameID);

        // see if game exists and insert user as white or black user
        if (game == null) {
            throw new DataAccessException("Game doesn't exist");
        } else if (playerColor.equalsIgnoreCase("WHITE")) {
            statement += "whiteUsername = ? WHERE gameID = ?";
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            statement += "blackUsername = ? WHERE gameID = ?";
        } else {
            throw new DataAccessException("Invalid player color.");
        }

        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            ps.setInt(2, gameID);

            //see if there was actually a change
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataAccessException("Error: didn't update");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: couldn't update game data: " + e.getMessage());
        }
    }

    //Once we reach phase 6, this will update the state of ChessGame and the board
    public void updateGame(ChessGame game, int gameID) throws DataAccessException {
        if (game == null || findGame(gameID) == null) {
            throw new DataAccessException("Incorrect Game Information for Update");
        }

        String selectString = "SELECT gameID FROM game WHERE gameID=?";
        String updateString = "UPDATE game SET game=? WHERE gameID=?";

        try (var conn = DatabaseManager.getConnection();
             var selectStatement = conn.prepareStatement(selectString)) {

            // Confirm the game ID exists in the database
            selectStatement.setInt(1, gameID);
            try (var rs = selectStatement.executeQuery()) {
                if (rs.next()) {
                    try (var updateStatement = conn.prepareStatement(updateString)) {
                        // Serialize the game object to JSON
                        Gson gson = new Gson();
                        String gameJson = gson.toJson(game);

                        // Set parameters and execute update
                        updateStatement.setString(1, gameJson);
                        updateStatement.setInt(2, gameID);
                        updateStatement.executeUpdate();
                    }
                } else {
                    throw new DataAccessException("Game ID not found for update.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    //clears everything from game database
    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE game";
        try(var conn = DatabaseManager.getConnection()) {
            try(var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to clear games: %s", e.getMessage()));
        }
    }

    //private function to read in GameData by individual games
    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        String chessGameJson = rs.getString("game");
        ChessGame chessGame = new Gson().fromJson(chessGameJson, ChessGame.class);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }

    public void removePlayer(int gameID, String playerColor) throws DataAccessException {
        if (!playerColor.equalsIgnoreCase("WHITE") && !playerColor.equalsIgnoreCase("BLACK")) {
            throw new DataAccessException("Invalid player color");
        }
        if (playerColor == "observer") {
            return;
        }
        var statement = "UPDATE game SET " + playerColor + "Username" + " = NULL WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            int rowsUpdated = ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //SQL to create game database
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` JSON NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

}
