package server;
import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    public String authToken;
    public Session session;

    public Connection(String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

}
