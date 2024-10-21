package chess;

import java.util.Collection;
import java.util.HashSet;

//Calculates all valid moves a knight can make in a certain position on the current board
public class Knight extends ChessPiece {

    public Knight (ChessGame.TeamColor pieceColor) {
        super(pieceColor, ChessPiece.PieceType.KNIGHT);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();


//CHECK EACH WAY
        int[][] directions = {
                {2, 1},   // right 2 then up
                {1, 2},  // right 1 then up 2
                {2, -1},  // right 2 then down
                {1, -2}, // right then down 2
                {-1, 2}, // left then up 2
                {-2, 1}, // left 2 then up
                {-1, -2}, // left 1 then down 2
                {-2, -1} // left 2 then down
        };

        evaluateMove(board, position, moves, directions);

        return moves;
    }

}
