package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;


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
        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, session);
                case MAKE_MOVE -> makeMove(command);
                case LEAVE -> leave(command);
                case RESIGN -> resign(command);
            }
        }
        catch (Exception e) {
            String errorMessage = (e.getMessage() == null) ? "Unknown Error" : e.getMessage();
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage(errorMessage)));
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
        connections.broadcast(gameID, user, new Gson().toJson(notification));
    }

    private void makeMove(UserGameCommand command) throws Exception {
        String user = authDAO.getUsername(command.getAuthToken());
        int gameID = command.getGameID();
        var gameData = gameDAO.getGame(Integer.toString(gameID));
        ChessGame game = gameData.game();
        if (game == null) {
            throw new Exception("Error: game does not exist");
        }

        ChessGame.TeamColor userTeam = null;
        if (user.equals(gameData.whiteUsername())) {
            userTeam = ChessGame.TeamColor.WHITE;
        }
        else if (user.equals(gameData.blackUsername())) {
            userTeam = ChessGame.TeamColor.BLACK;
        }
        else {
            throw new Exception("Error: Observer does not make moves");
        }

        if (userTeam != game.getTeamTurn()) {
            throw new Exception("Error: Other team's turn");
        }

        ChessMove move = command.getMove();
        game.makeMove(move);

        if (gameDAO instanceof MySqlGameAccess sqlGameAccess) {
            sqlGameAccess.saveGameState(Integer.toString(gameID), game);
        }
        else {
            throw new DataAccessException("Error");
        }

        ServerMessage update = new LoadGameMessage(game);
        connections.broadcast(gameID, null, new Gson().toJson(update));

        ServerMessage notification = new NotificationMessage(user + "moved piece");
        connections.broadcast(gameID, user, new Gson().toJson(notification));
    }

    private void leave(UserGameCommand command) throws Exception {
        String user = authDAO.getUsername(command.getAuthToken());
        int gameID = command.getGameID();
        connections.remove(gameID, user);
        ServerMessage notification = new NotificationMessage(user + "left game");
        connections.broadcast(gameID, user, new Gson().toJson(notification));
    }

    private void resign(UserGameCommand command) throws Exception {
        String user = authDAO.getUsername(command.getAuthToken());
        int gameID = command.getGameID();
        ChessGame game = gameDAO.getGame(Integer.toString(gameID)).game();
        if (game == null) {
            throw new Exception("Error: game does not exist");
        }
        gameDAO.updateGame(Integer.toString(gameID), game.getTeamTurn(), user);
        ServerMessage resignMessage = new NotificationMessage(user + "resigned");
        connections.broadcast(gameID, null, new Gson().toJson(resignMessage));
    }
}
