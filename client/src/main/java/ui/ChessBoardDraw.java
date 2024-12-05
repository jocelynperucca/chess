package ui;

import chess.*;
import java.util.Collection;

public class ChessBoardDraw {

    private static final String LABEL_BACKGROUND = EscapeSequences.SET_BG_COLOR_BLUE;
    private static final String RESET = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;

    public static void drawChessBoard(ChessBoard board, ChessPosition position) {
        drawChessBoardOrientation(board, true, position);
        System.out.println("\n");
        drawChessBoardOrientation(board, false, position);
    }

    private static void drawChessBoardOrientation(ChessBoard board, boolean normalOrientation, ChessPosition selectedPosition) {
        printColumnLabels(normalOrientation);
        printRows(board, normalOrientation, selectedPosition);
        printColumnLabels(normalOrientation);
    }

    private static void printColumnLabels(boolean normalOrientation) {
        System.out.print(LABEL_BACKGROUND + "    ");
        System.out.print(normalOrientation ?
                "h   g   f" + "\u2005" + "  e" + "\u2005" + "  d  " + "\u2005" + "c   b " + "\u2005" + " a    " + "\u2009" :
                "a   b   c" + "\u2005" + "  d" + "\u2005" + "  e  " + "\u2005" + "f   g " + "\u2005" + " h    " + "\u2009");
        System.out.println(RESET);
    }

    private static void printRows(ChessBoard board, boolean normalOrientation, ChessPosition selectedPosition) {
        int startRow = normalOrientation ? 1 : 8;
        int endRow = normalOrientation ? 8 : 1;
        int rowStep = normalOrientation ? 1 : -1;

        for (int row = startRow; row != endRow + rowStep; row += rowStep) {
            printRowLabel(row);
            printRowSquares(board, row, normalOrientation, selectedPosition);
            printRowLabel(row);
            System.out.println();
        }
    }

    private static void printRowLabel(int row) {
        System.out.print(LABEL_BACKGROUND + " " + row + " " + RESET);
    }

    private static void printRowSquares(ChessBoard board, int row, boolean normalOrientation, ChessPosition selectedPosition) {
        int startCol = normalOrientation ? 8 : 1;
        int endCol = normalOrientation ? 1 : 8;
        int colStep = normalOrientation ? -1 : 1;

        for (int col = startCol; col != endCol + colStep; col += colStep) {
            ChessPosition position = new ChessPosition(row, col);
            printSquare(board, position, selectedPosition);
        }
    }

    private static void printSquare(ChessBoard board, ChessPosition position, ChessPosition selectedPosition) {
        setSquareBackground(position);
        highlightValidMoves(board, position, selectedPosition);
        printPiece(board, position, selectedPosition);
        System.out.print(RESET);
    }

    private static void setSquareBackground(ChessPosition position) {
        System.out.print((position.getRow() + position.getColumn()) % 2 == 0 ?
                EscapeSequences.SET_BG_COLOR_DARK_GREY :
                EscapeSequences.SET_BG_COLOR_MAGENTA);
    }

    private static void highlightValidMoves(ChessBoard board, ChessPosition position, ChessPosition selectedPosition) {
        if (selectedPosition != null) {
            ChessPiece selectedPiece = board.getPiece(selectedPosition);
            if (selectedPiece != null) {
                Collection<ChessMove> validMoves = selectedPiece.pieceMoves(board, selectedPosition);
                ChessMove move = new ChessMove(selectedPosition, position, null);
                if (validMoves != null && validMoves.contains(move)) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                }
            }
        }
    }

    private static void printPiece(ChessBoard board, ChessPosition position, ChessPosition selectedPosition) {
        ChessPiece piece = board.getPiece(position);
        if (piece != null) {
            if (position.equals(selectedPosition)) {
                System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
            }
            System.out.print(getPieceSymbol(piece));
        } else {
            System.out.print(EscapeSequences.EMPTY);
        }
    }

    private static String getPieceSymbol(ChessPiece piece) {
        ChessGame.TeamColor color = piece.getTeamColor();
        switch (piece.getPieceType()) {
            case KING: return color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN: return color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK: return color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP: return color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT: return color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN: return color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            default: return EscapeSequences.EMPTY;
        }
    }
}