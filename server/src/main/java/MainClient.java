import chess.*;
import dataaccess.DataAccessException;
import server.Server;

public class MainClient {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        var server = new Server();

        //START SERVER
        server.run(8080);
    }
}