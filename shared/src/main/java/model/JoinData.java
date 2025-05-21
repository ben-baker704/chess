package model;

import chess.ChessGame;

public record JoinData(ChessGame.TeamColor color, String gameID) {
}
