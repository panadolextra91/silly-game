package entities.projectiles;

import entities.enemies.Enemy;
import entities.enemies.DamageType;
import utils.Vector2D;
import java.awt.Color;

/**
 * Arrow projectile for archer towers
 */
public class ArrowProjectile extends Projectile {
    
    public ArrowProjectile(double startX, double startY, Enemy target, int damage, DamageType damageType, Color color) {
        super(new Vector2D(startX, startY), target.getPosition(), damage, 200.0);
        this.target = target;
        this.color = color;
        this.size = 3;
    }
    
    @Override
    protected void onHit(Enemy enemy) {
        enemy.takeDamage(damage, DamageType.PHYSICAL);
    }
}
