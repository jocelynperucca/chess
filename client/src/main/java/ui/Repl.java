package ui;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.print(EscapeSequences.ERASE_SCREEN);

        System.out.println("Welcome to Chess!");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);

        }
    }


}
