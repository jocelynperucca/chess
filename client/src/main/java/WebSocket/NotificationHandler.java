package WebSocket;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage message);
}