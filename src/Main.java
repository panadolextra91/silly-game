/**
 * Advanced Tower Defense Game
 * Main entry point for the game
 */
public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new core.Game().start();
        });
    }
}
