package chess;
import java.util.ArrayList;
import java.util.Collection;

public class Rook extends ChessPiece {
    public Rook(ChessGame.TeamColor pieceColor, ChessPiece.PieceType pieceType) {
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

        for (int[] direction : directions) {
            int rowOffset = direction[0];
            int colOffset = direction[1];
            boolean stop = false;

            for (int i = 1; i <= 8; i++) {

                int newRow = position.getRow() + rowOffset * i;
                int newCol = position.getColumn() + colOffset * i;
                if(newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessMove newMove = new ChessMove(position, newPosition, null);
                    if(hasPiece(board, newMove).equals("good")) {
                        moves.add(newMove);
                    } else if (hasPiece(board, newMove).equals("can capture")) {
                        moves.add(newMove);
                        stop = true;
                    } else if (hasPiece(board, newMove).equals("same team")) {
                        stop = true;
                    }
                    if (stop) {
                        break;
                    }
                    //moves.add(newMove);
                } else {
                    break;
                }

            }
        }

        return moves;

    }
}


