package chess;
import java.util.Collection;

public interface PieceMovesCalculator {

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    //receive type of piece...switch case

}

