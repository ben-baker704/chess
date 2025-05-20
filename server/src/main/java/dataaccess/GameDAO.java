package dataaccess;
import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public interface GameDAO {

    void createGame(String username, ChessGame.TeamColor color, String gameName)
            throws DataAccessException;
    GameData getGame(String gameID) throws DataAccessException;
    HashMap<Integer, GameData> listGames() throws DataAccessException;
    void updateGame(String gameID, ChessGame.TeamColor color, String username) throws DataAccessException;
    void clear() throws DataAccessException;
}
