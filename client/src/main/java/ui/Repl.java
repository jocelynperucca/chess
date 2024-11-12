package ui;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.print("\033[2J"); // ANSI escape code to clear the screen

        System.out.println("Welcome to Chess!");


        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("What do you want to do?");
            System.out.println(client.loginScreen());
            System.out.print("\n> ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("quit")) {
                System.out.println("Exiting chess program. Goodbye!");
                break;
            }

            String result = client.eval(input);
            System.out.println(result);
        }
    }
}
