package ui;

//import org.eclipse.jetty.util.Scanner;
import chess.ChessBoard;
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
    ChessBoardDraw chessBoardDraw = new ChessBoardDraw();
    private final ChessBoard chessBoard = new ChessBoard();

    public ChessClient(String serverUrl) {
        this.out = new PrintStream(System.out, true);
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens [0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(out);
                case "login" -> login(out);
                case "logout" -> logout(out);
                case "create" -> createGame(out);
                case "list" -> listGames(out);
                case "join" -> joinGame(out);
                case "help" -> help();

                default -> loginScreen();
            };
        } catch (Exception e) {
            return ("Invalid command");
        }
    }

    public String register(PrintStream out) {
        out.println("Registering a new account:");
        out.print("Choose Username: ");
        String userName = scanner.nextLine();
        out.print("New password: ");
        String password = scanner.nextLine();
        out.print("Enter email: ");
        String email = scanner.nextLine();

        if (userName.isEmpty() || password.isEmpty() || email.isEmpty()) {
            out.println("You are missing some of the key information");
            return "Registration failed: missing information";
        }

        UserData userData = new UserData(userName, password, email);

        try {
            AuthData registerResponse = server.register(userData);
            String authToken = registerResponse.getAuthToken();
            return "Registration successful!";
        } catch (ResponseException e) {
            return "Registration failed: ";
        }
    }

    public String login(PrintStream out) throws ResponseException {
        out.println("Login:");
        out.print("Enter username: ");
        String userName = scanner.nextLine();
        out.print("Enter password: ");
        String password = scanner.nextLine();

        if (userName.isEmpty() || password.isEmpty()) {
            out.println("Not a valid username or password entry.");
            return "Login failed: missing info";
        }

        UserData userData = new UserData(userName, password, null);
        try {
            AuthData loginResponse = server.login(userData);
            String authToken = loginResponse.getAuthToken();
            authData = new AuthData(userName, authToken);
            state = State.SIGNEDIN;
            return "Login successful!";
        } catch (ResponseException e) {
            return "Login failed: ";
        }
    }

    public String logout(PrintStream out) throws ResponseException {
        assertSignedIn();
        out.println("Logging out");
        try {
            server.logout(authData);
            state = State.SIGNEDOUT;
            return "Logged Out";
        } catch (ResponseException e) {
            return "Couldn't logout";
        }
    }

    public String listGames(PrintStream out) throws ResponseException {
        assertSignedIn(); // Ensure the user is signed in
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

        String gameDisplay = gameListBuilder.toString();
        return gameDisplay; // Print to the console
    }

    public String joinGame(PrintStream out) throws ResponseException {
        assertSignedIn();
        out.println("Joining Game...");

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

        String gameDisplay = gameListBuilder.toString();
        out.println(gameDisplay); // Print to the console

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

        GameData selectedGame = gameList.get(selectedNumber - 1); // Get the selected game
        int gameID = selectedGame.getGameID(); // Retrieve the actual game ID

        out.print("Enter 'white' or 'black' to choose your player color: ");
        String playerColor = scanner.nextLine();

        try {
            server.joinGame(playerColor, gameID, authData);
            String message = "Successfully joined game #" + selectedNumber + " as " + playerColor;
            chessBoard.resetBoard();
            ChessBoardDraw.drawChessBoard(chessBoard);
            return message;
        } catch (ResponseException e) {
            return "Failed to join game, check player color or status of game";
        }
    }

    public String createGame(PrintStream out) {
        out.println("Creating Game...");

        out.print("Enter a name for your game: ");
        String gameName = scanner.nextLine();

        if (gameName.isEmpty()) {
            return "Game name cannot be empty";
        }

        try {
            GameData newGame = server.createGame(gameName, authData);
            String message = "Game created: " + gameName;
            out.println("Join the game through the menu to start playing");
            return message;
        } catch (ResponseException e) {
            return "Failed to create game";
        }
    }

    public String loginScreen() {
        return switch (state) {
            case SIGNEDOUT -> """
                    - register
                    - login <USERNAME> <PASSWORD>
                    - quit
                    - help - show available commands
                    """;
            case SIGNEDIN -> """
                    - logout
                    - create game <NAME>
                    - list games (list)
                    - join game (join) <GAMEID> <WHITE|BLACK>
                    - observe game (observe) <GAMEID>
                    = quit
                    - help - Show available commands
                    """;
        };

    }

    public String help() {
        return switch (state) {
            case SIGNEDOUT -> """
                    - register - Register an account
                    - login <USERNAME> <PASSWORD> - Sign in
                    - quit - Exit chess program
                    - help - Show available commands
                    """;
            case SIGNEDIN -> """
                    - logout - Sign out
                    - create game <NAME> - Start a game
                    - list games - List all available games
                    - play game <GAMEID> <WHITE|BLACK> - Join a game as white or black
                    - observe game <GAMEID> - Watch an ongoing game
                    - quit - Exit chess program
                    - help - Show available commands
                    """;
        };
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
