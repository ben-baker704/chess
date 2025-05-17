package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class RookMoves implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        List<ChessMove> validMoves = new ArrayList<>();

        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] direction : directions) {
            int row = position.getRow();
            int col = position.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];

                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;}

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);

                if (target == null) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                }
                else {
                    ChessPiece rook = board.getPiece(position);
                    ChessGame.TeamColor color = rook.getTeamColor();
                    if (target.getTeamColor() != color) {
                        validMoves.add(new ChessMove(position, newPosition, null));
                    }
                    break;
                }
            }
        }

        return validMoves;
    }
}
