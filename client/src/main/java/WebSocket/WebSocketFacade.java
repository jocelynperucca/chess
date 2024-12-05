package WebSocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.JsonSyntaxException;
import ui.ResponseException;
import com.google.gson.Gson;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    private ServerMessageListener serverMessageListener;

    public WebSocketFacade(String url, NotificationHandler notificationHandler, ServerMessageListener messageListener) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;
            this.serverMessageListener = messageListener;  // Ensure it's set here

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // Set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private void handleMessage(String message) {
        try {
            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> {
                    LoadGameMessage lgMessage = new Gson().fromJson(message, LoadGameMessage.class);
                    serverMessageListener.onLoadGame(lgMessage);
                }
                case NOTIFICATION -> {
                    NotificationMessage nMessage = new Gson().fromJson(message, NotificationMessage.class);
                    serverMessageListener.onNotification(nMessage);
                }
                case ERROR -> {
                    ErrorMessage eMessage = new Gson().fromJson(message, ErrorMessage.class);
                    serverMessageListener.onError(eMessage);
                }
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON format: " + message);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void setMessageListener(ServerMessageListener messageListener) {
        this.serverMessageListener = messageListener;
    }

    @ClientEndpoint
    public interface ServerMessageListener {
        void onLoadGame(LoadGameMessage message);
        void onNotification(NotificationMessage message);
        void onError(ErrorMessage message);
    }


    public void sendCommand(UserGameCommand userGameCommand) throws ResponseException {
        try {
            String command = new Gson().toJson(userGameCommand);
            session.getBasicRemote().sendText(command);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void joinPlayerSend(int gameID, ChessGame.TeamColor teamColor, String authToken) throws ResponseException {
        ConnectCommand connectCommand = new ConnectCommand(authToken, gameID);
        sendCommand(connectCommand);
    }

    public void makeMoveSend(String authToken, int gameID, ChessMove chessMove) throws ResponseException {
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(authToken, gameID, chessMove);
        sendCommand(makeMoveCommand);
    }

    public void leaveSend(String authToken, int gameID) throws ResponseException {
        LeaveCommand leaveCommand = new LeaveCommand(authToken, gameID);
        sendCommand(leaveCommand);
    }

    public void resignSend(String authToken, int gameID) throws ResponseException {
        ResignCommand resignCommand = new ResignCommand(authToken, gameID);
        sendCommand(resignCommand);
    }
}
