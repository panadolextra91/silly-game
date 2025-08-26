package ui;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main game window using Swing
 */
public class GameWindow extends JFrame implements GameEventListener {
    private GamePanel gamePanel;
    private UIPanel uiPanel;
    private GameState gameState;
    private Timer gameTimer;
    
    // Game timing
    private long lastUpdateTime;
    private final int TARGET_FPS = 60;
    private final long FRAME_TIME = 1000 / TARGET_FPS; // milliseconds
    
    // Game state
    private boolean isPaused = false;
    
    public GameWindow() {
        initializeWindow();
        initializeComponents();
        setupEventListeners();
        startGameLoop();
    }
    
    /**
     * Initialize the main window
     */
    private void initializeWindow() {
        setTitle("Advanced Tower Defense");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Calculate window size
        int windowWidth = 1000;
        int windowHeight = 700;
        setSize(windowWidth, windowHeight);
        
        // Center window on screen
        setLocationRelativeTo(null);
        
        // Set layout
        setLayout(new BorderLayout());
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        gameState = GameState.getInstance();
        
        // Create game panel (main playing area)
        gamePanel = new GamePanel(800, 600);
        add(gamePanel, BorderLayout.CENTER);
        
        // Create UI panel (controls and info)
        uiPanel = new UIPanel(200, 600);
        uiPanel.setGameWindow(this);
        add(uiPanel, BorderLayout.EAST);
        
        // Set focus to game panel for keyboard input
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
    }
    
    /**
     * Setup event listeners
     */
    private void setupEventListeners() {
        // Register for game events
        gameState.getEventManager().addListener(EventType.GAME_OVER, this);
        gameState.getEventManager().addListener(EventType.WAVE_STARTED, this);
        gameState.getEventManager().addListener(EventType.WAVE_COMPLETED, this);
        
        // Window close event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopGameLoop();
                System.exit(0);
            }
        });
    }
    
    /**
     * Start the game loop
     */
    private void startGameLoop() {
        lastUpdateTime = System.currentTimeMillis();
        
        // Use Swing Timer for consistent frame rate
        gameTimer = new Timer((int) FRAME_TIME, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                repaintGame();
            }
        });
        
        gameTimer.start();
    }
    
    /**
     * Update game logic
     */
    private void updateGame() {
        if (isPaused) return;
        
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0; // Convert to seconds
        lastUpdateTime = currentTime;
        
        // Cap delta time to prevent large jumps
        deltaTime = Math.min(deltaTime, 1.0 / 30.0); // Max 30 FPS equivalent
        
        // Debug tick log (low frequency)
        // System.out.println("[TICK] dt=" + String.format("%.3f", deltaTime));
        
        // Update game state
        gameState.update(deltaTime);
        
        // Update UI
        uiPanel.update(deltaTime);
        
        // Check win/lose conditions
        checkGameEndConditions();
    }
    
    /**
     * Repaint all components
     */
    private void repaintGame() {
        gamePanel.repaint();
        uiPanel.repaint();
    }
    
    /**
     * Stop the game loop
     */
    private void stopGameLoop() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
    
    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {
            case GAME_OVER:
                handleGameOver(event);
                break;
            case WAVE_STARTED:
                handleWaveStarted(event);
                break;
            case WAVE_COMPLETED:
                handleWaveCompleted(event);
                break;
            case ENEMY_SPAWNED:
                // no-op UI side
                break;
            case ENEMY_KILLED:
                // no-op UI side
                break;
            case ENEMY_REACHED_END:
                // no-op UI side (house handles damage/game state reacts)
                break;
            case TOWER_PLACED:
                // no-op UI side
                break;
            case TOWER_UPGRADED:
                // no-op UI side
                break;
            case TOWER_SOLD:
                // no-op UI side
                break;
            case PLAYER_HEALTH_CHANGED:
                // no-op UI side
                break;
            case PLAYER_MONEY_CHANGED:
                // no-op UI side; UIPanel pulls from GameState each frame
                break;
            case PROJECTILE_FIRED:
                // no-op UI side
                break;
            case PROJECTILE_HIT:
                // no-op UI side
                break;
        }
    }
    
    /**
     * Check for win/lose conditions
     */
    private void checkGameEndConditions() {
        if (gameState.isGameWon()) {
            handleGameWin();
        } else if (gameState.isGameLost()) {
            handleGameLose();
        }
    }
    
    /**
     * Handle game over event
     */
    private void handleGameOver(GameEvent event) {
        stopGameLoop();
        
        int score = event.getData("score");
        int result = JOptionPane.showConfirmDialog(
            this,
            "Game Over!\nFinal Score: " + score + "\n\nPlay again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }
    
    /**
     * Handle game win
     */
    private void handleGameWin() {
        stopGameLoop();
        
        int score = gameState.getScore();
        int result = JOptionPane.showConfirmDialog(
            this,
            "Congratulations! You Won!\nFinal Score: " + score + "\n\nPlay again?",
            "Victory!",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }
    
    /**
     * Handle game lose
     */
    private void handleGameLose() {
        stopGameLoop();
        
        int score = gameState.getScore();
        int result = JOptionPane.showConfirmDialog(
            this,
            "Your house was destroyed!\nFinal Score: " + score + "\n\nPlay again?",
            "Defeat",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }
    
    /**
     * Handle wave started event
     */
    private void handleWaveStarted(GameEvent event) {
        int waveNumber = event.getData("waveNumber");
        gamePanel.showWaveStartMessage("Wave " + waveNumber + " Started!");
    }
    
    /**
     * Handle wave completed event
     */
    private void handleWaveCompleted(GameEvent event) {
        int waveNumber = event.getData("waveNumber");
        gamePanel.showWaveCompleteMessage("Wave " + waveNumber + " Complete!");
    }
    
    /**
     * Restart the game
     */
    private void restartGame() {
        // Stop current game
        stopGameLoop();
        
        // Reset game state
        gameState = GameState.getInstance();
        
        // Reinitialize components
        gamePanel.reset();
        uiPanel.reset();
        
        // Restart game loop
        startGameLoop();
    }
    
    /**
     * Get the game panel for external access
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }
    
    /**
     * Get the UI panel for external access
     */
    public UIPanel getUIPanel() {
        return uiPanel;
    }
    
    /**
     * Toggle pause state
     */
    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            System.out.println("Game paused");
        } else {
            System.out.println("Game resumed");
        }
    }
    
    /**
     * Check if game is paused
     */
    public boolean isPaused() {
        return isPaused;
    }
}
