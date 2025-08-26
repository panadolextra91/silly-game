package core;

import entities.enemies.*;
import entities.towers.*;
import entities.projectiles.*;
import utils.Vector2D;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages the current state of the game
 * Implements Singleton pattern for global access
 */
public class GameState implements GameEventListener {
    private static GameState instance;
    
    // Game entities
    private List<Enemy> enemies;
    private List<Tower> towers;
    private List<Projectile> projectiles;
    private List<Vector2D> enemyPath;
    
    // Game statistics
    private int playerHealth;
    private int playerMoney;
    private int currentWave;
    private int enemiesKilled;
    private int score;
    
    // Wave management
    private WaveManager waveManager;
    private boolean waveInProgress;
    private double timeSinceWaveEnd;
    private final double WAVE_DELAY = 5.0; // seconds between waves
    
    // Game settings
    private final int STARTING_HEALTH = 20;
    private final int STARTING_MONEY = 200;
    
    // Event system
    private GameEventManager eventManager;
    
    private GameState() {
        initializeGame();
    }
    
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }
    
    /**
     * Initialize the game state
     */
    private void initializeGame() {
        // Initialize collections
        enemies = new CopyOnWriteArrayList<>();
        towers = new CopyOnWriteArrayList<>();
        projectiles = new CopyOnWriteArrayList<>();
        
        // Initialize game stats
        playerHealth = STARTING_HEALTH;
        playerMoney = STARTING_MONEY;
        currentWave = 0;
        enemiesKilled = 0;
        score = 0;
        waveInProgress = false;
        timeSinceWaveEnd = 0.0;
        
        // Initialize systems
        eventManager = new GameEventManager();
        waveManager = new WaveManager();
        
        // Register for events
        eventManager.addListener(EventType.ENEMY_KILLED, this);
        eventManager.addListener(EventType.ENEMY_REACHED_END, this);
        
        // Create default path
        createDefaultPath();
    }
    
    /**
     * Create a default enemy path
     */
    private void createDefaultPath() {
        enemyPath = new ArrayList<>();
        // Simple path from left to right with some curves
        enemyPath.add(new Vector2D(0, 300));
        enemyPath.add(new Vector2D(150, 300));
        enemyPath.add(new Vector2D(150, 150));
        enemyPath.add(new Vector2D(400, 150));
        enemyPath.add(new Vector2D(400, 450));
        enemyPath.add(new Vector2D(650, 450));
        enemyPath.add(new Vector2D(650, 200));
        enemyPath.add(new Vector2D(800, 200));
    }
    
    /**
     * Update the game state
     */
    public void update(double deltaTime) {
        // Update all entities
        updateEnemies(deltaTime);
        updateTowers(deltaTime);
        updateProjectiles(deltaTime);
        
        // Update wave management
        updateWaveManagement(deltaTime);
        
        // Process queued events
        eventManager.processQueuedEvents();
        
        // Clean up destroyed entities
        cleanupEntities();
    }
    
    /**
     * Update all enemies
     */
    private void updateEnemies(double deltaTime) {
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                enemy.update(deltaTime);
                
                // Check if enemy reached the end
                if (enemy.getPathProgress() >= 1.0) {
                    enemyReachedEnd(enemy);
                }
                
                // Check if enemy died
                if (enemy.getCurrentHp() <= 0) {
                    enemyKilled(enemy);
                }
            }
        }
    }
    
    /**
     * Update all towers
     */
    private void updateTowers(double deltaTime) {
        for (Tower tower : towers) {
            if (tower.isActive()) {
                // Provide current enemies to tower
                provideTowerWithEnemies(tower);
                tower.update(deltaTime);
            }
        }
    }
    
    /**
     * Update all projectiles
     */
    private void updateProjectiles(double deltaTime) {
        for (Projectile projectile : projectiles) {
            if (projectile.isActive()) {
                projectile.update(deltaTime);
            }
        }
    }
    
    /**
     * Provide tower with current enemy list for targeting
     */
    private void provideTowerWithEnemies(Tower tower) {
        // This is a workaround for the tower's getCurrentEnemies method
        // In a more sophisticated design, we'd use dependency injection
    }
    
    /**
     * Update wave management
     */
    private void updateWaveManagement(double deltaTime) {
        if (!waveInProgress) {
            timeSinceWaveEnd += deltaTime;
            if (timeSinceWaveEnd >= WAVE_DELAY) {
                startNextWave();
            }
        } else {
            // Check if wave is complete
            if (enemies.isEmpty() && !waveManager.hasMoreEnemies()) {
                waveCompleted();
            }
        }
        
        // Spawn enemies from wave manager
        if (waveInProgress) {
            Enemy newEnemy = waveManager.getNextEnemy(deltaTime);
            if (newEnemy != null) {
                spawnEnemy(newEnemy);
            }
        }
    }
    
    /**
     * Start the next wave
     */
    private void startNextWave() {
        currentWave++;
        waveInProgress = true;
        timeSinceWaveEnd = 0.0;
        
        waveManager.startWave(currentWave);
        
        // Fire wave started event
        GameEvent event = new WaveStartedEvent(currentWave);
        eventManager.fireEvent(event);
    }
    
    /**
     * Called when wave is completed
     */
    private void waveCompleted() {
        waveInProgress = false;
        
        // Award bonus money for completing wave
        int bonus = currentWave * 10;
        addMoney(bonus);
        
        // Fire wave completed event
        GameEvent event = new WaveCompletedEvent(currentWave);
        eventManager.fireEvent(event);
    }
    
    /**
     * Spawn an enemy
     */
    public void spawnEnemy(Enemy enemy) {
        enemy.setPath(new ArrayList<>(enemyPath));
        enemies.add(enemy);
        
        // Fire enemy spawned event
        GameEvent event = new EnemySpawnedEvent(enemy);
        eventManager.fireEvent(event);
    }
    
    /**
     * Place a tower
     */
    public boolean placeTower(Tower tower) {
        // Check if position is valid (not on path, not overlapping other towers)
        if (isValidTowerPosition(tower.getPosition())) {
            // Check if player has enough money
            if (playerMoney >= tower.getBaseCost()) {
                towers.add(tower);
                subtractMoney(tower.getBaseCost());
                
                // Fire tower placed event
                GameEvent event = new TowerPlacedEvent(tower);
                eventManager.fireEvent(event);
                
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if tower position is valid
     */
    private boolean isValidTowerPosition(Vector2D position) {
        // Check distance from path
        for (Vector2D pathPoint : enemyPath) {
            if (position.distanceTo(pathPoint) < 30) {
                return false;
            }
        }
        
        // Check distance from other towers
        for (Tower tower : towers) {
            if (position.distanceTo(tower.getPosition()) < 35) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Fire a projectile
     */
    public void fireProjectile(Projectile projectile) {
        projectiles.add(projectile);
        
        // Fire projectile fired event
        GameEvent event = new ProjectileFiredEvent(projectile);
        eventManager.fireEvent(event);
    }
    
    /**
     * Handle enemy killed
     */
    private void enemyKilled(Enemy enemy) {
        addMoney(enemy.getReward());
        enemiesKilled++;
        score += enemy.getReward() * 10;
        
        // Fire enemy killed event
        GameEvent event = new EnemyKilledEvent(enemy);
        eventManager.fireEvent(event);
        
        enemy.destroy();
    }
    
    /**
     * Handle enemy reaching end
     */
    private void enemyReachedEnd(Enemy enemy) {
        playerHealth -= enemy.getDamage();
        
        // Fire enemy reached end event
        GameEvent event = new EnemyReachedEndEvent(enemy);
        eventManager.fireEvent(event);
        
        enemy.destroy();
        
        // Check game over
        if (playerHealth <= 0) {
            gameOver();
        }
    }
    
    /**
     * Handle game over
     */
    private void gameOver() {
        GameEvent event = new GameOverEvent(score);
        eventManager.fireEvent(event);
    }
    
    /**
     * Clean up destroyed entities
     */
    private void cleanupEntities() {
        enemies.removeIf(enemy -> !enemy.isActive());
        towers.removeIf(tower -> !tower.isActive());
        projectiles.removeIf(projectile -> !projectile.isActive());
    }
    
    /**
     * Add money to player
     */
    public void addMoney(int amount) {
        playerMoney += amount;
        fireMoneyChangedEvent();
    }
    
    /**
     * Subtract money from player
     */
    public void subtractMoney(int amount) {
        playerMoney = Math.max(0, playerMoney - amount);
        fireMoneyChangedEvent();
    }
    
    /**
     * Fire money changed event
     */
    private void fireMoneyChangedEvent() {
        GameEvent event = new PlayerMoneyChangedEvent(playerMoney);
        eventManager.fireEvent(event);
    }
    
    @Override
    public void onEvent(GameEvent event) {
        // Handle specific events this class cares about
        switch (event.getType()) {
            case ENEMY_KILLED:
                // Already handled in enemyKilled method
                break;
            case ENEMY_REACHED_END:
                // Already handled in enemyReachedEnd method
                break;
        }
    }
    
    // Getters
    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies);
    }
    
    public List<Tower> getTowers() {
        return new ArrayList<>(towers);
    }
    
    public List<Projectile> getProjectiles() {
        return new ArrayList<>(projectiles);
    }
    
    public List<Vector2D> getEnemyPath() {
        return new ArrayList<>(enemyPath);
    }
    
    public int getPlayerHealth() {
        return playerHealth;
    }
    
    public int getPlayerMoney() {
        return playerMoney;
    }
    
    public int getCurrentWave() {
        return currentWave;
    }
    
    public int getEnemiesKilled() {
        return enemiesKilled;
    }
    
    public int getScore() {
        return score;
    }
    
    public boolean isWaveInProgress() {
        return waveInProgress;
    }
    
    public double getTimeUntilNextWave() {
        return waveInProgress ? 0.0 : WAVE_DELAY - timeSinceWaveEnd;
    }
    
    public GameEventManager getEventManager() {
        return eventManager;
    }
}

// Event classes
class EnemySpawnedEvent extends GameEvent {
    public EnemySpawnedEvent(Enemy enemy) {
        super(EventType.ENEMY_SPAWNED);
        setData("enemy", enemy);
    }
}

class EnemyKilledEvent extends GameEvent {
    public EnemyKilledEvent(Enemy enemy) {
        super(EventType.ENEMY_KILLED);
        setData("enemy", enemy);
    }
}

class EnemyReachedEndEvent extends GameEvent {
    public EnemyReachedEndEvent(Enemy enemy) {
        super(EventType.ENEMY_REACHED_END);
        setData("enemy", enemy);
    }
}

class TowerPlacedEvent extends GameEvent {
    public TowerPlacedEvent(Tower tower) {
        super(EventType.TOWER_PLACED);
        setData("tower", tower);
    }
}

class ProjectileFiredEvent extends GameEvent {
    public ProjectileFiredEvent(Projectile projectile) {
        super(EventType.PROJECTILE_FIRED);
        setData("projectile", projectile);
    }
}

class WaveStartedEvent extends GameEvent {
    public WaveStartedEvent(int waveNumber) {
        super(EventType.WAVE_STARTED);
        setData("waveNumber", waveNumber);
    }
}

class WaveCompletedEvent extends GameEvent {
    public WaveCompletedEvent(int waveNumber) {
        super(EventType.WAVE_COMPLETED);
        setData("waveNumber", waveNumber);
    }
}

class PlayerMoneyChangedEvent extends GameEvent {
    public PlayerMoneyChangedEvent(int newAmount) {
        super(EventType.PLAYER_MONEY_CHANGED);
        setData("amount", newAmount);
    }
}

class GameOverEvent extends GameEvent {
    public GameOverEvent(int finalScore) {
        super(EventType.GAME_OVER);
        setData("score", finalScore);
    }
}
