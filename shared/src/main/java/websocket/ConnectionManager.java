package websocket;
import com.google.gson.Gson;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>>
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

//    public void broadcast(String excludeVisitorName, Notification notification) throws IOException {
//        var removeList = new ArrayList<Connection>();
//        for (var c : connections.values()) {
//            if (c.session.isOpen()) {
//                if (!c.visitorName.equals(excludeVisitorName)) {
//                    c.send(notification.toString());
//                }
//            } else {
//                removeList.add(c);
//            }
//        }
//
//        // Clean up any connections that were left open.
//        for (var c : removeList) {
//            connections.remove(c.visitorName);
//        }
//    }
}
