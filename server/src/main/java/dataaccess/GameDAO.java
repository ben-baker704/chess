package dataaccess;
import chess.ChessGame;
import model.GameData;
import java.util.Collection;

public interface GameDAO {

    GameData buildGame(String gameName)
            throws DataAccessException;
    GameData getGame(String gameID) throws DataAccessException;
    Collection<GameData> gamesList() throws DataAccessException;
    void updateGame(String gameID, ChessGame.TeamColor color, String username) throws DataAccessException;
    void clear() throws DataAccessException;
}
