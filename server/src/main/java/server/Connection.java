package server;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    public String authToken;
    public int gameID; // Add a gameID field to group by game
    public Session session;

    public Connection(String authToken, int gameID, Session session) {
        this.authToken = authToken;
        this.gameID = gameID; // Initialize gameID
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
