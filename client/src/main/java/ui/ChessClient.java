package ui;

import org.eclipse.jetty.util.Scanner;

import java.io.PrintStream;
import java.util.Arrays;

public class ChessClient {

    private final PrintStream out;

    public ChessClient(String serverUrl) {
        this.out = new PrintStream(System.out, true);
    }

    public void beforeLogin(PrintStream out) {
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens [0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                default -> help();
            }
        }
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - signIn <USERNAME> <PASSWORD>
                    -
                    """;
        }
        return """
                
                """;
    }
}
