package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

//figure out toString

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    //compares positions

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        //throw new RuntimeException("Not implemented");
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        switch (piece.getPieceType()) {
            case BISHOP:
                Bishop bishop = new Bishop(pieceColor, type);
                return bishop.pieceMoves(board, myPosition);
            case ROOK:
                Rook rook = new Rook(pieceColor, type);
                return rook.pieceMoves(board, myPosition);
            case QUEEN:
                Rook queen = new Rook(pieceColor, type);
                Bishop queenBishop = new Bishop(pieceColor, type);
                Collection<ChessMove> queenPieceMoves = new HashSet<>();
                queenPieceMoves.addAll(queen.pieceMoves(board,myPosition));
                queenPieceMoves.addAll(queenBishop.pieceMoves(board,myPosition));
                return queenPieceMoves;
            case KNIGHT:
                Knight knight = new Knight(pieceColor, type);
                return knight.pieceMoves(board, myPosition);
            case PAWN:
                Pawn pawn = new Pawn(pieceColor, type);
                return pawn.pieceMoves(board, myPosition);
            case KING:
                King king = new King(pieceColor, type);
                return king.pieceMoves(board, myPosition);





        }
//        if (PieceType.BISHOP.equals(type)) {
//
//            Bishop bishop = new Bishop(pieceColor, type);
//            return bishop.pieceMoves(board, myPosition);
//
//        }
        return new ArrayList<>();


    }
}


