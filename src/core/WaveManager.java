package core;

import entities.enemies.*;
import patterns.factories.EnemyFactory;
//import core.EnemyType;
import java.util.*;

/**
 * Manages wave generation and enemy spawning
 * Uses Factory pattern for enemy creation
 */
public class WaveManager {
    private Queue<Enemy> currentWaveEnemies;
    private double timeSinceLastSpawn;
    private double spawnInterval;
    private boolean waveActive;
    private int currentWaveNumber;
    
    // Wave configuration
    private final double BASE_SPAWN_INTERVAL = 1.0; // seconds
    private final double MIN_SPAWN_INTERVAL = 0.2;
    
    public WaveManager() {
        this.currentWaveEnemies = new ArrayDeque<>();
        this.timeSinceLastSpawn = 0.0;
        this.spawnInterval = BASE_SPAWN_INTERVAL;
        this.waveActive = false;
        this.currentWaveNumber = 0;
    }
    
    /**
     * Start a new wave
     */
    public void startWave(int waveNumber) {
        this.currentWaveNumber = waveNumber;
        this.waveActive = true;
        this.timeSinceLastSpawn = 0.0;
        
        // Calculate spawn interval for this wave
        this.spawnInterval = Math.max(MIN_SPAWN_INTERVAL, 
                                     BASE_SPAWN_INTERVAL - (waveNumber * 0.05));
        
        // Generate enemies for this wave
        generateWaveEnemies(waveNumber);
    }
    
    /**
     * Generate enemies for the current wave
     */
    private void generateWaveEnemies(int waveNumber) {
        currentWaveEnemies.clear();
        
        if (waveNumber % 10 == 0) {
            // Boss wave every 10 waves
            generateBossWave(waveNumber);
        } else {
            // Regular wave
            generateRegularWave(waveNumber);
        }
    }
    
    /**
     * Generate a regular wave
     */
    private void generateRegularWave(int waveNumber) {
        int totalEnemies = Math.min(30, 8 + waveNumber);
        EnemyFactory factory = new EnemyFactory();
        
        // Calculate enemy type distribution
        int basicCount = (int) (totalEnemies * 0.4);
        int fastCount = (int) (totalEnemies * 0.25);
        int armoredCount = (int) (totalEnemies * 0.15);
        int flyingCount = (int) (totalEnemies * 0.1);
        int elementalCount = (int) (totalEnemies * 0.1);
        
        // Adjust counts based on wave number
        if (waveNumber >= 5) {
            fastCount += 2;
        }
        if (waveNumber >= 10) {
            armoredCount += 2;
            flyingCount += 2;
        }
        if (waveNumber >= 15) {
            elementalCount += 3;
        }
        
        // Create enemies in a mixed order for variety
        List<Enemy> waveEnemies = new ArrayList<>();
        
        // Add basic enemies
        for (int i = 0; i < basicCount; i++) {
            waveEnemies.add(factory.createEnemy(EnemyType.BASIC, waveNumber));
        }
        
        // Add fast enemies
        for (int i = 0; i < fastCount; i++) {
            waveEnemies.add(factory.createEnemy(EnemyType.FAST, waveNumber));
        }
        
        // Add armored enemies
        for (int i = 0; i < armoredCount; i++) {
            waveEnemies.add(factory.createEnemy(EnemyType.ARMORED, waveNumber));
        }
        
        // Add flying enemies
        for (int i = 0; i < flyingCount; i++) {
            waveEnemies.add(factory.createEnemy(EnemyType.FLYING, waveNumber));
        }
        
        // Add elemental enemies
        for (int i = 0; i < elementalCount; i++) {
            EnemyType elementalType = (Math.random() < 0.5) ? EnemyType.FIRE_ELEMENTAL : EnemyType.ICE_ELEMENTAL;
            waveEnemies.add(factory.createEnemy(elementalType, waveNumber));
        }
        
        // Add regenerating enemies for later waves
        if (waveNumber >= 8) {
            int regenCount = Math.min(3, waveNumber / 4);
            for (int i = 0; i < regenCount; i++) {
                waveEnemies.add(factory.createEnemy(EnemyType.REGENERATING, waveNumber));
            }
        }
        
        // Shuffle the list for random spawn order
        Collections.shuffle(waveEnemies);
        
        // Add to spawn queue
        currentWaveEnemies.addAll(waveEnemies);
    }
    
    /**
     * Generate a boss wave
     */
    private void generateBossWave(int waveNumber) {
        EnemyFactory factory = new EnemyFactory();
        
        // Add boss enemy
        currentWaveEnemies.add(factory.createEnemy(EnemyType.BOSS, waveNumber));
        
        // Add supporting enemies
        int supportCount = Math.min(15, waveNumber / 2);
        for (int i = 0; i < supportCount; i++) {
            if (i % 3 == 0) {
                currentWaveEnemies.add(factory.createEnemy(EnemyType.ARMORED, waveNumber - 2));
            } else if (i % 3 == 1) {
                currentWaveEnemies.add(factory.createEnemy(EnemyType.FAST, waveNumber - 2));
            } else {
                currentWaveEnemies.add(factory.createEnemy(EnemyType.FLYING, waveNumber - 2));
            }
        }
    }
    
    /**
     * Get the next enemy to spawn
     */
    public Enemy getNextEnemy(double deltaTime) {
        if (!waveActive || currentWaveEnemies.isEmpty()) {
            return null;
        }
        
        timeSinceLastSpawn += deltaTime;
        
        if (timeSinceLastSpawn >= spawnInterval) {
            timeSinceLastSpawn = 0.0;
            return currentWaveEnemies.poll();
        }
        
        return null;
    }
    
    /**
     * Check if there are more enemies to spawn
     */
    public boolean hasMoreEnemies() {
        return !currentWaveEnemies.isEmpty();
    }
    
    /**
     * End the current wave
     */
    public void endWave() {
        waveActive = false;
        currentWaveEnemies.clear();
    }
    
    /**
     * Get wave information
     */
    public WaveInfo getWaveInfo(int waveNumber) {
        if (waveNumber % 10 == 0) {
            return new WaveInfo(waveNumber, "Boss Wave", "Prepare for a challenging boss enemy!");
        } else if (waveNumber % 5 == 0) {
            return new WaveInfo(waveNumber, "Elite Wave", "Stronger enemies incoming!");
        } else {
            return new WaveInfo(waveNumber, "Wave " + waveNumber, "Standard enemy wave");
        }
    }
    
    // Getters
    public boolean isWaveActive() {
        return waveActive;
    }
    
    public int getCurrentWaveNumber() {
        return currentWaveNumber;
    }
    
    public int getRemainingEnemies() {
        return currentWaveEnemies.size();
    }
    
    public double getTimeUntilNextSpawn() {
        return Math.max(0, spawnInterval - timeSinceLastSpawn);
    }
}

/**
 * Wave information class
 */
class WaveInfo {
    private final int waveNumber;
    private final String title;
    private final String description;
    
    public WaveInfo(int waveNumber, String title, String description) {
        this.waveNumber = waveNumber;
        this.title = title;
        this.description = description;
    }
    
    public int getWaveNumber() {
        return waveNumber;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
}


