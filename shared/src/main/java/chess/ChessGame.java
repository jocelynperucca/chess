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
            for (ChessMove checkMove : currentPiece.pieceMoves(currentBoard, startPosition)) {

                //if it doesn't put the king in check, you're okay
                if(!canCheck(teamColor, currentBoard)) {
                    //create copy board to see if move puts king in check
                    ChessBoard testBoard = currentBoard.copyBoard();
                    officialMove(checkMove,currentPiece, testBoard);
                    if(!canCheck(currentPiece.getTeamColor(),testBoard)) {
                        validMoves.add(checkMove);
                    }

                } else if (canCheck(teamColor, currentBoard)) {
                    //if it gets rid of check, you can move
                    if(ridOfCheck(checkMove)) {
                        validMoves.add(checkMove);
                    }
                }
            }

        //returns all valid moves for current board when checked for check or checkmate
        return validMoves;
    }

    //See if King needs to move (if any other piece can kill it)
    private boolean canCheck(TeamColor teamColor, ChessBoard currentBoard) {
        ChessPosition kingPosition = findKing(currentBoard, teamColor);

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (canPieceKill(currentBoard, new ChessPosition(i, j), teamColor, kingPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    //See if piece is available to kill King
    private boolean canPieceKill(ChessBoard board, ChessPosition position, TeamColor kingColor, ChessPosition kingPosition) {
        ChessPiece piece = board.getPiece(position);
        if (piece == null || piece.getTeamColor() == kingColor) {
            return false;
        }
        //check if any of the moves match the king position
        Collection<ChessMove> moves = piece.pieceMoves(board, position);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
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

        if (!canCheck(teamTurn, testBoard)) {
            return false;
        }

        return !canMovePreventCheck(teamColor, testBoard);
    }

    //Check the individual moves if they can prevent check, calls on the pieces too
    private boolean canMovePreventCheck(TeamColor teamColor, ChessBoard board) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (canPiecePreventCheck(piece, currentPosition, board)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //Check if a single piece can prevent check by moving in the way of attackers
    private boolean canPiecePreventCheck(ChessPiece piece, ChessPosition position, ChessBoard board) {
        for (ChessMove move : piece.pieceMoves(board, position)) {
            ChessBoard checkBoard = currentBoard.copyBoard();
            officialMove(move, piece, checkBoard);

            if (!canCheck(piece.getTeamColor(), checkBoard)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (canCheck(teamColor, currentBoard)) {
            return false; // not a stalemate if in check
        }
        return !hasLegalMoves(teamColor);
    }

    //sees if a color has any legal moves at all
    private boolean hasLegalMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if (hasLegalMoveForPiece(teamColor, row, col)) {
                    return true; // Found a legal move, not a stalemate
                }
            }
        }
        return false; // No legal moves found, it's a stalemate
    }

    //checks individual move if they have legal moves
    private boolean hasLegalMoveForPiece(TeamColor teamColor, int row, int col) {
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = currentBoard.getPiece(position);

        if (piece == null || piece.getTeamColor() != teamColor) {
            return false;
        }

        Collection<ChessMove> possibleMoves = validMoves(position);
        for (ChessMove move : possibleMoves) {
            ChessBoard copiedBoard = currentBoard.copyBoard();
            officialMove(move, piece, copiedBoard);
            if (!canCheck(teamColor, copiedBoard)) {
                return true; // Found a legal move
            }
        }
        return false; // No legal moves for this piece
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(currentBoard, chessGame.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, currentBoard);
    }
}
