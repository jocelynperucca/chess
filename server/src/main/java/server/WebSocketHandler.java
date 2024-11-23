package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, Action.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> enter(userGameCommand.visitorName(), session);
            case LEAVE -> exit(action.visitorName());
            case MAKE_MOVE -> ;
            case RESIGN -> ;
        }
    }

}
