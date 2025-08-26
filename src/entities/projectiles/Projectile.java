package entities.projectiles;

import core.GameObject;
import entities.enemies.Enemy;
import utils.Vector2D;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Base class for all projectiles
 */
public abstract class Projectile extends GameObject {
    protected Vector2D velocity;
    protected int damage;
    protected double speed;
    protected Enemy target;
    protected Vector2D targetPosition;
    protected double maxRange;
    protected double distanceTraveled;
    protected Color color;
    protected int size;
    
    public Projectile(Vector2D startPos, Vector2D targetPos, int damage, double speed) {
        super(startPos.x, startPos.y);
        this.targetPosition = new Vector2D(targetPos);
        this.damage = damage;
        this.speed = speed;
        this.maxRange = 300.0;
        this.distanceTraveled = 0.0;
        this.size = 4;
        this.color = Color.BLACK;
        
        // Calculate initial velocity
        Vector2D direction = targetPosition.subtract(position).normalize();
        this.velocity = direction.multiply(speed);
    }
    
    @Override
    protected void updateLogic(double deltaTime) {
        // Move projectile
        Vector2D movement = velocity.multiply(deltaTime);
        position = position.add(movement);
        distanceTraveled += movement.magnitude();
        
        // Check if projectile should be destroyed
        if (distanceTraveled >= maxRange || shouldDestroy()) {
            destroy();
            return;
        }
        
        // Check for collision with target
        if (target != null && target.isActive()) {
            if (position.distanceTo(target.getPosition()) <= size + 6) {
                onHit(target);
                destroy();
            }
        } else {
            // Check collision with any enemy if no specific target
            Enemy hitEnemy = checkCollisionWithEnemies();
            if (hitEnemy != null) {
                onHit(hitEnemy);
                destroy();
            }
        }
    }
    
    @Override
    protected void updateGraphics(double deltaTime) {
        // Graphics updates if needed
    }
    
    @Override
    protected void draw(Graphics2D g2d) {
        g2d.setColor(color);
        int x = (int) (position.x - size / 2);
        int y = (int) (position.y - size / 2);
        g2d.fillOval(x, y, size, size);
    }
    
    /**
     * Called when projectile hits a target
     */
    protected abstract void onHit(Enemy enemy);
    
    /**
     * Check if projectile should be destroyed
     */
    protected boolean shouldDestroy() {
        return false; // Override in subclasses if needed
    }
    
    /**
     * Check collision with all enemies
     */
    protected Enemy checkCollisionWithEnemies() {
        // This would be provided by the game state
        // For now, return null
        return null;
    }
    
    // Getters
    public int getDamage() {
        return damage;
    }
    
    public Enemy getTarget() {
        return target;
    }
    
    public void setTarget(Enemy target) {
        this.target = target;
    }
}

/**
 * Arrow projectile for archer towers
 */
class ArrowProjectile extends Projectile {
    
    public ArrowProjectile(Vector2D startPos, Vector2D targetPos, int damage, double speed) {
        super(startPos, targetPos, damage, speed);
        this.color = new Color(139, 69, 19); // Brown
        this.size = 3;
    }
    
    @Override
    protected void onHit(Enemy enemy) {
        enemy.takeDamage(damage, entities.enemies.DamageType.PHYSICAL);
    }
}

/**
 * Cannonball projectile for cannon towers
 */
class CannonballProjectile extends Projectile {
    private double splashRadius;
    
    public CannonballProjectile(Vector2D startPos, Vector2D targetPos, int damage, double speed) {
        super(startPos, targetPos, damage, speed);
        this.color = Color.DARK_GRAY;
        this.size = 6;
        this.splashRadius = 25.0;
    }
    
    @Override
    protected void onHit(Enemy enemy) {
        // Apply splash damage to all enemies in radius
        applySplashDamage(position, damage, splashRadius);
    }
    
    private void applySplashDamage(Vector2D center, int damage, double radius) {
        // This would iterate through all enemies in the game
        // For now, just damage the direct target
        if (target != null) {
            target.takeDamage(damage, entities.enemies.DamageType.PHYSICAL);
        }
    }
}

/**
 * Lightning bolt - instant hit
 */
class LightningBolt extends Projectile {
    
    public LightningBolt(Vector2D startPos, Vector2D targetPos, int damage) {
        super(startPos, targetPos, damage, 1000.0); // Very fast
        this.color = Color.YELLOW;
        this.size = 2;
        this.maxRange = 150.0;
    }
    
    @Override
    protected void onHit(Enemy enemy) {
        enemy.takeDamage(damage, entities.enemies.DamageType.MAGICAL);
    }
}

/**
 * Ice shard projectile
 */
class IceShardProjectile extends Projectile {
    
    public IceShardProjectile(Vector2D startPos, Vector2D targetPos, int damage, double speed) {
        super(startPos, targetPos, damage, speed);
        this.color = new Color(173, 216, 230); // Light blue
        this.size = 4;
    }
    
    @Override
    protected void onHit(Enemy enemy) {
        enemy.takeDamage(damage, entities.enemies.DamageType.ICE);
        enemy.applyFreeze(2.0);
    }
}

/**
 * Poison blob projectile
 */
class PoisonBlobProjectile extends Projectile {
    
    public PoisonBlobProjectile(Vector2D startPos, Vector2D targetPos, int damage, double speed) {
        super(startPos, targetPos, damage, speed);
        this.color = new Color(128, 255, 0); // Lime green
        this.size = 5;
    }
    
    @Override
    protected void onHit(Enemy enemy) {
        enemy.takeDamage(damage, entities.enemies.DamageType.POISON);
        enemy.applyPoison(5.0, damage / 2);
    }
}
