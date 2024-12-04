package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.sql.SQLException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New session connected: " + session.getRemoteAddress());
        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException, InvalidMoveException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(session, new Gson().fromJson(message, ConnectCommand.class));
            case MAKE_MOVE -> makeMove(session, new Gson().fromJson(message, MakeMoveCommand.class));
            // Other command handling (e.g., LEAVE, MAKE_MOVE, RESIGN) can be added here.
        }
    }

    private void connect(Session session, ConnectCommand command) throws DataAccessException, IOException {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID(); // gameID is now an int.
        if (gameID <= 0 || authToken == null) {
            throw new IllegalArgumentException("Invalid gameID or authToken");
        }

        // Validate authToken and retrieve user information
        if (authDAO.getAuthToken(authToken) == null) {
            throw new IllegalArgumentException("Invalid authToken");
        }
        String userName = authDAO.getAuthToken(authToken).getUsername();

        // Add connection grouped by gameID
        connections.add(authToken, gameID, session);

        // Notify other users in the same game
        var message = String.format("%s has connected to game", userName);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, authToken, notification);
        GameData gameData = gameDAO.findGame(gameID);
        var loadGameMessage = new LoadGameMessage(gameData.getGame());
        String jsonMessage = loadGameMessage.toJson();
        session.getRemote().sendString(jsonMessage);
    }

    private void makeMove(Session session, MakeMoveCommand command) throws DataAccessException, InvalidMoveException, IOException {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        ChessMove move = command.getMove();
        GameData gameData = gameDAO.findGame(gameID);

        ChessGame chessGame = gameData.getGame();
        chessGame.makeMove(move);
        gameDAO.updateGame(chessGame, gameID);

        var notification = new NotificationMessage("Move made successfully");
        connections.broadcast(gameID, authToken, notification);

        LoadGameMessage loadGameMessage = new LoadGameMessage(chessGame);
        String jsonMessage = new Gson().toJson(loadGameMessage);
        session.getRemote().sendString(jsonMessage);
    }

    private void addConnection(String authToken, int gameID, Session session) throws DataAccessException {
        if (authDAO.getAuthToken(authToken) == null) {
            throw new IllegalArgumentException("Invalid authToken");
        }
        connections.add(authToken, gameID, session);
    }
}
