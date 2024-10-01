package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard currentBoard;


    public ChessGame() {

        teamTurn = TeamColor.WHITE;

    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new HashSet<>();
        ChessPiece currentPiece = currentBoard.getPiece(startPosition);

        if(currentPiece== null) {


        } else {
            TeamColor teamColor = currentPiece.getTeamColor();
            for (ChessMove Checkmove : currentPiece.pieceMoves(currentBoard, startPosition)) {
                if(canCheck(teamColor,currentBoard) == false) {
                    validMoves.add(Checkmove);
                }

            }
            //
        }

        return validMoves;
    }


//See if King needs to move
    private boolean canCheck(TeamColor teamColor, ChessBoard currentBoard) {
        TeamColor currentTeam = getTeamTurn();

        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition checkPosition = new ChessPosition(i, j);
                if(currentBoard.getPiece(checkPosition) != null && currentBoard.getPiece(checkPosition).getTeamColor() != teamColor) {
                    Collection<ChessMove> checkMoves = currentBoard.getPiece(checkPosition).pieceMoves(currentBoard,checkPosition);
                    for (ChessMove move : checkMoves) {
                        ChessPosition kingPosition = findKing(currentBoard, currentTeam);
                        if(move.getEndPosition().equals(kingPosition)) {
                            return true;
                            //KING POSITOIN HERE)
                        }
                    }
                }
            }
        }
        return false;
    }

    private ChessPosition findKing(ChessBoard currentBoard, TeamColor teamColor) {
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition newPosition = new ChessPosition(i, j);
                if(currentBoard.getPiece(newPosition).getPieceType() == ChessPiece.PieceType.KING && currentBoard.getPiece(newPosition).getTeamColor() != teamColor) {
                    return newPosition;
                }
            }
        }
        return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return canCheck(teamColor, currentBoard);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(currentBoard, chessGame.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, currentBoard);
    }
}
