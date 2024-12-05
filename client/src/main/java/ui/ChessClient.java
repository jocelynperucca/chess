package ui;

import WebSocket.NotificationHandler;
import WebSocket.WebSocketFacade;
import chess.*;
import dataaccess.DataAccessException;
import dataaccess.SQLGameDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ConnectionManager;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.sql.SQLException;
import java.util.*;
import java.io.PrintStream;

public class ChessClient {

    private final PrintStream out;
    private State state = State.SIGNEDOUT;
    Scanner scanner = new Scanner(System.in);
    private final ServerFacade server;
    private AuthData authData;
    private final String serverUrl;
    private ChessBoard chessBoard = new ChessBoard();
    private WebSocketFacade ws;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade.ServerMessageListener serverMessageListener;
    boolean inGameplay = false;
    private String playerColor;
    private ChessGame currentGame;
    private int gameID;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.out = new PrintStream(System.out, true);
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        this.serverMessageListener = new WebSocketFacade.ServerMessageListener() {
            @Override
            public void onLoadGame(LoadGameMessage message) {
                // Handle game load
                currentGame = message.getGame();
                chessBoard = currentGame.getBoard();
                ChessBoardDraw.drawChessBoard(chessBoard,null);
                String setTextColorRed = EscapeSequences.SET_TEXT_COLOR_RED;
                String resetTextColor = EscapeSequences.RESET_TEXT_COLOR;
                System.out.println(setTextColorRed + ">> " + "Game Loaded" + resetTextColor);
            }

            @Override
            public void onNotification(NotificationMessage message) {
                // Handle notification
                String setTextColorRed = EscapeSequences.SET_TEXT_COLOR_RED;
                String resetTextColor = EscapeSequences.RESET_TEXT_COLOR;
                System.out.println( setTextColorRed  + ">> " + message.getMessage() + resetTextColor);
            }

            @Override
            public void onError(ErrorMessage message) {
                // Handle error
                System.err.println("Error: " + message.getErrorMessage());
            }
        };
    }

    //evaluation of any given user command
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens [0] : "help";

            //Switch statement for given commands based on user input
            return switch (cmd) {
                case "register" -> register(out);
                case "login" -> login(out);
                case "logout" -> logout(out);
                case "create" -> createGame(out);
                case "list" -> listGames(out);
                case "play" -> joinGame(out);
                case "observe" -> observe(out);
                case "help" -> help();
                case "redraw" -> redrawChessboard(out);
                case "make" -> makeMove(out);
                case "highlight" -> highlight(out);
                case "leave" -> leave(out);
                case "resign" -> resign(out);
                case " " -> help();
                default -> "Invalid command";
            };
        } catch (Exception e) {
            return ("Invalid command");
        }
    }

    //function to register on the client end
    public String register(PrintStream out) {

        //register ui and set username, password, and email based on user input
        out.println("Registering a new account:");
        out.print("Choose Username: ");
        String userName = scanner.nextLine();
        out.print("New password: ");
        String password = scanner.nextLine();
        out.print("Enter email: ");
        String email = scanner.nextLine();

        //check if valid
        if (userName.isEmpty() || password.isEmpty() || email.isEmpty()) {
            out.println("You are missing some of the key information");
            return "Registration failed: missing information";
        }

        //set userData
        UserData userData = new UserData(userName, password, email);

        //try to register with given userData, throw exception if not
        try {
            authData = server.register(userData);
            state = State.SIGNEDIN;
            return "Registration successful!";
        } catch (ResponseException e) {
            return "Registration failed: ";
        }
    }

    //function to log in from the client end
    public String login(PrintStream out) {

        //user interface for login and set username and password
        out.println("Login:");
        out.print("Enter username: ");
        String userName = scanner.nextLine();
        out.print("Enter password: ");
        String password = scanner.nextLine();

        //check if valid/empty
        if (userName.isEmpty() || password.isEmpty()) {
            out.println("Not a valid username or password entry.");
            return "Login failed: missing info";
        }

        //userData to log in
        UserData userData = new UserData(userName, password, null);

        //try to login with given userdata, throw exception if not
        try {
            //ws = new WebSocketFacade(serverUrl, notificationHandler);
            server.setWebsocket(ws);
            AuthData loginResponse = server.login(userData);
            String authToken = loginResponse.getAuthToken();
            authData = new AuthData(userName, authToken);
            state = State.SIGNEDIN;
            return "Login successful!";
        } catch (ResponseException e) {
            return "Login failed: ";
        }
    }

    //function to logout from the client end
    public String logout(PrintStream out) throws ResponseException {
        assertSignedIn();
        out.println("Logging out");

        try {
            server.logout(authData);
            state = State.SIGNEDOUT;
            return "Logged Out";
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    //function to list games from the client end
    public String listGames(PrintStream out) throws ResponseException {
        assertSignedIn();
        out.println("Fetching game list...");

        Collection<GameData> games = server.listGames(authData);

        if (games == null || games.isEmpty()) {
            return "No games available.";
        }

        // Store games in an ArrayList and reverse the order
        List<GameData> gameList = new ArrayList<>(games);
        Collections.reverse(gameList); // Reverse to display oldest first

        // Display games with sequential numbering
        StringBuilder gameListBuilder = new StringBuilder("Available Games:\n");
        for (int i = 0; i < gameList.size(); i++) {
            gameListBuilder.append(i + 1).append(". ").append(gameList.get(i).toString()).append("\n");
        }

        //assign gameDisplay and output it to the console
        String gameDisplay = gameListBuilder.toString();
        return gameDisplay;
    }

    //Function to join game from the client end
    public String joinGame(PrintStream out) throws ResponseException {
        assertSignedIn();
        out.println("Joining Game...");

        Collection<GameData> games = server.listGames(authData);

        if (games == null || games.isEmpty()) {
            return "No games available.";
        }

        // Store games in an ArrayList and reverse the order
        List<GameData> gameList = new ArrayList<>(games);
        Collections.reverse(gameList); // Reverse to display to mirror what happens in listGames

        //set number
        out.print("Enter the number of the game you want to join: ");
        int selectedNumber;
        try {
            selectedNumber = Integer.parseInt(scanner.nextLine());
            if (selectedNumber < 1 || selectedNumber > gameList.size()) {
                return "Invalid selection: please enter a valid game number.";
            }
        } catch (NumberFormatException e) {
            return "Invalid format: please enter a number.";
        }

        //Get selected game and set gameID
        GameData selectedGame = gameList.get(selectedNumber - 1);
        gameID = selectedGame.getGameID();

        //set player color
        out.print("Enter 'white' or 'black' to choose your player color: ");
        playerColor = scanner.nextLine();

        //try to join game, if not, return exception
        try {
            currentGame = server.joinGame(playerColor, gameID, authData);
            String message = "Successfully joined game " + selectedGame.getGameName() + " as " + playerColor;

            ws = new WebSocketFacade(serverUrl, notificationHandler, serverMessageListener);
            server.setWebsocket(ws);
            inGameplay = true;
            try {
                ws.joinPlayerSend(gameID, playerColor, authData.getAuthToken());
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
            return message;
        } catch (ResponseException e) {
            return "Failed to join game, check player color or status of game";
        }
    }

    public String createGame(PrintStream out) {
        out.println("Creating Game...");

        //Set game name
        out.print("Enter a name for your game: ");
        String gameName = scanner.nextLine();

        if (gameName.isEmpty()) {
            return "Game name cannot be empty";
        }

        //try to create game with given name, throws exception if cannot
        try {
            server.createGame(gameName, authData);
            String message = "Game created: " + gameName;
            out.println("Join the game through the menu to start playing");
            return message;
        } catch (ResponseException e) {
            return "Failed to create game";
        }
    }

    public String observe(PrintStream out) throws ResponseException {

        // Store games in an ArrayList and reverse the order
        Collection<GameData> games = server.listGames(authData);
        List<GameData> gameList = new ArrayList<>(games);
        Collections.reverse(gameList);

        // Display games with sequential numbering
        StringBuilder gameListBuilder = new StringBuilder("Available Games:\n");
        for (int i = 0; i < gameList.size(); i++) {
            gameListBuilder.append(i + 1).append(". ").append(gameList.get(i).toString()).append("\n");
        }

        //Choose game number to observe
        out.print("Enter the number of the game you want to observe: ");
        int selectedNumber;
        try {
            selectedNumber = Integer.parseInt(scanner.nextLine());
            if (selectedNumber < 1 || selectedNumber > gameList.size()) {
                return "Invalid selection: please enter a valid game number.";
            }
        } catch (NumberFormatException e) {
            return "Invalid format: please enter a number.";
        }
        //get selectedGame number
        GameData selectedGame = gameList.get(selectedNumber - 1);
        gameID = selectedGame.getGameID();
        ws = new WebSocketFacade(serverUrl, notificationHandler, serverMessageListener);
        server.setWebsocket(ws);
        currentGame = selectedGame.getGame();
        chessBoard = currentGame.getBoard();
        ws.joinPlayerSend(gameID, "observer", authData.getAuthToken());

        //Successfully entered game to observe
        return "Observing game: " + selectedGame.getGameName();
    }

    public String redrawChessboard(PrintStream out) throws ResponseException {
        assertInGameplay();
        out.println("Printing chessboard...");
        ChessBoardDraw.drawChessBoard(chessBoard,null);
        return "Current Chessboard";

    }

    public String makeMove(PrintStream out) throws ResponseException, InvalidMoveException {
        assertInGameplay();
        if (currentGame.isGameOver()) {
            return "Game is over, cannot make a move";
        }

        out.println("Making Move...");
        out.println("Enter coordinates of piece you want to move: ");
        String coordinates = scanner.nextLine();
        if (!validCoordinates(coordinates)) {
            out.println("Coordinate does not exist");
            makeMove(out);
        }
        ChessPosition start = parseChessPosition(coordinates);
        ChessPiece piece = chessBoard.getPiece(start);
        ChessGame.TeamColor teamColor = stringtoTeamColor(playerColor);
        if(piece == null) {
            return "There is no piece there!";
        }

        if (piece.getTeamColor() != teamColor) {
            out.println("This piece isn't yours, choose another");
            makeMove(out);
        }
        out.println("Enter coordinates where you want to move piece: ");
        String endCoordinates = scanner.nextLine();

        if (!validCoordinates(endCoordinates)) {
            out.println("Coordinate does not exist");
            makeMove(out);
        }
        ChessPosition end = parseChessPosition(endCoordinates);
        ChessGame game = new ChessGame();
        ChessMove tryMove = new ChessMove(start, end, null);
        try {
            ws.makeMoveSend(authData.getAuthToken(), gameID, tryMove);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        if(canPromote(piece, tryMove)) {
            out.println("What would you like to sub your piece for? [Q|R|B|N] : ");
            String promoteType = scanner.nextLine();
            ChessPiece.PieceType promotionPiece = setPieceType(promoteType);
            tryMove = new ChessMove(start, end, promotionPiece);
        }

        return "Made move";

    }

    private boolean canPromote(ChessPiece piece, ChessMove move) {
        return ((piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.WHITE && move.getEndPosition().getRow() == 8)) ||
                (piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.BLACK && move.getEndPosition().getRow() == 1);
    }

    private ChessPiece.PieceType setPieceType(String promotionType) {
        return switch (promotionType.toUpperCase()) {
            case "Q" -> ChessPiece.PieceType.QUEEN;
            case "B" -> ChessPiece.PieceType.BISHOP;
            case "N" -> ChessPiece.PieceType.KNIGHT;
            case "R" -> ChessPiece.PieceType.ROOK;
            default -> throw new IllegalArgumentException("Not a valid promotion type");
        };
    }

    public String highlight(PrintStream out) {
        out.println("Highlighting moves...");
        out.println("Enter coordinates of piece you want to check: ");
        String coordinates = scanner.nextLine();
        if(!validCoordinates(coordinates)) {
            out.println("Coordinates don't exist");
            highlight(out);
        } else {
            ChessPosition position = parseChessPosition(coordinates);
            ChessBoardDraw.drawChessBoard(chessBoard, position);
        }
        return "Available moves";
    }

    public String leave(PrintStream out) throws ResponseException {
        assertSignedIn();

        if (currentGame == null) {
            return "You're not in a game";
        }

        try {
        ws.leaveSend(authData.getAuthToken(), gameID);
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        sqlGameDAO.removePlayer(gameID, playerColor);
        currentGame = null;
        inGameplay = false;


        return "You have left the game.";
        } catch (ResponseException | DataAccessException | SQLException e) {
            return "Failed to leave game" + e.getMessage();
        }

    }

    public String resign(PrintStream out) throws ResponseException {
        assertInGameplay();
        try {
            ws.resignSend(authData.getAuthToken(), gameID);

            currentGame = null;
            inGameplay = false;

            return "Resigned";
        } catch (ResponseException e) {
            return "Could not resign" + e.getMessage();
        }
    }



    //Default login Screen depending on if they are logged in or out
    public String loginScreen() {
        return switch (state) {
            case SIGNEDOUT -> """
                    - register
                    - login
                    - quit
                    - help - show available commands
                    """;
            case SIGNEDIN -> {
                if (inGameplay) {
                    yield gameplayScreen();
                } else {
                    yield """
                            - logout
                            - create game (create)
                            - list games (list)
                            - play game (play)
                            - observe game (observe)
                            = quit
                            - help - Show available commands
                            """;
                }
            }
        };
    }


    public String gameplayScreen() {
        return """
                    - redraw chessboard (redraw)
                    - leave
                    - make move (make)
                    - resign
                    - highlight legal moves (highlight)
                    = quit
                    - help - Show available commands
                    """;
    }
    public String gameplayScreenHelp() {
        return """
                    - redraw chessboard - see the current state of the board
                    - leave - leave the current state of the game
                    - make move - enter in two coordinates to make a valid move
                    - resign - give up the game
                    - highlight legal moves - see what moves you can make
                    = quit
                    - help - Show available commands
                    """;
    }

    //Help menu
    public String help() {
        return switch (state) {
            case SIGNEDOUT -> """
                - register - Register an account
                - login - Sign in
                - quit - Exit chess program
                - help - Show available commands
                """;
            case SIGNEDIN -> {
                if (inGameplay) {
                    yield gameplayScreenHelp();
                } else {
                    yield """
                        - logout - Sign out
                        - create game - Start a game
                        - list games - List all available games
                        - play game - Join a game as white or black
                        - observe game - Watch an ongoing game
                        - quit - Exit chess program
                        - help - Show available commands
                        """;
                }
            }
        };
    }

    public boolean validCoordinates(String coordinate) {
        if (coordinate.length() != 2) {
            return false;
        }
        String columns = "abcdefgh";
        char letter = coordinate.charAt(1);
        char num = coordinate.charAt(0);

        boolean isValidColumn = columns.contains(String.valueOf(letter));
        boolean isValidRow = Character.isDigit(num) && (num >= '1' && num <= '8');

        return isValidColumn && isValidRow;
    }

    public ChessPosition parseChessPosition(String coordinates) {
        char letter = coordinates.charAt(1);
        int number = coordinates.charAt(0) - '0';

        int col = letter - 'a' + 1;
        int row = number;

        return new ChessPosition(row, col);
    }


    private ChessGame.TeamColor stringtoTeamColor(String playerColor) {
        if (playerColor.equalsIgnoreCase("white")) {
            return ChessGame.TeamColor.WHITE;
        } else if (playerColor.equalsIgnoreCase("black")) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }


    //private function to make sure user is signed in
    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    private void assertInGameplay() throws ResponseException {
        if (inGameplay != true) {
            throw new ResponseException(400, "You must be in a game");
        }
    }

    private void assertGameOver() {
        if (currentGame.isGameOver()) {
            throw new IllegalStateException("The game is over. No further moves can be made. Leave game to play again");
        }
    }
}
