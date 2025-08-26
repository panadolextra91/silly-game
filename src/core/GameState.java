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
    private List<List<Vector2D>> enemyPaths;
    private House house;
    
    // Game statistics
    private int playerMoney;
    private int currentWave;
    private int enemiesKilled;
    private int score;
    
    // Game timer
    private double gameTime;
    private final double GAME_DURATION = 30.0; // 30 seconds
    private boolean gameWon;
    private boolean gameLost;
    
    // Wave management
    private WaveManager waveManager;
    private boolean waveInProgress;
    private double timeSinceWaveEnd;
    private final double WAVE_DELAY = 5.0; // seconds between waves
    
    // Game settings
    //private final int STARTING_HEALTH = 20;
    private final int STARTING_MONEY = 200;
    private int currentLevel = 1;
    
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
        playerMoney = STARTING_MONEY;
        currentWave = 0;
        enemiesKilled = 0;
        score = 0;
        waveInProgress = false;
        timeSinceWaveEnd = 0.0;
        
        // Initialize game timer
        gameTime = 0.0;
        gameWon = false;
        gameLost = false;
        
        // Initialize systems
        eventManager = new GameEventManager();
        waveManager = new WaveManager();
        
        // Register for events
        eventManager.addListener(EventType.ENEMY_KILLED, this);
        eventManager.addListener(EventType.ENEMY_REACHED_END, this);
        
        // Create default paths for current level
        createPathsForLevel(currentLevel);
        
        // Create house at the end of the path
        createHouse();
    }
    
    /**
     * Create a default enemy path
     */
    private void createPathsForLevel(int level) {
        enemyPaths = new ArrayList<>();
        switch (level) {
            case 1: {
                List<Vector2D> path = new ArrayList<>();
                path.add(new Vector2D(0, 300));
                path.add(new Vector2D(200, 300));
                path.add(new Vector2D(400, 300));
                path.add(new Vector2D(600, 300));
                path.add(new Vector2D(760, 300));
                enemyPaths.add(path);
                break;
            }
            case 2: {
                // Three paths converging to the same end
                List<Vector2D> top = new ArrayList<>();
                top.add(new Vector2D(0, 150));
                top.add(new Vector2D(250, 150));
                top.add(new Vector2D(450, 200));
                top.add(new Vector2D(620, 260));
                top.add(new Vector2D(760, 300));
                List<Vector2D> mid = new ArrayList<>();
                mid.add(new Vector2D(0, 350));
                mid.add(new Vector2D(150, 350));
                mid.add(new Vector2D(350, 320));
                mid.add(new Vector2D(550, 310));
                mid.add(new Vector2D(760, 300));
                List<Vector2D> bot = new ArrayList<>();
                bot.add(new Vector2D(0, 500));
                bot.add(new Vector2D(200, 480));
                bot.add(new Vector2D(420, 420));
                bot.add(new Vector2D(620, 360));
                bot.add(new Vector2D(760, 300));
                enemyPaths.add(top);
                enemyPaths.add(mid);
                enemyPaths.add(bot);
                break;
            }
            case 3:
            default: {
                // More complex and unpredictable: zigzags
                List<Vector2D> pathA = new ArrayList<>();
                pathA.add(new Vector2D(0, 100));
                pathA.add(new Vector2D(120, 180));
                pathA.add(new Vector2D(60, 260));
                pathA.add(new Vector2D(200, 340));
                pathA.add(new Vector2D(120, 420));
                pathA.add(new Vector2D(300, 450));
                pathA.add(new Vector2D(500, 380));
                pathA.add(new Vector2D(680, 330));
                pathA.add(new Vector2D(760, 300));
                List<Vector2D> pathB = new ArrayList<>();
                pathB.add(new Vector2D(0, 550));
                pathB.add(new Vector2D(180, 520));
                pathB.add(new Vector2D(300, 400));
                pathB.add(new Vector2D(380, 250));
                pathB.add(new Vector2D(520, 220));
                pathB.add(new Vector2D(650, 260));
                pathB.add(new Vector2D(760, 300));
                List<Vector2D> pathC = new ArrayList<>();
                pathC.add(new Vector2D(0, 280));
                pathC.add(new Vector2D(200, 200));
                pathC.add(new Vector2D(300, 300));
                pathC.add(new Vector2D(450, 200));
                pathC.add(new Vector2D(600, 350));
                pathC.add(new Vector2D(760, 300));
                enemyPaths.add(pathA);
                enemyPaths.add(pathB);
                enemyPaths.add(pathC);
                break;
            }
        }
    }
    
    /**
     * Create house at the end of the path
     */
    private void createHouse() {
        // Place house at end of the first path
        List<Vector2D> firstPath = enemyPaths.get(0);
        Vector2D endPoint = firstPath.get(firstPath.size() - 1);
        house = new House(endPoint.x, endPoint.y);
        System.out.println("House created at: " + endPoint.x + ", " + endPoint.y);
    }
    
    /**
     * Update the game state
     */
    public void update(double deltaTime) {
        if (gameWon || gameLost) return;
        
        // Update game timer
        gameTime += deltaTime;
        if (gameTime >= GAME_DURATION) {
            gameWon = true;
            return;
        }
        
        // Update all entities
        updateEnemies(deltaTime);
        updateTowers(deltaTime);
        updateProjectiles(deltaTime);
        updateHouse(deltaTime);
        
        // Update wave management
        updateWaveManagement(deltaTime);
        
        // Process queued events
        eventManager.processQueuedEvents();
        
        // Clean up destroyed entities
        cleanupEntities();
        
        // Check win/lose conditions
        checkGameEndConditions();
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
        // Assign a random path among available ones
        List<Vector2D> path = enemyPaths.get(new Random().nextInt(enemyPaths.size()));
        enemy.setPath(new ArrayList<>(path));
        enemies.add(enemy);
        System.out.println("Enemy spawned at: " + enemy.getPosition().x + ", " + enemy.getPosition().y);
        
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
                System.out.println("[PLACE][Tower] id=" + tower.getId() + " type=" + tower.getClass().getSimpleName() +
                    " pos=(" + tower.getPosition().x + "," + tower.getPosition().y + ") cost=" + tower.getBaseCost());
                
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
        // Check distance from all paths
        for (List<Vector2D> path : enemyPaths) {
            for (Vector2D pathPoint : path) {
                if (position.distanceTo(pathPoint) < 30) {
                    return false;
                }
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
        // Enemy attacks the house
        house.takeDamage(enemy.getDamage());
        
        // Fire enemy reached end event
        GameEvent event = new EnemyReachedEndEvent(enemy);
        eventManager.fireEvent(event);
        
        enemy.destroy();
    }
    
    /**
     * Update house
     */
    private void updateHouse(double deltaTime) {
        if (house != null && house.isActive()) {
            house.update(deltaTime);
        }
    }
    
    /**
     * Check win/lose conditions
     */
    private void checkGameEndConditions() {
        if (house != null && house.isDestroyed()) {
            gameLost = true;
            GameEvent event = new GameOverEvent(score);
            eventManager.fireEvent(event);
        }
    }
    
    /**
     * Handle game over
     */
    @SuppressWarnings("unused")
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
            case GAME_OVER:
            case TOWER_SOLD:
            case PROJECTILE_FIRED:
            case WAVE_STARTED:
            case PROJECTILE_HIT:
            case TOWER_UPGRADED:
            case PLAYER_MONEY_CHANGED:
            case TOWER_PLACED:
            case ENEMY_SPAWNED:
            case PLAYER_HEALTH_CHANGED:
            case WAVE_COMPLETED:
                // No-op in GameState for these events
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
        // Backward compatibility: return first path
        return new ArrayList<>(enemyPaths.get(0));
    }

    public List<List<Vector2D>> getEnemyPaths() {
        List<List<Vector2D>> copy = new ArrayList<>();
        for (List<Vector2D> p : enemyPaths) {
            copy.add(new ArrayList<>(p));
        }
        return copy;
    }

    public void setLevel(int level) {
        this.currentLevel = Math.max(1, Math.min(3, level));
        createPathsForLevel(this.currentLevel);
        createHouse();
    }
    
    public int getPlayerHealth() {
        return house != null ? house.getCurrentHealth() : 0;
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
    
    public double getGameTime() {
        return gameTime;
    }
    
    public double getGameDuration() {
        return GAME_DURATION;
    }
    
    public boolean isGameWon() {
        return gameWon;
    }
    
    public boolean isGameLost() {
        return gameLost;
    }
    
    public House getHouse() {
        return house;
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
