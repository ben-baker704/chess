package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessPiece.PieceType.*;

public class PawnMoves implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        List<ChessMove> valid_moves = new ArrayList<>();

        int up;
        int up_two;
        int[][] diagonals;
        int starting_row;
        int promotion_row;

        ChessPiece pawn = board.getPiece(position);
        ChessGame.TeamColor color = pawn.getTeamColor();

        if (color == ChessGame.TeamColor.WHITE) {
            up = 1;
            up_two = 2;
            diagonals = new int[][] {
                    {1, -1}, {1, 1}
            };
            starting_row = 2;
            promotion_row = 8;
        }

        else {
            up = -1;
            up_two = -2;
            diagonals = new int[][] {
                    {-1, -1}, {-1, 1}
            };
            starting_row = 7;
            promotion_row = 1;
        }

        int row = position.getRow();
        int col = position.getColumn();
        ChessPosition new_position = new ChessPosition(row + up, col);
        ChessPiece target = board.getPiece(new_position);

        // Move up 1
        if (row + up >= 1 && row + up <= 8) {

            if (target == null) {
                if (new_position.getRow() == promotion_row) {
                    valid_moves.add(new ChessMove(position, new_position, QUEEN));
                    valid_moves.add(new ChessMove(position, new_position, BISHOP));
                    valid_moves.add(new ChessMove(position, new_position, ROOK));
                    valid_moves.add(new ChessMove(position, new_position, KNIGHT));
                } else {
                    valid_moves.add(new ChessMove(position, new_position, null));
                }
            }
        }

        // Move Up 2
        if (starting_row == row) {
            ChessPosition new_position_2 = new ChessPosition(row + up_two, col);
            ChessPiece target_2 = board.getPiece(new_position_2);
            if (target == null && target_2 == null) {
                valid_moves.add(new ChessMove(position, new_position_2, null));
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
            new_position = new ChessPosition(row, col);
            target = board.getPiece(new_position);
            if (new_position.getRow() == promotion_row && target != null && target.getTeamColor() != color) {
                valid_moves.add(new ChessMove(position, new_position, QUEEN));
                valid_moves.add(new ChessMove(position, new_position, BISHOP));
                valid_moves.add(new ChessMove(position, new_position, ROOK));
                valid_moves.add(new ChessMove(position, new_position, KNIGHT));
            } else if (target != null && target.getTeamColor() != color) {
                valid_moves.add(new ChessMove(position, new_position, null));
            }
        }

        return valid_moves;
    }



    @Override
    public String toString() {
        return "PawnMoves{}";
    }
}
