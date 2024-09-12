package chess;

import java.util.Collection;
import java.util.HashSet;

public class Pawn extends ChessPiece {

    public Pawn (ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, ChessPiece.PieceType.PAWN);
    }


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();

        ChessGame.TeamColor pieceColor = getTeamColor();



        //on starting row
        if ((position.getRow() == 2 && pieceColor == ChessGame.TeamColor.WHITE)|| (position.getRow() == 7 && pieceColor == ChessGame.TeamColor.BLACK)) {
            int[][] directions = {
                    {1,0}, //up one
                    {2,0} // up two
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

            //not on starting row
        } else {
            int[][] directions = {
                    {1,0} //up one
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

        }

        return moves;
    }



}
