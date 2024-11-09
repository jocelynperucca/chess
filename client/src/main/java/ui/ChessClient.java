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

                default -> help();
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String register(PrintStream out) {
        out.println("register");
        out.print("Choose Username: ");
        String userName = scanner.nextLine();
        out.print("New password: ");
        String password = scanner.nextLine();
        out.print("Enter email: ");
        String email = scanner.nextLine();

        if(Objects.equals(userName, "") || Objects.equals(password, "") || Objects.equals(email, "")) {
            out.println("You are missing some of the key information");
        }

        UserData userData = new UserData(userName, password, email);

        try {
            AuthData loginResponse = server.register(userData);
            String authToken = loginResponse.getAuthToken();
            AuthData authData = new AuthData(userName, authToken);
            do {

            }
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }





    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - register an account
                    - login <USERNAME> <PASSWORD> - sign in
                    - quit - exit chess program
                    - help - show what commands you can do
                    """;
        }
        return """
                - logout - sign out of account
                - create game - <NAME> - start a game
                - list games - see all possible games
                - play game - <GAMEID> <WHITE|BLACK> - join a game with a specified color
                - observe game - <GAMEID> - watch an ongoing game
                - help - see what commands you can do
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
