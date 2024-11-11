package ui;

public class ChessBoardDraw {

    public static void drawChessBoard() {
        // ANSI reset sequence for clearing styles between squares
        final String RESET = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;

        // Print top column labels with extra space for alignment
        System.out.print("   "); // Adjusted to better align labels
        for (char col = 'a'; col <= 'h'; col++) {
            System.out.printf(" " + col + " ");; // Uniform width for each label
        }
        System.out.println(); // Newline after column labels

        // Loop to print each row of the chessboard
        for (int row = 0; row < 8; row++) {
            // Print row label on the left with alignment
            System.out.print(" " + (row + 1) + " "); // Row numbers (1-8)

            for (int col = 0; col < 8; col++) {
                // Alternate colors: even cells in a row are one color, odd cells another
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE + "   " + RESET);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + "   " + RESET);
                }
            }

            // Print row label on the right for alignment
            System.out.println(" " + (row + 1)); // Row numbers (1-8) on the right side
        }

        // Print bottom column labels with extra space for alignment
        System.out.print("   "); // Adjusted to better align labels
        for (char col = 'a'; col <= 'h'; col++) {
            System.out.printf(" " + col + " "); // Uniform width for each label
        }
        System.out.println(); // Newline after bottom column labels
    }
}
