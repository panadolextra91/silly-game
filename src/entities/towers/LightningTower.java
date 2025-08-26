package entities.towers;

import entities.enemies.Enemy;
import entities.enemies.DamageType;
import java.awt.Color;

/**
 * Lightning Tower - Chain lightning, magical damage
 */
public class LightningTower extends Tower {
    
    public LightningTower(double x, double y) {
        super(x, y, 25, 90.0, 1.2, new Color(255, 255, 0)); // Yellow
        this.upgradeCost = 60;
    }
    
    @Override
    protected void performAttack(Enemy target) {
        fireProjectile(target, target.getCurrentHp(), entities.enemies.DamageType.MAGICAL);
        addDamageDealt(target.getCurrentHp());
    }
    
    @SuppressWarnings("unused")
    private void applyChainLightning(Enemy initialTarget, int damage, int maxChains) {
        Enemy currentTarget = initialTarget;
        int currentDamage = damage;
        
        for (int i = 0; i < maxChains && currentTarget != null; i++) {
            currentTarget.takeDamage(currentDamage, DamageType.MAGICAL);
            
            // Find next target (closest enemy within chain range)
            Enemy nextTarget = null;
            double closestDistance = Double.MAX_VALUE;
            
            for (Enemy enemy : getCurrentEnemies()) {
                if (enemy != currentTarget && enemy.isActive()) {
                    double distance = currentTarget.getPosition().distanceTo(enemy.getPosition());
                    if (distance <= 40.0 && distance < closestDistance) {
                        nextTarget = enemy;
                        closestDistance = distance;
                    }
                }
            }
            
            currentTarget = nextTarget;
            currentDamage = (int) (currentDamage * 0.7); // Damage decreases with each chain
        }
    }
    
    @Override
    protected void applyUpgrade() {
        switch (level) {
            case 2:
                damage += 10;
                fireRate += 0.3;
                color = new Color(255, 255, 102); // Light yellow
                break;
            case 3:
                damage += 15;
                range += 20;
                fireRate += 0.4;
                color = new Color(255, 255, 153); // Lighter yellow
                break;
            case 4:
                damage += 25;
                fireRate += 0.5;
                range += 30;
                color = new Color(255, 215, 0); // Gold
                break;
        }
        upgradeCost = (int) (upgradeCost * 1.4);
    }
    
    @Override
    protected int getMaxLevel() {
        return 4;
    }
    
    @Override
    public int getBaseCost() {
        return 85;
    }
}
