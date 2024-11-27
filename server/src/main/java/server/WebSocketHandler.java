package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.sql.SQLException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    //private Session session;


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
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(session, new Gson().fromJson(message, ConnectCommand.class));
//            case LEAVE -> ;
//            case MAKE_MOVE -> ;
//            case RESIGN -> ;
        }
    }

    private void connect(Session session, ConnectCommand command) throws DataAccessException, IOException {
        connections.add(command.getAuthToken(), session);
        String authToken = command.getAuthToken();
        String userName = authDAO.getAuthToken(authToken).getUsername();
        var message = String.format("%s has connected", userName);
        var notification = new NotificationMessage(message);
        connections.broadcast(userName, notification);
    }

    private void addConnection(String authToken, Session session) throws DataAccessException {
        if (authDAO.getAuthToken(authToken) == null) {
            throw new IllegalArgumentException("Invalid authToken");
        }
        connections.add(authToken, session);
    }
}
