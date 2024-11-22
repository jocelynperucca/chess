package ui;

import WebSocket.NotificationHandler;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    //run UI program for client
    public void run() {
        System.out.print("\033[2J"); // ANSI escape code to clear the screen
        System.out.println("Welcome to Chess!");
        Scanner scanner = new Scanner(System.in);

        //loop through login until "quit" command is called by user
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

    @Override
    public void notify(ServerMessage message) {
        System.out.println(EscapeSequences.SET_BG_COLOR_RED + message);

    }
}
