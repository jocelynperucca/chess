package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(new Gson().fromJson(message, ConnectCommand.class));
            case LEAVE -> ;
            case MAKE_MOVE -> ;
            case RESIGN -> ;
        }
    }

    private void connect(ConnectCommand command) {
        connections.add
    }

}
