package chess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class Bishop extends ChessPiece {

    public Bishop (ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, PieceType.BISHOP );
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();


//CHECK EACH WAY
        int[][] directions = {
                {1, 1},   // Up-right
                {1, -1},  // Down-right
                {-1, 1},  // Up-left
                {-1, -1}  // Down-left
        };

        for (int[] direction : directions) {
            int rowOffset = direction[0];
            int colOffset = direction[1];

            for (int i = 1; i <= 8; i++) {

                int newRow = position.getRow() + rowOffset * i;
                int newCol = position.getColumn() + colOffset * i;
                if(newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessMove newMove = new ChessMove(position, newPosition, null);
                    moves.add(newMove);
                } else {
                    break;
                }

            }
        }

        return moves;

    }


}
