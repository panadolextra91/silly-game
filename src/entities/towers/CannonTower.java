package entities.towers;

import entities.enemies.Enemy;
import entities.enemies.DamageType;
import java.awt.Color;

/**
 * Cannon Tower - High damage, slow firing, splash damage
 */
public class CannonTower extends Tower {
    
    public CannonTower(double x, double y) {
        super(x, y, 40, 70.0, 0.5, Color.DARK_GRAY);
        this.upgradeCost = 75;
        this.size = 25; // Larger than archer tower
    }
    
    @Override
    protected void performAttack(Enemy target) {
        // Cannon does splash damage
        applySplashDamage(target.getPosition(), damage, 25.0);
        addDamageDealt(damage);
    }
    
    private void applySplashDamage(utils.Vector2D center, int damage, double radius) {
        for (Enemy enemy : getCurrentEnemies()) {
            if (enemy.isActive() && center.distanceTo(enemy.getPosition()) <= radius) {
                enemy.takeDamage(damage, DamageType.PHYSICAL);
            }
        }
    }
    
    @Override
    protected void applyUpgrade() {
        switch (level) {
            case 2:
                damage += 20;
                fireRate += 0.2;
                color = new Color(105, 105, 105); // Dim gray
                break;
            case 3:
                damage += 30;
                range += 15;
                fireRate += 0.2;
                color = new Color(169, 169, 169); // Dark gray
                break;
            case 4:
                damage += 50;
                fireRate += 0.3;
                range += 25;
                color = new Color(192, 192, 192); // Silver
                break;
        }
        upgradeCost = (int) (upgradeCost * 1.6);
    }
    
    @Override
    protected int getMaxLevel() {
        return 4;
    }
    
    @Override
    public int getBaseCost() {
        return 120;
    }
}
