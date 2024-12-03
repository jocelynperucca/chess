package websocket.messages;

import com.google.gson.Gson;

public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
