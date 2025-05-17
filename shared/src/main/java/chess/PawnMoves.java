package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessPiece.PieceType.*;

public class PawnMoves implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        List<ChessMove> validMoves = new ArrayList<>();

        int up;
        int upTwo;
        int[][] diagonals;
        int startingRow;
        int promotionRow;

        ChessPiece pawn = board.getPiece(position);
        ChessGame.TeamColor color = pawn.getTeamColor();

        if (color == ChessGame.TeamColor.WHITE) {
            up = 1;
            upTwo = 2;
            diagonals = new int[][] {
                    {1, -1}, {1, 1}
            };
            startingRow = 2;
            promotionRow = 8;
        }

        else {
            up = -1;
            upTwo = -2;
            diagonals = new int[][] {
                    {-1, -1}, {-1, 1}
            };
            startingRow = 7;
            promotionRow = 1;
        }

        int row = position.getRow();
        int col = position.getColumn();
        ChessPosition newPosition = new ChessPosition(row + up, col);
        ChessPiece target = board.getPiece(newPosition);

        // Move up 1
        if (row + up >= 1 && row + up <= 8) {

            if (target == null) {
                if (newPosition.getRow() == promotionRow) {
                    validMoves.add(new ChessMove(position, newPosition, QUEEN));
                    validMoves.add(new ChessMove(position, newPosition, BISHOP));
                    validMoves.add(new ChessMove(position, newPosition, ROOK));
                    validMoves.add(new ChessMove(position, newPosition, KNIGHT));
                } else {
                    validMoves.add(new ChessMove(position, newPosition, null));
                }
            }
        }

        // Move Up 2
        if (startingRow == row) {
            ChessPosition newPosition2 = new ChessPosition(row + upTwo, col);
            ChessPiece target2 = board.getPiece(newPosition2);
            if (target == null && target2 == null) {
                validMoves.add(new ChessMove(position, newPosition2, null));
            }
        }

        // Diagonal moves
        for (int[] diagonal : diagonals) {
            row = position.getRow();
            col = position.getColumn();

            row += diagonal[0];
            col += diagonal[1];


            if (row < 1 || row > 8  || col < 1 || col > 8) {
                continue;
            }
            newPosition = new ChessPosition(row, col);
            target = board.getPiece(newPosition);
            if (newPosition.getRow() == promotionRow && target != null && target.getTeamColor() != color) {
                validMoves.add(new ChessMove(position, newPosition, QUEEN));
                validMoves.add(new ChessMove(position, newPosition, BISHOP));
                validMoves.add(new ChessMove(position, newPosition, ROOK));
                validMoves.add(new ChessMove(position, newPosition, KNIGHT));
            } else if (target != null && target.getTeamColor() != color) {
                validMoves.add(new ChessMove(position, newPosition, null));
            }
        }

        return validMoves;
    }



    @Override
    public String toString() {
        return "PawnMoves{}";
    }
}
