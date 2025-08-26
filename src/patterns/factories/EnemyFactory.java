package patterns.factories;

import entities.enemies.BasicEnemy;
import entities.enemies.Enemy;
import core.EnemyType;

/**
 * Factory pattern implementation for creating enemies
 */
public class EnemyFactory {
    
    /**
     * Create an enemy of the specified type
     */
    public Enemy createEnemy(EnemyType type, int waveNumber) {
        // For now, just return BasicEnemy for all types
        // In a more complex implementation, you'd have separate enemy classes
        return new BasicEnemy(0, 0, waveNumber);
    }
    
    /**
     * Create a random enemy appropriate for the wave number
     */
    public Enemy createRandomEnemy(int waveNumber) {
        EnemyType[] availableTypes = getAvailableTypes(waveNumber);
        EnemyType randomType = availableTypes[(int) (Math.random() * availableTypes.length)];
        return createEnemy(randomType, waveNumber);
    }
    
    /**
     * Get available enemy types for a given wave number
     */
    private EnemyType[] getAvailableTypes(int waveNumber) {
        if (waveNumber <= 3) {
            return new EnemyType[]{EnemyType.BASIC, EnemyType.FAST};
        } else if (waveNumber <= 7) {
            return new EnemyType[]{EnemyType.BASIC, EnemyType.FAST, EnemyType.ARMORED};
        } else if (waveNumber <= 12) {
            return new EnemyType[]{EnemyType.BASIC, EnemyType.FAST, EnemyType.ARMORED, EnemyType.FLYING};
        } else if (waveNumber <= 20) {
            return new EnemyType[]{
                EnemyType.BASIC, EnemyType.FAST, EnemyType.ARMORED, 
                EnemyType.FLYING, EnemyType.FIRE_ELEMENTAL, EnemyType.ICE_ELEMENTAL
            };
        } else {
            return EnemyType.values(); // All types available
        }
    }
    
    /**
     * Get the cost to spawn an enemy (for balance calculations)
     */
    public int getEnemyCost(EnemyType type) {
        switch (type) {
            case BASIC: return 1;
            case FAST: return 1;
            case ARMORED: return 2;
            case FLYING: return 2;
            case FIRE_ELEMENTAL: return 3;
            case ICE_ELEMENTAL: return 3;
            case REGENERATING: return 4;
            case BOSS: return 10;
            default: return 1;
        }
    }
}
