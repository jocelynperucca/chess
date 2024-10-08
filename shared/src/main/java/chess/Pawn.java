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


//WHITE PAWN
        if(ChessGame.TeamColor.WHITE == pieceColor) {
            //on starting row for WHITE
            if ((position.getRow() == 2)) {
                int[][] directions = {
                        {1,0}, //up one
                        {2,0}, // up two
                        {1,-1}, //diagonal up left
                        {1,1} //diagonal up right
                };

                for (int[] direction : directions) {
                    int rowOffset = direction[0];
                    int colOffset = direction[1];
                    //check if is in bounds
                    int newRow = position.getRow() + rowOffset;
                    int newCol = position.getColumn() + colOffset;
                    if(newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        ChessMove newMove = new ChessMove(position, newPosition, null);
                        String result = hasPiece(board, newMove);

                        //check diagonals
                        if ((rowOffset == 1 && colOffset == 1) || (rowOffset == 1 && colOffset == -1)) {
                            if (result.equals("can capture")) {
                                moves.add(newMove);
                            }
                            //else check if blocked
                        } else {
                            if (!"can capture".equals(result) && !"same team".equals(result)) {
                                //check if can't hop other piece
                                if (rowOffset != 2) {
                                    moves.add(newMove);
                                } else {
                                    ChessPosition behindPosition = new ChessPosition(newRow -1, newCol);
                                    ChessMove behindMove = new ChessMove(position, behindPosition, null);
                                    String behindResult = hasPiece(board, behindMove);
                                    if(!behindResult.equals("same team")) {
                                        moves.add(newMove);
                                    }
                                }

                            }
                        }

                    } else {
                        break;
                    }

                }

                //not on starting row for WHITE
            } else {
                int[][] directions = {
                        {1,0}, //up one
                        {1,-1}, //diagonal up left
                        {1,1} //diagonal up right
                };

                for (int[] direction : directions) {
                    int rowOffset = direction[0];
                    int colOffset = direction[1];
                    //check if is in bounds
                    int newRow = position.getRow() + rowOffset;
                    int newCol = position.getColumn() + colOffset;
                    if(newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        ChessMove newMove = new ChessMove(position, newPosition, null);
                        String result = hasPiece(board, newMove);

                        //check diagonals
                        if ((rowOffset == 1 && colOffset == 1) || (rowOffset == 1 && colOffset == -1)) {
                            if (result.equals("can capture")) {
                                if (canPromote(newRow)) {
                                    moves.add(new ChessMove(position, newPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(position, newPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(position, newPosition, PieceType.KNIGHT));
                                    moves.add(new ChessMove(position, newPosition, PieceType.BISHOP));



                                } else {
                                    moves.add(newMove);
                                }

                            }
                            // check if blocked
                        } else {
                            if (!"can capture".equals(result) && !"same team".equals(result)) {
                                if (canPromote(newRow)) {
                                    moves.add(new ChessMove(position, newPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(position, newPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(position, newPosition, PieceType.KNIGHT));
                                    moves.add(new ChessMove(position, newPosition, PieceType.BISHOP));



                                } else {
                                    moves.add(newMove);
                                }
                            }
                        }
                    } else {
                        break;
                    }
                }
            }

//BLACK PAWN
        } else if (pieceColor == ChessGame.TeamColor.BLACK) {
            //on starting row for BLACK
            if ((position.getRow() == 7)) {
                int[][] directions = {
                        {-1,0}, //up one
                        {-2,0}, // up two
                        {-1,-1}, //diagonal down left
                        {-1,1} // diagonal down right
                };

                for (int[] direction : directions) {
                    int rowOffset = direction[0];
                    int colOffset = direction[1];


                    int newRow = position.getRow() + rowOffset;
                    int newCol = position.getColumn() + colOffset;
                    if(newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        ChessMove newMove = new ChessMove(position, newPosition, null);
                        String result = hasPiece(board, newMove);

                        //check diagonals
                        if((rowOffset == -1 && colOffset == -1) || (rowOffset == -1 && colOffset == 1)) {
                            if (result.equals("can capture")) {
                                moves.add(newMove);
                            }
                            //else normal move
                        } else {
                            // checks straight forward blockage
                            if (!result.equals("can capture") && !result.equals("same team")) {
                                if (rowOffset != -2) {
                                    moves.add(newMove);
                                }   else {
                                    ChessPosition behindPosition = new ChessPosition(newRow +1, newCol);
                                    ChessMove behindMove = new ChessMove(position, behindPosition, null);
                                    String behindResult = hasPiece(board, behindMove);
                                    if (!"same team".equals(behindResult)) {
                                        moves.add(newMove);
                                    }

                                }

                                //moves.add(newMove);
                            }
                        }

                    } else {
                        break;
                    }

                }

                //not on starting row for BLACK
            } else {
                int[][] directions = {
                        {-1,0}, //up one
                        {-1,-1}, //diagonal down left
                        {-1,1} // diagonal down right
                };

                for (int[] direction : directions) {
                    int rowOffset = direction[0];
                    int colOffset = direction[1];

                    //CHECK IF IN BOUNDS
                    int newRow = position.getRow() + rowOffset;
                    int newCol = position.getColumn() + colOffset;
                    if(newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) {
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        ChessMove newMove = new ChessMove(position, newPosition, null);
                        String result = hasPiece(board, newMove);

                        //check diagonals
                        if ((rowOffset == -1 && colOffset == -1) || (rowOffset == -1 && colOffset == 1)) {
                            if (result.equals("can capture")) {

                                //CHECK PROMOTION
                                if (canPromote(newRow)) {
                                    moves.add(new ChessMove(position, newPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(position, newPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(position, newPosition, PieceType.KNIGHT));
                                    moves.add(new ChessMove(position, newPosition, PieceType.BISHOP));





                                } else {
                                    moves.add(newMove);
                                }
                            }
                        } else {
                            //if in front of piece, cannot capture
                            if (!"can capture".equals(result) && !"same team".equals(result)) {

                                //CHECK PROMOTION
                                if (canPromote(newRow)) {
                                    //moves.add(newMove);
                                    moves.add(new ChessMove(position, newPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(position, newPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(position, newPosition, PieceType.KNIGHT));
                                    moves.add(new ChessMove(position, newPosition, PieceType.BISHOP));
                                } else {
                                    moves.add(newMove);
                                }

                            }
                        }

                        //moves.add(newMove);
                    } else {
                        break;
                    }
                }
            }

        }
        //RETURN PAWN MOVES
        return moves;
    }

    public boolean canPromote (int row) {
        return ((row == 8 && ChessGame.TeamColor.WHITE == getTeamColor()) || (row == 1 && ChessGame.TeamColor.BLACK == getTeamColor()));
    }



}
