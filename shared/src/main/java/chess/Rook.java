package chess;
import java.util.ArrayList;
import java.util.Collection;

//calculates possible moves for Rook
public class Rook extends ChessPiece {
    public Rook(ChessGame.TeamColor pieceColor) {
        super(pieceColor, PieceType.ROOK);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] directions = {
                {0, 1},   // Up
                {0, -1},  // Down
                {-1, 0},  // left
                {1, 0}  // Right
        };

        //find valid moves for a rook and adds to Collection moves
        evaluateMovesInDirection(board, position, moves, directions);

        return moves;
    }
}


