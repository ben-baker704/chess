package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> makeMove(command);
            case LEAVE -> leave(command);
            case RESIGN -> resign(command);
        }
    }

    private void connect(UserGameCommand command, Session session) throws Exception {
        String user = authDAO.getUsername(command.getAuthToken());
        int gameID = command.getGameID();
        connections.add(gameID, user, session);
        ChessGame game = gameDAO.getGame(Integer.toString(gameID)).game();
        if (game == null) {
            throw new Exception("Error: game does not exist");
        }

        ServerMessage loadGame = new LoadGameMessage(game);
        connections.sendMessageTo(gameID, user, new Gson().toJson(loadGame));
        ServerMessage notification = new NotificationMessage(user + "joined game");
        connections.broadcast(gameID, user, new Gson().toJson(loadGame));
    }

    private void makeMove(UserGameCommand command) throws Exception {
        String user = authDAO.getUsername(command.getAuthToken());
        int gameID = command.getGameID();
        ChessGame game = gameDAO.getGame(Integer.toString(gameID)).game();
        if (game == null) {
            throw new Exception("Error: game does not exist");
        }

        ChessMove move = command.getMove();
        game.makeMove(move);
        gameDAO.updateGame(Integer.toString(gameID), game.getTeamTurn(), user);

        ServerMessage update = new LoadGameMessage(game);
        connections.broadcast(gameID, null, new Gson().toJson(update));
    }

    private void leave(UserGameCommand command) throws Exception {

    }

    private void resign(UserGameCommand command) throws Exception {

    }
}
