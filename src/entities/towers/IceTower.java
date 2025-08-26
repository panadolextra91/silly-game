package entities.towers;

import entities.enemies.Enemy;
//import entities.enemies.DamageType;
import java.awt.Color;

/**
 * Ice Tower - Slows enemies, ice damage
 */
public class IceTower extends Tower {
    
    public IceTower(double x, double y) {
        super(x, y, 20, 75.0, 1.5, new Color(173, 216, 230)); // Light blue
        this.upgradeCost = 45;
    }
    
    @Override
    protected void performAttack(Enemy target) {
        fireProjectile(target, damage, entities.enemies.DamageType.ICE);
        // Apply freeze effect upon hit is handled in projectile; as a fallback, apply here too
        target.applyFreeze(2.0);
        addDamageDealt(damage);
    }
    
    @Override
    protected void applyUpgrade() {
        switch (level) {
            case 2:
                damage += 8;
                fireRate += 0.3;
                color = new Color(135, 206, 235); // Sky blue
                break;
            case 3:
                damage += 12;
                range += 15;
                fireRate += 0.4;
                color = new Color(100, 149, 237); // Cornflower blue
                break;
            case 4:
                damage += 18;
                fireRate += 0.5;
                range += 25;
                color = new Color(65, 105, 225); // Royal blue
                break;
        }
        upgradeCost = (int) (upgradeCost * 1.3);
    }
    
    @Override
    protected int getMaxLevel() {
        return 4;
    }
    
    @Override
    public int getBaseCost() {
        return 70;
    }
}
