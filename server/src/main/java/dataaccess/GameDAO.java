package dataaccess;
import chess.ChessGame;

public interface GameDAO {

    ChessGame createGame() throws DataAccessException;

}
