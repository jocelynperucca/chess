package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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

    public ChessPiece(ChessPiece other) {
        this.pieceColor = other.pieceColor;
        this.type = other.type;
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
                Bishop bishop = new Bishop(pieceColor);
                return bishop.pieceMoves(board, myPosition);
            case ROOK:
                Rook rook = new Rook(pieceColor);
                return rook.pieceMoves(board, myPosition);
            case QUEEN:
                Rook queen = new Rook(pieceColor);
                Bishop queenBishop = new Bishop(pieceColor);
                Collection<ChessMove> queenPieceMoves = new HashSet<>();
                queenPieceMoves.addAll(queen.pieceMoves(board,myPosition));
                queenPieceMoves.addAll(queenBishop.pieceMoves(board,myPosition));
                return queenPieceMoves;
            case KNIGHT:
                Knight knight = new Knight(pieceColor);
                return knight.pieceMoves(board, myPosition);
            case PAWN:
                Pawn pawn = new Pawn(pieceColor);
                return pawn.pieceMoves(board, myPosition);
            case KING:
                King king = new King(pieceColor);
                return king.pieceMoves(board, myPosition);
        }

        return new ArrayList<>();
    }

    String hasPiece(ChessBoard board, ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        ChessPiece checkPiece = board.getPiece(move.getEndPosition());
        if(piece == null) {
            return "no piece to move";
        }
        if (checkPiece == null) {
            return "good";

        } else if (piece.getTeamColor() == checkPiece.getTeamColor()) {
            return "same team";

        } else {
            return "can capture";
        }
    }


    //Calculates valid moves for any given piece when given an array of moves it could make
    //Can move in one direction til it hits another piece (all pieces other than knight or King)
    public void evaluateMovesInDirection(ChessBoard board, ChessPosition position, Collection<ChessMove> moves, int[][] directions) {
        for(int[] direction: directions) {
            int rowOffset = direction[0];
            int colOffset = direction[1];

            label:
            for (int i = 1; i <= 8; i++) {
                int newRow = position.getRow() + rowOffset * i;
                int newCol = position.getColumn() + colOffset * i;

                // Check for valid board position
                if (newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessMove newMove = new ChessMove(position, newPosition, null);

                    //see if spot already has a piece, therefore making it valid or invalid
                    String moveStatus = hasPiece(board, newMove);

                    switch (moveStatus) {
                        case "good":
                            moves.add(newMove);
                            break;
                        case "can capture":
                            moves.add(newMove);
                            break label; // Stop after capturing
                        case "same team":
                            break label; // Stop if the piece is of the same team
                    }
                } else {
                    break; // Stop if out of bounds
                }
            }
        }
    }

    //Calculates valid moves for any given piece when given an array of moves it could make
    // Can move in when has set moves it can make (Knight and King)
    public void evaluateMove(ChessBoard board, ChessPosition position, Collection<ChessMove> moves, int[][] directions) {

        for (int[] direction : directions) {
            int rowOffset = direction[0];
            int colOffset = direction[1];

            int newRow = position.getRow() + rowOffset;
            int newCol = position.getColumn() + colOffset;

            // Check if the new position is within the bounds of the chessboard
            if (newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessMove newMove = new ChessMove(position, newPosition, null);

                //Checks if space is already occupied
                String result = hasPiece(board, newMove);

                if (result.equals("good") || result.equals("can capture")) {
                    moves.add(newMove);
                }
            }
        }
    }



    @Override
    public boolean equals(Object o) {
        if (this == o)  {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }


}




