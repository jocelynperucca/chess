package chess;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    public ChessPiece[][] getSquares() {
        return squares;
    }

    //memory address override
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }



    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;


    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        //CLEAR BOARD
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                squares[row][col] = null;
            }
        }

        //white pieces
        squares[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        //pawns
        for (int col = 0; col < 8; col++) {
            squares[1][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }

        //black pieces
        squares[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        //black pawns
        for (int col = 0; col < 8; col++) {
            squares[6][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Iterate over the rows of the chessboard
        for (int row = 7; row >= 0; row--) { // Start from the top row (index 7) to show in traditional board format
            sb.append((row + 1)).append(" "); // Add row number for better readability
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = squares[row][col];
                if (piece == null) {
                    sb.append("- "); // Empty square
                } else {
                    sb.append(pieceToString(piece)).append(" "); // Display piece
                }
            }
            sb.append("\n"); // Newline at the end of each row
        }

        // Add column labels at the bottom
        sb.append("  1 2 3 4 5 6 7 8\n");

        return sb.toString();
    }

    private String pieceToString(ChessPiece piece) {
        char typeChar = ' ';

        switch (piece.getPieceType()) {
            case KING:
                typeChar = 'K';
                break;
            case QUEEN:
                typeChar = 'Q';
                break;
            case ROOK:
                typeChar = 'R';
                break;
            case BISHOP:
                typeChar = 'B';
                break;
            case KNIGHT:
                typeChar = 'N';
                break;
            case PAWN:
                typeChar = 'P';
                break;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            typeChar = Character.toLowerCase(typeChar);
            // Return something like 'WK' for White King or 'BP' for Black Pawn
        }
        return String.valueOf(typeChar);
    }


}
