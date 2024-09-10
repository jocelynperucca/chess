package chess;
import java.util.Collection;

public abstract class PieceMovesCalculator {

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    boolean validPosition(ChessBoard board, ChessPosition position) {
        return position.getRow() >= 0 && position.getRow() < 8
                && position.getColumn() >= 0 && position.getColumn() < 8;

        //GET COLOR by uppercase or lower? fimd if occupied
    }

    //receive type of piece...switch case

}
