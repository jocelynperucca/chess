package chess;

import java.util.Collection;
import java.util.HashSet;

public class King extends ChessPiece{

    public King (ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, PieceType.KING );
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();


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

        for (int[] direction : directions) {
            int rowOffset = direction[0];
            int colOffset = direction[1];

                int newRow = position.getRow() + rowOffset;
                int newCol = position.getColumn() + colOffset;
                if(newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessMove newMove = new ChessMove(position, newPosition, null);
                    moves.add(newMove);
                } else {
                    break;

            }
        }

        return moves;

    }

}
