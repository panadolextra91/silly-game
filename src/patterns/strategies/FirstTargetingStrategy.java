package patterns.strategies;

import entities.enemies.Enemy;
import entities.towers.Tower;
import java.util.List;

/**
 * Target the first enemy in the path (closest to exit)
 */
public class FirstTargetingStrategy implements TargetingStrategy {
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
