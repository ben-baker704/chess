package dataaccess;

import chess.ChessGame;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    private int nextId = 1;
    private final HashMap<Integer, String> games = new HashMap<>();

    @Override
    public ChessGame createGame() throws DataAccessException {
        return new ChessGame();
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
}
