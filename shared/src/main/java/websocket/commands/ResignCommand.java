package websocket.commands;

public class ResignCommand extends UserGameCommand {
    private final boolean inGame;

    public ResignCommand(String authToken, int gameID, boolean inGame) {
        super(CommandType.RESIGN, authToken, gameID);
        this.inGame = inGame;
    }

    public boolean getInGame() {
        return inGame;
    }

}
