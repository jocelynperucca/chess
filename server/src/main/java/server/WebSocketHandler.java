package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
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
        GameData gameData = gameDAO.findGame(gameID);
        if (gameData == null) {
            String errorMessage = "ERROR: Invalid gameID - " + gameID;
            //System.err.println(errorMessage);
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage(errorMessage)));
            return;
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
        //GameData gameData = gameDAO.findGame(gameID);
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

        LoadGameMessage loadGameMessage = new LoadGameMessage(chessGame);
        String jsonMessage = new Gson().toJson(loadGameMessage);
        connections.broadcast(gameID, authToken, loadGameMessage);
        session.getRemote().sendString(jsonMessage);
        String startMove = toChessCoordinates(move.getStartPosition());
        String endMove = toChessCoordinates(move.getEndPosition());
        String moves = startMove + "-" + endMove + " ";
        AuthData authData = authDAO.getAuthToken(authToken);
        String userName = authData.getUsername();


        var notification = new NotificationMessage(moves + userName + " made move successfully");
        connections.broadcast(gameID, authToken, notification);
    }

    private void addConnection(String authToken, int gameID, Session session) throws DataAccessException {
        if (authDAO.getAuthToken(authToken) == null) {
            throw new IllegalArgumentException("Invalid authToken");
        }
        connections.add(authToken, gameID, session);
    }

    public String toChessCoordinates(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();

        char letter = (char) ('a' + col - 1); // Convert column number to letter ('a' = 1, 'b' = 2, etc.)
        int number = row; // Row remains as is.

        return "" + number + letter; // Concatenate letter and number into a single string.
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        // Log the error
        System.err.println("Error in WebSocket communication: " + throwable.getMessage());
        throwable.printStackTrace();

        String errorMesge = "An error occurred: " + throwable.getMessage();
        ErrorMessage errorMessage = new ErrorMessage(errorMesge);

        String jsonErrorMessage = errorMessage.toJson();



        // Optionally, you can send a message to the client indicating the error
        try {
            session.getRemote().sendString(jsonErrorMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
