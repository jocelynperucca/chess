package WebSocket;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void handleMessage(ServerMessage message);
}
