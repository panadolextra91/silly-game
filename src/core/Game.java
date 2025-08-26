package core;

import ui.GameWindow;
import javax.swing.SwingUtilities;

/**
 * Main game class - entry point and coordinator
 * Implements Facade pattern to provide simple interface to complex subsystems
 */
public class Game {
    private GameWindow gameWindow;
    private GameState gameState;
    private boolean running;
    
    public Game() {
        initialize();
    }
    
    /**
     * Initialize the game
     */
    private void initialize() {
        // Initialize game state
        gameState = GameState.getInstance();
        running = false;
        
        System.out.println("Advanced Tower Defense Game");
        System.out.println("===========================");
        System.out.println("Initializing game systems...");
    }
    
    /**
     * Start the game
     */
    public void start() {
        if (running) {
            System.out.println("Game is already running!");
            return;
        }
        
        System.out.println("Starting game...");
        running = true;
        
        // Create and show game window on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                gameWindow = new GameWindow();
                gameWindow.setVisible(true);
                
                System.out.println("Game started successfully!");
                System.out.println();
                printControls();
                
            } catch (Exception e) {
                System.err.println("Error starting game: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Stop the game
     */
    public void stop() {
        if (!running) {
            return;
        }
        
        System.out.println("Stopping game...");
        running = false;
        
        if (gameWindow != null) {
            gameWindow.dispose();
        }
        
        System.out.println("Game stopped.");
    }
    
    /**
     * Print game controls to console
     */
    private void printControls() {
        System.out.println("GAME CONTROLS:");
        System.out.println("=============");
        System.out.println("Mouse:");
        System.out.println("  - Click to place towers or select existing towers");
        System.out.println("  - Move mouse to preview tower placement");
        System.out.println();
        System.out.println("Keyboard:");
        System.out.println("  - 1: Select Archer Tower ($50)");
        System.out.println("  - 2: Select Cannon Tower ($120)");
        System.out.println("  - 3: Select Lightning Tower ($85)");
        System.out.println("  - 4: Select Ice Tower ($70)");
        System.out.println("  - 5: Select Poison Tower ($90)");
        System.out.println("  - U: Upgrade selected tower");
        System.out.println("  - S: Sell selected tower");
        System.out.println("  - ESC: Cancel current action");
        System.out.println();
        System.out.println("Tower Types:");
        System.out.println("  - Archer: Balanced damage and range, good all-around");
        System.out.println("  - Cannon: High damage with splash effect, slow firing");
        System.out.println("  - Lightning: Chain lightning that jumps between enemies");
        System.out.println("  - Ice: Slows and freezes enemies");
        System.out.println("  - Poison: Damage over time effects");
        System.out.println();
        System.out.println("Enemy Types:");
        System.out.println("  - Red Circle: Basic enemy");
        System.out.println("  - Yellow Circle: Fast enemy");
        System.out.println("  - Gray Circle: Armored enemy (physical resistance)");
        System.out.println("  - Cyan Circle: Flying enemy");
        System.out.println("  - Orange Circle: Fire elemental (fire immune, ice weak)");
        System.out.println("  - Blue Circle: Ice elemental (ice immune, fire weak)");
        System.out.println("  - Green Circle: Regenerating enemy");
        System.out.println("  - Large Magenta Circle: Boss enemy (every 10 waves)");
        System.out.println();
        System.out.println("Good luck defending your base!");
    }
    
    /**
     * Check if game is running
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Get game window reference
     */
    public GameWindow getGameWindow() {
        return gameWindow;
    }
    
    /**
     * Get game state reference
     */
    public GameState getGameState() {
        return gameState;
    }
}
