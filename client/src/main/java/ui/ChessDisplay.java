package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessDisplay {

    public void draw(ChessBoard board, ChessGame.TeamColor color) {
        int[] rowOrder = (color == ChessGame.TeamColor.WHITE) ? new int[]{8, 7, 6, 5, 4, 3, 2, 1} :
                new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        char[] columnOrder = (color == ChessGame.TeamColor.WHITE) ? new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'} :
                new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};

        System.out.println();
        printRowChars(columnOrder);
        for (int row : rowOrder) {
            System.out.print(" " + row + " ");
            for (char column : columnOrder) {
                int col = column - 'a' + 1;
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                makeSquare(piece, row, column);
            }
            System.out.println(" " + row);
        }
        printRowChars(columnOrder);
        System.out.println();
    }

    private void makeSquare(ChessPiece piece, int column, char row) {
        boolean lighterSquare = (column + (row - 'a' + 1)) % 2 != 0;
        String backgroundColor = lighterSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY :
                EscapeSequences.SET_BG_COLOR_DARK_GREY;
        String pieceType = getPieceSequence(piece);
        System.out.print(backgroundColor + pieceType + EscapeSequences.RESET_BG_COLOR);
    }

    private void printRowChars(char[] chars) {
        System.out.print("   ");
        for (char character : chars) {
            System.out.print(" " + character + " ");
        }
        System.out.println();
    }

    private String getPieceSequence(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }
        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.BLACK_KING
                    : EscapeSequences.WHITE_KING;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.BLACK_QUEEN
                    : EscapeSequences.WHITE_QUEEN;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.BLACK_BISHOP
                    : EscapeSequences.WHITE_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.BLACK_KNIGHT
                    : EscapeSequences.WHITE_KNIGHT;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.BLACK_ROOK
                    : EscapeSequences.WHITE_ROOK;
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.BLACK ? EscapeSequences.BLACK_PAWN
                    : EscapeSequences.WHITE_PAWN;
        };
    }
}
