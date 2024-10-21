package chess;
import java.util.Collection;
import java.util.HashSet;


//Calculates possible moves for the bishop piece
public class Bishop extends ChessPiece {

    public Bishop (ChessGame.TeamColor pieceColor) {
        super(pieceColor, PieceType.BISHOP );
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        //CHECK EACH WAY
        int[][] directions = {
                {1, 1},   // Up-right
                {1, -1},  // Down-right
                {-1, 1},  // Up-left
                {-1, -1}  // Down-left
        };

        //find valid moves for a bishop and add them to Collection moves
        evaluateMovesInDirection(board, position, moves, directions);

        return moves;
    }
}
