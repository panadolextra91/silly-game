package entities.towers;

import entities.enemies.Enemy;
//import entities.projectiles.*;
import java.awt.Color;

/**
 * Archer Tower - Basic physical damage tower
 * Fast firing rate, moderate damage, good range
 */
public class ArcherTower extends Tower {
    
    public ArcherTower(double x, double y) {
        super(x, y, 15, 80.0, 2.0, new Color(139, 69, 19)); // Brown color
        this.upgradeCost = 25;
    }
    
    @Override
    protected void performAttack(Enemy target) {
        fireProjectile(target, damage, entities.enemies.DamageType.PHYSICAL);
        addDamageDealt(damage);
    }
    
    @Override
    protected void applyUpgrade() {
        switch (level) {
            case 2:
                damage += 8;
                fireRate += 0.5;
                color = new Color(160, 82, 45); // Lighter brown
                break;
            case 3:
                damage += 12;
                range += 20;
                fireRate += 0.5;
                color = new Color(210, 180, 140); // Tan
                break;
            case 4:
                damage += 20;
                fireRate += 1.0;
                range += 30;
                color = new Color(255, 215, 0); // Gold
                break;
        }
        upgradeCost = (int) (upgradeCost * 1.5);
    }
    
    @Override
    protected int getMaxLevel() {
        return 4;
    }
    
    @Override
    public int getBaseCost() {
        return 50;
    }
    

}
