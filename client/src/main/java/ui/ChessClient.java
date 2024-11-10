package ui;

//import org.eclipse.jetty.util.Scanner;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.Collection;
import java.util.Scanner;
import java.io.PrintStream;
import java.util.Arrays;

public class ChessClient {

    private final PrintStream out;
    private State state = State.SIGNEDOUT;
    Scanner scanner = new Scanner(System.in);
    private final ServerFacade server;
    private AuthData authData;

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
                case "list" -> listGames(out);
                case "join" -> joinGame(out);
                case "help" -> help();

                default -> loginScreen();
            };
        } catch (Exception e) {
            out.println("Invalid command");
            return e.getMessage();
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
            state = State.SIGNEDIN;  // Update state to signed in
            return "Registration successful!";
        } catch (ResponseException e) {
            return "Registration failed: " + e.getMessage();
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
            return "Login failed: " + e.getMessage();
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
            return "Couldn't logout: " + e.getMessage();
        }
    }

    public String listGames(PrintStream out) throws ResponseException {
        assertSignedIn(); // Ensure the user is signed in
        out.println("Fetching game list...");

        // Retrieve the list of games from the server
        Collection<GameData> games = server.listGames(authData);

        if (games == null || games.isEmpty()) {
            out.println("No games available.");
            return "No games available.";
        }

        StringBuilder gameListBuilder = new StringBuilder("Available Games:\n");
        for (GameData game : games) {
            gameListBuilder.append("- ").append(game.toString()).append("\n"); // Assuming `GameData` has a meaningful `toString` implementation
        }

        String gameList = gameListBuilder.toString();
        out.println(gameList); // Print to the console
        return gameList;       // Return the formatted list
    }

    public String joinGame(PrintStream out) throws ResponseException {
        assertSignedIn();
        out.println("Joining Game...");

        Collection<GameData> games = server.listGames(authData);

        if (games == null || games.isEmpty()) {
            return "No games available.";
        }

        StringBuilder gameListBuilder = new StringBuilder("Available Games:\n");
        for (GameData game : games) {
            gameListBuilder.append("- ").append(game.toString()).append("\n"); // Assuming `GameData` has a meaningful `toString` implementation
        }

        String gameList = gameListBuilder.toString();
        out.println(gameList); // Print to the console
        out.print("Game ID you want to join: ");
        int gameID;
        try {
            gameID = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return "Invalid game ID format";
        }

        out.print("Enter 'white' or 'black' to choose your player color");
        String playerColor = scanner.nextLine();

        try {
            server.joinGame(playerColor, gameID, authData);
            String message = "Successfully joined game ID " + gameID + " as " + playerColor;
            out.println(message);
            return message;
        } catch (ResponseException e) {
            out.println("Failed to join game");
            return e.getMessage();
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
            String message = "Game: " + newGame.getGameName() + " GameID: " + newGame.getGameID();
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
