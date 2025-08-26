package entities.projectiles;

import core.GameObject;
import entities.enemies.Enemy;
import core.GameState;
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
        // Home towards target if available
        if (target != null && target.isActive()) {
            Vector2D directionToTarget = target.getPosition().subtract(position).normalize();
            this.velocity = directionToTarget.multiply(speed);
            this.targetPosition = target.getPosition();
        }
        
        // Move projectile
        Vector2D movement = velocity.multiply(deltaTime);
        position = position.add(movement);
        distanceTraveled += movement.magnitude();
        // Debug movement occasionally
        // System.out.println("[PROJ][Move] id=" + getId() + " pos=" + position + " dist=" + String.format("%.1f", distanceTraveled));
        
        // Check if projectile should be destroyed
        boolean outOfRange = (target == null || !target.isActive()) && distanceTraveled >= maxRange;
        if (outOfRange || shouldDestroy()) {
            System.out.println("[PROJ][Destroy] id=" + getId() + " reason=" + (distanceTraveled >= maxRange ? "range" : "custom"));
            destroy();
            return;
        }
        
        // Check for collision with target
        if (target != null && target.isActive()) {
            if (position.distanceTo(target.getPosition()) <= size + 6) {
                onHit(target);
                System.out.println("[PROJ][HitTarget] proj=" + getId() + " enemy=" + target.getId() + " dmg=" + damage);
                destroy();
            }
        } else {
            // Check collision with any enemy if no specific target
            Enemy hitEnemy = checkCollisionWithEnemies();
            if (hitEnemy != null) {
                onHit(hitEnemy);
                System.out.println("[PROJ][HitAny] proj=" + getId() + " enemy=" + hitEnemy.getId() + " dmg=" + damage);
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
        for (Enemy enemy : GameState.getInstance().getEnemies()) {
            if (enemy.isActive() && position.distanceTo(enemy.getPosition()) <= size + 6) {
                return enemy;
            }
        }
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


