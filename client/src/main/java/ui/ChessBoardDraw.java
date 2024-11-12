package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoardDraw {

    public static void drawChessBoard(ChessBoard board) {
        // Draw the chessboard in the default orientation
        board.resetBoard();
        drawChessBoardOrientation(board, true);

        // Print a line separator between orientations
        System.out.println("\n");

        // Draw the chessboard in the reversed orientation
        drawChessBoardOrientation(board, false);
    }

    private static void drawChessBoardOrientation(ChessBoard board, boolean normalOrientation) {
        final String labelBackground = EscapeSequences.SET_BG_COLOR_BLUE;
        final String reset = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;

        // Print top column labels
        System.out.print(labelBackground + "    ");
        if (normalOrientation) {
            System.out.print("a   b   c" + "\u2005" + "  d" + "\u2005" + "  e  " + "\u2005" + "f   g " + "\u2005" + " h    " + "\u2009");
        } else {
            System.out.print("h   g   f" + "\u2005" + "  e" + "\u2005" + "  d  " + "\u2005" + "c   b " + "\u2005" + " a    " + "\u2009");
        }        System.out.print(reset + "\n");

        // Determine row range based on orientation
        int startRow = !normalOrientation ? 8 : 1;
        int endRow = !normalOrientation ? 1 : 8;
        int rowStep = !normalOrientation ? -1 : 1;

        // Loop to print each row of the chessboard
        for (int row = startRow; row != endRow + rowStep; row += rowStep) {
            // Print row label on the left side
            System.out.print(labelBackground + " " + row + " " + reset);

            // Determine column range based on orientation
            int startCol = !normalOrientation ? 1 : 8;
            int endCol = !normalOrientation ? 8 : 1;
            int colStep = !normalOrientation ? 1 : -1;

            for (int col = startCol; col != endCol + colStep; col += colStep) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                // Alternate square colors
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_MAGENTA);
                }

                // Print piece symbol or empty space
                if (piece != null) {
                    System.out.print(getPieceSymbol(piece));
                } else {
                    System.out.print(EscapeSequences.EMPTY);
                }
                System.out.print(reset); // Reset color after each square
            }

            // Print row label on the right side
            System.out.println(labelBackground + " " + row + " " + reset);
        }

        // Print bottom column labels
        System.out.print(labelBackground + "    ");
        if (normalOrientation) {
            System.out.print("a   b   c" + "\u2005" + "  d" + "\u2005" + "  e  " + "\u2005" + "f   g " + "\u2005" + " h    " + "\u2009");
        } else {
            System.out.print("h   g   f" + "\u2005" + "  e" + "\u2005" + "  d  " + "\u2005" + "c   b " + "\u2005" + " a    " + "\u2009");
        }        System.out.print(reset + "\n");
    }


    /**
     * Returns the ANSI symbol for the given chess piece based on its type and color.
     */
    private static String getPieceSymbol(ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            default:
                return EscapeSequences.EMPTY; // Fallback for unknown pieces
        }
    }
}
