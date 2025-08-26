package patterns.strategies;

import entities.enemies.Enemy;
import entities.towers.Tower;
import java.util.List;

/**
 * Strategy Pattern implementation for tower targeting
 * Allows different targeting behaviors to be swapped at runtime
 */
public interface TargetingStrategy {
    /**
     * Select the best target from available enemies
     * @param enemies List of enemies in range
     * @param tower The tower doing the targeting
     * @return Selected enemy or null if no valid target
     */
    Enemy selectTarget(List<Enemy> enemies, Tower tower);
    
    /**
     * Get the name of this targeting strategy
     */
    String getName();
}

/**
 * Target the first enemy in the path (closest to exit)
 */
class FirstTargetingStrategy implements TargetingStrategy {
    @Override
    public Enemy selectTarget(List<Enemy> enemies, Tower tower) {
        return enemies.stream()
                .filter(enemy -> enemy.isActive())
                .max((e1, e2) -> Double.compare(e1.getPathProgress(), e2.getPathProgress()))
                .orElse(null);
    }
    
    @Override
    public String getName() {
        return "First";
    }
}

/**
 * Target the last enemy in the path (closest to entrance)
 */
class LastTargetingStrategy implements TargetingStrategy {
    @Override
    public Enemy selectTarget(List<Enemy> enemies, Tower tower) {
        return enemies.stream()
                .filter(enemy -> enemy.isActive())
                .min((e1, e2) -> Double.compare(e1.getPathProgress(), e2.getPathProgress()))
                .orElse(null);
    }
    
    @Override
    public String getName() {
        return "Last";
    }
}

/**
 * Target the strongest enemy (highest HP)
 */
class StrongestTargetingStrategy implements TargetingStrategy {
    @Override
    public Enemy selectTarget(List<Enemy> enemies, Tower tower) {
        return enemies.stream()
                .filter(enemy -> enemy.isActive())
                .max((e1, e2) -> Integer.compare(e1.getCurrentHp(), e2.getCurrentHp()))
                .orElse(null);
    }
    
    @Override
    public String getName() {
        return "Strongest";
    }
}

/**
 * Target the weakest enemy (lowest HP)
 */
class WeakestTargetingStrategy implements TargetingStrategy {
    @Override
    public Enemy selectTarget(List<Enemy> enemies, Tower tower) {
        return enemies.stream()
                .filter(enemy -> enemy.isActive())
                .min((e1, e2) -> Integer.compare(e1.getCurrentHp(), e2.getCurrentHp()))
                .orElse(null);
    }
    
    @Override
    public String getName() {
        return "Weakest";
    }
}

/**
 * Target the closest enemy to the tower
 */
class ClosestTargetingStrategy implements TargetingStrategy {
    @Override
    public Enemy selectTarget(List<Enemy> enemies, Tower tower) {
        return enemies.stream()
                .filter(enemy -> enemy.isActive())
                .min((e1, e2) -> Double.compare(
                    tower.getPosition().distanceTo(e1.getPosition()),
                    tower.getPosition().distanceTo(e2.getPosition())
                ))
                .orElse(null);
    }
    
    @Override
    public String getName() {
        return "Closest";
    }
}

/**
 * Factory for creating targeting strategies
 */
class TargetingStrategyFactory {
    public static TargetingStrategy createStrategy(TargetingType type) {
        switch (type) {
            case FIRST: return new FirstTargetingStrategy();
            case LAST: return new LastTargetingStrategy();
            case STRONGEST: return new StrongestTargetingStrategy();
            case WEAKEST: return new WeakestTargetingStrategy();
            case CLOSEST: return new ClosestTargetingStrategy();
            default: return new FirstTargetingStrategy();
        }
    }
}

/**
 * Enum for targeting strategy types
 */
enum TargetingType {
    FIRST, LAST, STRONGEST, WEAKEST, CLOSEST
}
