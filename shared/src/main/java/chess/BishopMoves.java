package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoves implements PieceMovesCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        List<ChessMove> valid_moves = new ArrayList<>();

        // Diagonal directions
        int[][] directions = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] direction : directions) {
            int row = position.getRow();
            int col = position.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];

                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }

                ChessPosition new_position = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(new_position);

                if (target == null) {
                    valid_moves.add(new ChessMove(position, new_position, null));
                }
                else {
                    ChessPiece bishop = board.getPiece(position);
                    ChessGame.TeamColor color = bishop.getTeamColor();
                    if (target.getTeamColor() != color) {
                        valid_moves.add(new ChessMove(position, new_position, null));
                    }
                    break;
                }
            }
        }

        return valid_moves;
    }

    @Override
    public String toString() {
        return "BishopMoves{}";
    }
}
