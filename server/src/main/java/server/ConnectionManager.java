package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    // Map gameID (int) to a list of connections
    private final ConcurrentHashMap<Integer, List<Connection>> connectionsByGame = new ConcurrentHashMap<>();

    // Add a connection to a specific game
    public void add(String visitorName, int gameID, Session session) {
        var connection = new Connection(visitorName, gameID, session);
        connectionsByGame.computeIfAbsent(gameID, k -> new ArrayList<>()).add(connection);
    }

    // Remove a connection by visitorName and gameID
    public void remove(String authToken, int gameID) {
        List<Connection> connections = connectionsByGame.get(gameID);
        if (connections != null) {
            connections.removeIf(c -> c.authToken.equals(authToken));
            if (connections.isEmpty()) {
                connectionsByGame.remove(gameID); // Clean up empty game list
            }
        }
    }

    // Broadcast a message to all connections in a specific game, excluding a visitor
    public void broadcast(int gameID, String authToken, ServerMessage notification) throws IOException {
        System.out.println("BROADCASTING" + gameID);
        List<Connection> connections = connectionsByGame.get(gameID);
        if (connections == null) return;

        var removeList = new ArrayList<Connection>();
        for (var c : connections) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(authToken)) {
                    String jsonMessage = notification.toJson();
                    c.send(jsonMessage);
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any closed connections
        connections.removeAll(removeList);
        if (connections.isEmpty()) {
            connectionsByGame.remove(gameID);
        }
    }
}
