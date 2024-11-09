package ui;

//import org.eclipse.jetty.util.Scanner;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.Scanner;
import java.io.PrintStream;
import java.util.Arrays;

public class ChessClient {

    private final PrintStream out;
    private State state = State.SIGNEDOUT;
    Scanner scanner = new Scanner(System.in);
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        this.out = new PrintStream(System.out, true);
        server = new ServerFacade(serverUrl);
    }

    public void beforeLogin(PrintStream out) {
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens [0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(out);
                case "login" -> login(out);

                default -> help();
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        assertSignedIn();
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
            state = State.SIGNEDIN;
            return "Login successful!";
        } catch (ResponseException e) {
            return "Login failed: " + e.getMessage();
        }
    }

    public String logout(PrintStream out) {
        out.println("Logging out");
        try {
            server.log
        }

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
