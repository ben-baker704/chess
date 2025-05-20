package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    private int nextId = 1;
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(String username, ChessGame.TeamColor color, String gameName) throws DataAccessException {
        int id = nextId++;
        GameData game;
        if (color == ChessGame.TeamColor.WHITE) {
            game = new GameData(id, username, null, gameName, new ChessGame());
        }
        else {
            game = new GameData(id, null, username, gameName, new ChessGame());
        }
        games.put(id, game);
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        return games.get(Integer.parseInt(gameID));
    }

    @Override
    public HashMap<Integer, GameData> listGames() throws DataAccessException {
        return games;
    }

    @Override
    public void updateGame(String gameID, ChessGame.TeamColor color, String username) throws DataAccessException {
        GameData game = getGame(gameID);
        if (color == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() == null) {
                GameData updated = new GameData(game.gameID(), username, game.blackUsername(),
                        game.gameName(), game.game());
                games.put(game.gameID(), updated);
            }
        }
        else if (color == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() == null) {
                GameData updated = new GameData(game.gameID(), game.whiteUsername(), username,
                        game.gameName(), game.game());
                games.put(game.gameID(), updated);
            }
        }
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
}
