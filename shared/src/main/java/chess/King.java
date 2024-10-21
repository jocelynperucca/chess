package chess;

import java.util.Collection;
import java.util.HashSet;

//Calculates all valid moves a King can make given any position
public class King extends ChessPiece{
    public King (ChessGame.TeamColor pieceColor) {
        super(pieceColor, PieceType.KING );
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        //CHECK EACH WAY
        int[][] directions = {
                {1, 1},   // Up-right
                {1, -1},  // Down-right
                {-1, 1},  // Up-left
                {-1, -1}, // Down-left
                {1, 0}, // right
                {-1, 0}, // left
                {0, 1}, // up
                {0, -1}, // down
        };

        //Find all valid moves for King
        evaluateMove(board, position, moves, directions);

        return moves;
    }

}
