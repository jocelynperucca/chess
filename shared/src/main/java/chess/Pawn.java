package chess;

import java.util.Collection;
import java.util.HashSet;

public class Pawn extends ChessPiece {
    public Pawn(ChessGame.TeamColor pieceColor) {
        super(pieceColor, ChessPiece.PieceType.PAWN);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();
        ChessGame.TeamColor pieceColor = getTeamColor();

        int[][] directions = getDirections(pieceColor, position.getRow());
        int forwardDirection = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        for (int[] direction : directions) {
            int newRow = position.getRow() + direction[0];
            int newCol = position.getColumn() + direction[1];

            if (!isValidPosition(newRow, newCol)) {
                continue;
            }

            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessMove newMove = new ChessMove(position, newPosition, null);
            String result = hasPiece(board, newMove);

            if (isDiagonalMove(direction)) {
                handleDiagonalMove(moves, position, newPosition, result, newRow);
            } else {
                handleForwardMove(moves, board, position, newPosition, result, direction[0], forwardDirection);
            }
        }

        return moves;
    }

    private int[][] getDirections(ChessGame.TeamColor pieceColor, int currentRow) {
        boolean isStartingRow = (pieceColor == ChessGame.TeamColor.WHITE && currentRow == 2) ||
                (pieceColor == ChessGame.TeamColor.BLACK && currentRow == 7);
        int forward = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        if (isStartingRow) {
            return new int[][] {{forward, 0}, {2 * forward, 0}, {forward, -1}, {forward, 1}};
        } else {
            return new int[][] {{forward, 0}, {forward, -1}, {forward, 1}};
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row > 0 && row < 9 && col > 0 && col < 9;
    }

    private boolean isDiagonalMove(int[] direction) {
        return direction[1] != 0;
    }

    private void handleDiagonalMove(Collection<ChessMove> moves, ChessPosition position, ChessPosition newPosition, String result, int newRow) {
        if ("can capture".equals(result)) {
            handlePromotionIfNeeded(moves, position, newPosition, newRow);
        }
    }

    private void handleForwardMove(Collection<ChessMove> moves, ChessBoard board, ChessPosition position, ChessPosition newPosition, String result, int rowOffset, int forwardDirection) {
        if (!"can capture".equals(result) && !"same team".equals(result)) {
            if (Math.abs(rowOffset) != 2) {
                handlePromotionIfNeeded(moves, position, newPosition, newPosition.getRow());
            } else {
                ChessPosition behindPosition = new ChessPosition(newPosition.getRow() - forwardDirection, newPosition.getColumn());
                ChessMove behindMove = new ChessMove(position, behindPosition, null);
                String behindResult = hasPiece(board, behindMove);
                if (!"same team".equals(behindResult)) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }
        }
    }

    private boolean canPromote(int row) {
        return (row == 8 && getTeamColor() == ChessGame.TeamColor.WHITE) ||
                (row == 1 && getTeamColor() == ChessGame.TeamColor.BLACK);
    }

    private void handlePromotionIfNeeded(Collection<ChessMove> moves, ChessPosition position, ChessPosition endPosition, int newRow) {
        if (canPromote(newRow)) {
            moves.add(new ChessMove(position, endPosition, PieceType.QUEEN));
            moves.add(new ChessMove(position, endPosition, PieceType.ROOK));
            moves.add(new ChessMove(position, endPosition, PieceType.KNIGHT));
            moves.add(new ChessMove(position, endPosition, PieceType.BISHOP));
        } else {
            moves.add(new ChessMove(position, endPosition, null));
        }
    }
}