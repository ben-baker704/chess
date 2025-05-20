package dataaccess;

import chess.ChessGame;

public class MemoryGameDAO implements GameDAO{

    @Override
    public ChessGame createGame() throws DataAccessException {
        return new ChessGame();
    }
}
