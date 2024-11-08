package ui;

public class ChessBoardDraw {

    public static void drawChessBoard() {
        // ANSI reset sequence for clearing styles between squares
        final String RESET = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;

        // Print column labels
        System.out.print("   ");
        for (char col = 'a'; col <= 'h'; col++) {
            System.out.print(col + " ");
        }
        System.out.println(); // Newline after column labels

        // Loop to print each row of the chessboard
        for (int row = 0; row < 8; row++) {
            // Print row label
            System.out.print((row + 1) + "  "); // Row numbers (1-8)

            for (int col = 0; col < 8; col++) {
                // Alternate colors: even cells in a row are one color, odd cells another
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY + RESET + " ");
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY + RESET + " ");
                }
            }
            System.out.println(); // Newline after each row
        }
    }

}
