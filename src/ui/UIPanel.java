package ui;

import core.*;
import javax.swing.*;
import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;

/**
 * UI control panel for game information and controls
 */
public class UIPanel extends JPanel implements GameEventListener {
    private GameState gameState;
    private GameWindow gameWindow;
    
    // UI Components
    private JLabel healthLabel;
    private JLabel moneyLabel;
    private JLabel waveLabel;
    private JLabel scoreLabel;
    private JLabel killsLabel;
    private JLabel nextWaveLabel;
    private JProgressBar waveProgressBar;
    
    // Tower buttons
    private JButton archerButton;
    private JButton cannonButton;
    private JButton lightningButton;
    private JButton iceButton;
    private JButton poisonButton;
    
    // Control buttons
    private JButton pauseButton;
    private JButton speedButton;
    private JButton restartButton;
    
    // Game speed control
    private boolean paused = false;
    private int gameSpeed = 1; // 1x, 2x, 3x speed
    
    public UIPanel(int width, int height) {
        this.gameState = GameState.getInstance();
        
        setPreferredSize(new Dimension(width, height));
        setBackground(new Color(64, 64, 64)); // Dark gray
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initializeComponents();
        setupEventListeners();
        updateDisplay();
    }
    
    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }
    
    /**
     * Initialize all UI components
     */
    private void initializeComponents() {
        // Game statistics panel
        add(createStatsPanel());
        add(Box.createVerticalStrut(10));
        
        // Wave information panel
        add(createWavePanel());
        add(Box.createVerticalStrut(10));
        
        // Tower selection panel
        add(createTowerPanel());
        add(Box.createVerticalStrut(10));
        
        // Control panel
        add(createControlPanel());
        add(Box.createVerticalStrut(10));
        
        // Instructions panel
        add(createInstructionsPanel());
        
        // Add flexible space at bottom
        add(Box.createVerticalGlue());
    }
    
    /**
     * Create game statistics panel
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(48, 48, 48));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), 
            "Game Stats", 
            0, 0, null, Color.WHITE
        ));
        panel.setLayout(new GridLayout(5, 1, 5, 5));
        
        healthLabel = createInfoLabel("House Health: 100");
        moneyLabel = createInfoLabel("Money: $200");
        scoreLabel = createInfoLabel("Score: 0");
        killsLabel = createInfoLabel("Kills: 0");
        
        panel.add(healthLabel);
        panel.add(moneyLabel);
        panel.add(scoreLabel);
        panel.add(killsLabel);
        
        return panel;
    }
    
    /**
     * Create wave information panel
     */
    private JPanel createWavePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(48, 48, 48));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), 
            "Wave Info", 
            0, 0, null, Color.WHITE
        ));
        panel.setLayout(new GridLayout(3, 1, 5, 5));
        
        waveLabel = createInfoLabel("Wave: 1");
        nextWaveLabel = createInfoLabel("Next wave in: 5s");
        
        waveProgressBar = new JProgressBar(0, 100);
        waveProgressBar.setStringPainted(true);
        waveProgressBar.setString("Ready");
        waveProgressBar.setForeground(Color.GREEN);
        waveProgressBar.setBackground(Color.DARK_GRAY);
        
        panel.add(waveLabel);
        panel.add(nextWaveLabel);
        panel.add(waveProgressBar);
        
        return panel;
    }
    
    /**
     * Create tower selection panel
     */
    private JPanel createTowerPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(48, 48, 48));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), 
            "Towers (Hotkeys 1-5)", 
            0, 0, null, Color.WHITE
        ));
        panel.setLayout(new GridLayout(5, 1, 5, 5));
        
        archerButton = createTowerButton("1. Archer ($50)", new Color(139, 69, 19));
        cannonButton = createTowerButton("2. Cannon ($120)", Color.DARK_GRAY);
        lightningButton = createTowerButton("3. Lightning ($85)", Color.YELLOW);
        iceButton = createTowerButton("4. Ice ($70)", new Color(173, 216, 230));
        poisonButton = createTowerButton("5. Poison ($90)", new Color(128, 255, 0));
        
        panel.add(archerButton);
        panel.add(cannonButton);
        panel.add(lightningButton);
        panel.add(iceButton);
        panel.add(poisonButton);
        
        return panel;
    }
    
    /**
     * Create control panel
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(48, 48, 48));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), 
            "Controls", 
            0, 0, null, Color.WHITE
        ));
        panel.setLayout(new GridLayout(3, 1, 5, 5));
        
        pauseButton = createControlButton("Pause");
        speedButton = createControlButton("Speed: 1x");
        restartButton = createControlButton("Restart");
        
        panel.add(pauseButton);
        panel.add(speedButton);
        panel.add(restartButton);
        
        return panel;
    }
    
    /**
     * Create instructions panel
     */
    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(48, 48, 48));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), 
            "Instructions", 
            0, 0, null, Color.WHITE
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        String[] instructions = {
            "• Click towers to select",
            "• Press U to upgrade",
            "• Press S to sell",
            "• ESC to cancel",
            "",
            "Tower Types:",
            "• Archer: Balanced",
            "• Cannon: Splash damage", 
            "• Lightning: Chain attack",
            "• Ice: Slows enemies",
            "• Poison: DOT damage"
        };
        
        for (String instruction : instructions) {
            JLabel label = new JLabel(instruction);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.PLAIN, 10));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label);
        }
        
        return panel;
    }
    
    /**
     * Create styled info label
     */
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }
    
    /**
     * Create styled tower button
     */
    private JButton createTowerButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 10));
        button.setFocusable(false);
        
        // Make text more visible on light backgrounds
        if (color.equals(Color.YELLOW) || color.equals(new Color(173, 216, 230)) || 
            color.equals(new Color(128, 255, 0))) {
            button.setForeground(Color.BLACK);
        } else {
            button.setForeground(Color.WHITE);
        }
        
        return button;
    }
    
    /**
     * Create styled control button
     */
    private JButton createControlButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusable(false);
        return button;
    }
    
    /**
     * Setup event listeners
     */
    private void setupEventListeners() {
        // Register for game events
        gameState.getEventManager().addListener(EventType.PLAYER_MONEY_CHANGED, this);
        gameState.getEventManager().addListener(EventType.PLAYER_HEALTH_CHANGED, this);
        gameState.getEventManager().addListener(EventType.WAVE_STARTED, this);
        gameState.getEventManager().addListener(EventType.WAVE_COMPLETED, this);
        gameState.getEventManager().addListener(EventType.ENEMY_KILLED, this);
        
        // Control button listeners
        pauseButton.addActionListener(e -> { System.out.println("[INPUT][Click] Pause"); togglePause(); });
        speedButton.addActionListener(e -> { System.out.println("[INPUT][Click] Speed"); cycleSpeed(); });
        restartButton.addActionListener(e -> { System.out.println("[INPUT][Click] Restart"); restartGame(); });
        
        // Tower button listeners (these would trigger tower placement mode)
        archerButton.addActionListener(e -> { System.out.println("[INPUT][Click] Tower=archer"); selectTower("archer"); });
        cannonButton.addActionListener(e -> { System.out.println("[INPUT][Click] Tower=cannon"); selectTower("cannon"); });
        lightningButton.addActionListener(e -> { System.out.println("[INPUT][Click] Tower=lightning"); selectTower("lightning"); });
        iceButton.addActionListener(e -> { System.out.println("[INPUT][Click] Tower=ice"); selectTower("ice"); });
        poisonButton.addActionListener(e -> { System.out.println("[INPUT][Click] Tower=poison"); selectTower("poison"); });
    }
    
    /**
     * Update the display with current game state
     */
    public void update(double deltaTime) {
        updateDisplay();
        updateWaveProgress();
    }
    
    /**
     * Update all display elements
     */
    private void updateDisplay() {
        House house = gameState.getHouse();
        int houseHealth = house != null ? house.getCurrentHealth() : 0;
        healthLabel.setText("House Health: " + houseHealth);
        moneyLabel.setText("Money: $" + gameState.getPlayerMoney());
        waveLabel.setText("Wave: " + gameState.getCurrentWave());
        scoreLabel.setText("Score: " + gameState.getScore());
        killsLabel.setText("Kills: " + gameState.getEnemiesKilled());
        
        // Update next wave timer
        if (!gameState.isWaveInProgress()) {
            double timeUntilNext = gameState.getTimeUntilNextWave();
            nextWaveLabel.setText(String.format("Next wave in: %.1fs", timeUntilNext));
        } else {
            nextWaveLabel.setText("Wave in progress...");
        }
        
        // Update tower button availability based on money
        updateTowerButtonStates();
    }
    
    /**
     * Update wave progress bar
     */
    private void updateWaveProgress() {
        if (gameState.isWaveInProgress()) {
            // Show enemies remaining
            int totalEnemies = gameState.getEnemies().size();
            waveProgressBar.setString("Enemies: " + totalEnemies);
            waveProgressBar.setValue(Math.max(0, 100 - totalEnemies * 2));
            waveProgressBar.setForeground(Color.RED);
        } else {
            // Show countdown to next wave
            double timeUntilNext = gameState.getTimeUntilNextWave();
            int percentage = (int) ((5.0 - timeUntilNext) / 5.0 * 100);
            waveProgressBar.setValue(percentage);
            waveProgressBar.setString("Preparing...");
            waveProgressBar.setForeground(Color.GREEN);
        }
    }
    
    /**
     * Update tower button states based on available money
     */
    private void updateTowerButtonStates() {
        int money = gameState.getPlayerMoney();
        
        archerButton.setEnabled(money >= 50);
        cannonButton.setEnabled(money >= 120);
        lightningButton.setEnabled(money >= 85);
        iceButton.setEnabled(money >= 70);
        poisonButton.setEnabled(money >= 90);
    }
    
    /**
     * Toggle pause state
     */
    private void togglePause() {
        if (gameWindow != null) {
            gameWindow.togglePause();
            paused = gameWindow.isPaused();
            pauseButton.setText(paused ? "Resume" : "Pause");
        }
    }
    
    /**
     * Cycle through game speeds
     */
    private void cycleSpeed() {
        gameSpeed = (gameSpeed % 3) + 1;
        speedButton.setText("Speed: " + gameSpeed + "x");
        // Speed functionality would be implemented in the game loop
    }
    
    /**
     * Restart the game
     */
    private void restartGame() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to restart?",
            "Restart Game",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Restart functionality would be handled by the main game window
        }
    }
    
    /**
     * Select tower type for placement
     */
    private void selectTower(String towerType) {
        // This would communicate with the game panel to set tower placement mode
        // For now, just show a message
        JOptionPane.showMessageDialog(this, 
            "Selected " + towerType + " tower. Use hotkeys 1-5 or click on game area to place.",
            "Tower Selected", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Reset the panel
     */
    public void reset() {
        paused = false;
        gameSpeed = 1;
        pauseButton.setText("Pause");
        speedButton.setText("Speed: 1x");
        updateDisplay();
    }
    
    @Override
    public void onEvent(GameEvent event) {
        SwingUtilities.invokeLater(() -> {
            switch (event.getType()) {
                case PLAYER_MONEY_CHANGED:
                case PLAYER_HEALTH_CHANGED:
                case WAVE_STARTED:
                case WAVE_COMPLETED:
                case ENEMY_KILLED:
                case GAME_OVER:
                case ENEMY_SPAWNED:
                case ENEMY_REACHED_END:
                case PROJECTILE_FIRED:
                case TOWER_SOLD:
                case TOWER_UPGRADED:
                case PROJECTILE_HIT:
                case TOWER_PLACED:
                    updateDisplay();
                    break;
            }
        });
    }
}
