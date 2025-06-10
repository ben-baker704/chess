package websocket;
import com.google.gson.Gson;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<Integer, Map<String, Connection>>
            connections = new ConcurrentHashMap<>();

    public void add(int gameID, String visitorName, Session session) {
        connections.putIfAbsent(gameID, new ConcurrentHashMap<>());
        var connection = new Connection(visitorName, session);
        connections.get(gameID).put(visitorName, connection);

    }

    public void remove(int gameID, String visitorName) {
        var game = connections.get(gameID);
        if (game != null) {
            game.remove(visitorName);
            if (game.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, String excludeVisitorName, String notification) throws IOException {
        var game = connections.get(gameID);
        if (game != null) {
            for (var entry : game.entrySet()) {
                if (!entry.getKey().equals(excludeVisitorName)) {
                    entry.getValue().send(notification);
                }
            }
        }
    }

    public void sendMessageTo(int gameID, String visitorName, String notification) throws IOException {
        var connection = connections.getOrDefault(gameID, new ConcurrentHashMap<>()).get(visitorName);
        if (connection != null) {
            connection.send(notification);
        }
    }
}
