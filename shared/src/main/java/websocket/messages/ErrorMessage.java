package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        if(!errorMessage.contains("Error")) {
            throw new IllegalArgumentException("ErrorMessage must contain 'Error'");
        }
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
