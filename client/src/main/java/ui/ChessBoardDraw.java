package ui;

import chess.*;

import java.util.Collection;

public class ChessBoardDraw {

    public static void drawChessBoard(ChessBoard board, ChessPosition position) {
        // Draw the chessboard in the default orientation
        drawChessBoardOrientation(board, true, position);

        // Print a line separator between orientations
        System.out.println("\n");

        // Draw the chessboard in the reversed orientation
        drawChessBoardOrientation(board, false, position);
    }

    private static void drawChessBoardOrientation(ChessBoard board, boolean normalOrientation, ChessPosition selectedPosition) {
        final String labelBackground = EscapeSequences.SET_BG_COLOR_BLUE;
        final String reset = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;

        // Print top column labels
        System.out.print(labelBackground + "    ");
        if (!normalOrientation) {
            System.out.print("a   b   c" + "\u2005" + "  d" + "\u2005" + "  e  " + "\u2005" + "f   g " + "\u2005" + " h    " + "\u2009");
        } else {
            System.out.print("h   g   f" + "\u2005" + "  e" + "\u2005" + "  d  " + "\u2005" + "c   b " + "\u2005" + " a    " + "\u2009");
        }
        System.out.print(reset + "\n");

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

            // Loop to print each column in the row
            for (int col = startCol; col != endCol + colStep; col += colStep) {
                ChessPosition position = new ChessPosition(row, col);

                // Alternate square colors
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_MAGENTA);
                }

                // Highlight valid moves for the selected piece
                if (selectedPosition != null) {
                    ChessPiece pieceAtSelectedPosition = board.getPiece(selectedPosition);
                    if (pieceAtSelectedPosition != null) {
                        Collection<ChessMove> validMoves = pieceAtSelectedPosition.pieceMoves(board, selectedPosition);
                        ChessMove chessMove = new ChessMove(selectedPosition, position, null);

                        // Highlight valid move squares in green
                        if (validMoves != null && validMoves.contains(chessMove)) {
                            System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                        }
                    }
                }

                // Print the piece at the current position if it exists
                ChessPiece pieceAtPosition = board.getPiece(position);
                if (pieceAtPosition != null) {
                    // Highlight the selected position itself in yellow
                    if (position.equals(selectedPosition)) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
                    }

                    // Print the piece symbol
                    System.out.print(getPieceSymbol(pieceAtPosition));
                } else {
                    // Print empty space if no piece at the position
                    System.out.print(EscapeSequences.EMPTY);
                }

                // Reset any coloring
                System.out.print(reset);
            }

            // Print row label on the right side
            System.out.println(labelBackground + " " + row + " " + reset);
        }

        // Print bottom column labels
        System.out.print(labelBackground + "    ");
        if (!normalOrientation) {
            System.out.print("a   b   c" + "\u2005" + "  d" + "\u2005" + "  e  " + "\u2005" + "f   g " + "\u2005" + " h    " + "\u2009");
        } else {
            System.out.print("h   g   f" + "\u2005" + "  e" + "\u2005" + "  d  " + "\u2005" + "c   b " + "\u2005" + " a    " + "\u2009");
        }
        System.out.print(reset + "\n");
    }



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
                return EscapeSequences.EMPTY;
        }
    }
}
