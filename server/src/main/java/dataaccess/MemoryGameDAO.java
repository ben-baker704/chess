package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    private int nextId = 1;
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public GameData buildGame(String gameName) throws DataAccessException {
        int id = nextId++;
        GameData game = new GameData(id, null, null, gameName, new ChessGame());
        games.put(id, game);
        return game;
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        return games.get(Integer.parseInt(gameID));
    }

    @Override
    public Collection<GameData> gamesList() throws DataAccessException {
        return games.values();
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
            else {
                throw new DataAccessException("Error: Invalid");
            }
        }
        else if (color == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() == null) {
                GameData updated = new GameData(game.gameID(), game.whiteUsername(), username,
                        game.gameName(), game.game());
                games.put(game.gameID(), updated);
            }
            else {
                throw new DataAccessException("Error: Invalid");
            }
        }
    }

    @Override
    public void updateGame(String gameID, ChessGame.TeamColor color, String username, ChessGame game) throws DataAccessException {

    }

    @Override
    public void clearPlayer(String gameID, String username) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
}
