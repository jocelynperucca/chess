package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessBoard board;

    @Override
    public String toString() {
        return "ChessMove{" +
                "board=" + board +
                '}';
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
    }



    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition(ChessPiece targetPiece) {
        ChessPiece[][] squares  = board.getSquares();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = squares[row][col];
                if (piece != null && piece.equals(targetPiece)) {
                    return new ChessPosition(row + 1, col + 1);
                }
            }


        }
        return null;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        throw new RuntimeException("Not implemented");
        //enter in same that found in start position
    }

//    @Override
//    public String toString() {
//        return "ChessMove{" +
//                "board=" + board +
//                '}';
//    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        throw new RuntimeException("Not implemented");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(board, chessMove.board);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(board);
    }
}

