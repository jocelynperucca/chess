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
        //always start with team white
        teamTurn = TeamColor.WHITE;
        currentBoard = new ChessBoard();
        currentBoard.resetBoard();

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


            TeamColor teamColor = currentPiece.getTeamColor();
            for (ChessMove Checkmove : currentPiece.pieceMoves(currentBoard, startPosition)) {

                //if it doesn't put the king in check, you're okay
                if(!canCheck(teamColor, currentBoard)) {
                    //create copy board to see if move puts king in check
                    ChessBoard testBoard = currentBoard.copyBoard();
                    officialMove(Checkmove,currentPiece, testBoard);
                    if(!canCheck(currentPiece.getTeamColor(),testBoard)) {
                        validMoves.add(Checkmove);
                    }

                } else if (canCheck(teamColor, currentBoard)) {
                    //if it gets rid of check, you can move
                    if(ridOfCheck(Checkmove)) {
                        validMoves.add(Checkmove);
                    }
                }
            }

        //returns all valid moves for current board when checked for check or checkmate
        return validMoves;
    }

    //See if King needs to move (if any other piece can kill it)
    private boolean canCheck(TeamColor teamColor, ChessBoard currentBoard) {

        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                //set temporary position to check for moves to kill the king
                ChessPosition checkPosition = new ChessPosition(i, j);

                if(currentBoard.getPiece(checkPosition) != null && currentBoard.getPiece(checkPosition).getTeamColor() != teamColor) {
                    Collection<ChessMove> checkMoves = currentBoard.getPiece(checkPosition).pieceMoves(currentBoard,checkPosition);
                    ChessPosition kingPosition = findKing(currentBoard, teamColor);

                    //if one of these moves can kill the king, he's in check
                    for (ChessMove move : checkMoves) {
                        if(move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }

        //King is not in check
        return false;
    }

    //Check if a certain move from any piece will get a king out of check
    private boolean ridOfCheck(ChessMove move) {
        ChessBoard testBoard = currentBoard.copyBoard();
        ChessPiece testPiece = testBoard.getPiece(move.getStartPosition());
        officialMove(move, testPiece, testBoard);

        //will this move get the king out of check?
        return !canCheck(testPiece.getTeamColor(), testBoard);
    }

    //Find a kings position of any color
    private ChessPosition findKing(ChessBoard currentBoard, TeamColor teamColor) {
        for(int row = 1; row <= 8; row++) {
            for(int col = 1; col <= 8; col++) {

                //Set temporary position and get pieceType
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece testPiece = currentBoard.getPiece(newPosition);

                if (testPiece != null && testPiece.getPieceType() == ChessPiece.PieceType.KING && testPiece.getTeamColor() == teamColor) {
                    return newPosition; //found king
                }
            }
        }
        return null; //No king was found
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */

    public void makeMove(ChessMove move) throws InvalidMoveException {
        //get starting values
        ChessPosition startPosition = move.getStartPosition();

        if (currentBoard == null) {
            throw new InvalidMoveException();
        }

        if(currentBoard.getPiece(startPosition) == null) {
            throw new InvalidMoveException("Space is empty");
        }

        //check piece type, it's supposed valid moves, and color
        ChessPiece movePiece = currentBoard.getPiece(startPosition);
        Collection<ChessMove> validMoves = validMoves(startPosition);
        TeamColor movePieceColor = movePiece.getTeamColor();

        //Test tried move
        if(movePiece.getPieceType() == null) {
            throw new InvalidMoveException("Space is empty");

        } else if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Can't make move");

        } else if (movePieceColor != teamTurn) {
            throw new InvalidMoveException("Not your team");

        } else if (isInCheck(movePieceColor)) {
            //see if there's any moves to get out of check
            ChessBoard testBoard = currentBoard;
            officialMove(move, movePiece, testBoard);

            //check move or if move will put them in check
            if(!isInCheck(teamTurn)) {
                //free to move
                officialMove(move, movePiece,currentBoard);

            } else {
                //King in check and something needs to happen
                throw new InvalidMoveException("Your King is in danger");
            }

        } else {
            //You're good to move
            officialMove(move, movePiece, currentBoard);
        }

        //Change turns
        if(teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }

    //function to make a move with a piece after it is checked the move is good
    public void officialMove(ChessMove move, ChessPiece chessPiece, ChessBoard board) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        if(move.getPromotionPiece() != null) {
            chessPiece = new ChessPiece(teamTurn, move.getPromotionPiece());
        }

        // Add piece and remove other piece
        board.addPiece(endPosition, chessPiece);
        board.addPiece(startPosition, null);
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
        ChessBoard testBoard = currentBoard.copyBoard();

        //check on temporary board to see if the King is in danger
        if(!canCheck(teamTurn, testBoard)) {
            return false;
        }

        //Check each piece of the team to see if any move will protect the king
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i,j);
                ChessPiece checkPiece = testBoard.getPiece(currentPosition);

                if(checkPiece != null && checkPiece.getTeamColor() == teamColor) {

                    //Go through every valid move for checkPiece
                    for(ChessMove move : checkPiece.pieceMoves(testBoard,currentPosition)) {
                        ChessBoard checkBoard = currentBoard.copyBoard();
                        officialMove(move, checkPiece, checkBoard);

                        //Check to see if that move will protect the king
                        if(!canCheck(teamColor, checkBoard)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //stalemate is if team isn't in check and if they have NO valid moves
        if (canCheck(teamColor, currentBoard)) {
            return false; // not a stalemate if in check
        }

        // check for legal moves
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = currentBoard.getPiece(position);

                // check if current team is the same as the piece that is wanted to move
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = validMoves(position);

                    for (ChessMove move : possibleMoves) {
                        //check move on board
                        ChessBoard copiedBoard = currentBoard.copyBoard();
                        officialMove(move, piece, copiedBoard);

                        //if they get out of check, they're okay
                        if (!canCheck(teamColor, copiedBoard)) {
                            return false;
                        }
                    }
                }
            }
        }
        // STALEMATE
        return true;
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


    //OVERRIDE
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
