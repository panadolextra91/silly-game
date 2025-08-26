package entities.towers;

import entities.enemies.Enemy;
//import entities.enemies.DamageType;
import java.awt.Color;

/**
 * Poison Tower - Damage over time, poison effects
 */
public class PoisonTower extends Tower {
    
    public PoisonTower(double x, double y) {
        super(x, y, 15, 65.0, 1.0, new Color(128, 255, 0)); // Lime green
        this.upgradeCost = 55;
    }
    
    @Override
    protected void performAttack(Enemy target) {
        fireProjectile(target, damage, entities.enemies.DamageType.POISON);
        // Apply poison slow/dot as fallback
        target.applyPoison(5.0, damage / 2);
        addDamageDealt(damage);
    }
    
    @Override
    protected void applyUpgrade() {
        switch (level) {
            case 2:
                damage += 6;
                fireRate += 0.2;
                color = new Color(154, 205, 50); // Yellow green
                break;
            case 3:
                damage += 10;
                range += 10;
                fireRate += 0.3;
                color = new Color(107, 142, 35); // Olive drab
                break;
            case 4:
                damage += 15;
                fireRate += 0.4;
                range += 20;
                color = new Color(85, 107, 47); // Dark olive green
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
        return 90;
    }
}
