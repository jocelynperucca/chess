package server;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    public String authToken;
    public int gameID; // Add a gameID field to group by game
    public Session session;
    public final String role;

    public Connection(String authToken, int gameID, String role, Session session) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.role = role;// Initialize gameID
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    public String getRole() {
        return this.role;
    }
}
