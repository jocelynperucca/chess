package chess;

import java.util.Collection;
import java.util.HashSet;

public class Knight extends ChessPiece {

    public Knight (ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, ChessPiece.PieceType.KNIGHT);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();


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
