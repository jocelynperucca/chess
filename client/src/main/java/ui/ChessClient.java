package ui;

import WebSocket.NotificationHandler;
import WebSocket.WebSocketFacade;
import chess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
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
    boolean inGameplay = false;
    private String playerColor;
    private ChessGame currentGame;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.out = new PrintStream(System.out, true);
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
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
        int gameID = selectedGame.getGameID();

        //set player color
        out.print("Enter 'white' or 'black' to choose your player color: ");
        playerColor = scanner.nextLine();

        //try to join game, if not, return exception
        try {
            server.joinGame(playerColor, gameID, authData);
            chessBoard = currentGame.getBoard();
            String message = "Successfully joined game " + selectedGame.getGameName() + " as " + playerColor;
            chessBoard.resetBoard();
            ChessBoardDraw.drawChessBoard(chessBoard);
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            server.setWebsocket(ws);
            inGameplay = true;
            ws.joinPlayerSend(gameID, ChessGame.TeamColor.WHITE, authData.getAuthToken());
            //HERE
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

        //Draw chessboard depending on the board at the time
        ChessBoardDraw.drawChessBoard(chessBoard);

        //Successfully entered game to observe
        return "Observing game: " + selectedGame.getGameName();
    }

    public String redrawChessboard(PrintStream out) {
        out.println("Printing chessboard...");
        ChessBoardDraw.drawChessBoard(chessBoard);
        return "Current Chessboard";

    }

    public String makeMove(PrintStream out) throws ResponseException, InvalidMoveException {
        assertInGameplay();
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
        //get game chessboard from gameData
        //update board after successful move. make method for that

        ChessGame game = new ChessGame();
        ChessMove tryMove = new ChessMove(start, end, null);
        Collection< ChessMove > validMoves = game.validMoves(start);
        if (validMoves.contains(tryMove)) {
            currentGame.makeMove(tryMove);
            chessBoard = currentGame.getBoard();
            ChessBoardDraw.drawChessBoard(chessBoard);


        } else {
            return "Invalid move, try again";

        }


        return "Made move";

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
                    - redraw chessboard
                    - leave
                    - make move
                    - resign
                    - highlight legal moves
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
}
