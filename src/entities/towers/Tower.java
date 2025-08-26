package entities.towers;

import core.GameObject;
import entities.enemies.Enemy;
import patterns.strategies.TargetingStrategy;
import patterns.strategies.FirstTargetingStrategy;
import utils.Vector2D;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

/**
 * Abstract base class for all towers
 * Implements Template Method pattern for tower behavior
 */
public abstract class Tower extends GameObject {
    protected int damage;
    protected double range;
    protected double fireRate; // shots per second
    protected double timeSinceLastShot;
    protected TargetingStrategy targetingStrategy;
    protected Enemy currentTarget;
    protected Color color;
    protected int size;
    protected int level;
    protected int upgradeCost;
    protected boolean canTargetFlying;
    
    // Tower stats
    protected int totalKills;
    protected int totalDamageDealt;
    
    public Tower(double x, double y, int damage, double range, double fireRate, Color color) {
        super(x, y);
        this.damage = damage;
        this.range = range;
        this.fireRate = fireRate;
        this.color = color;
        this.size = 20;
        this.level = 1;
        this.upgradeCost = 50;
        this.canTargetFlying = true;
        this.timeSinceLastShot = 0.0;
        this.totalKills = 0;
        this.totalDamageDealt = 0;
        
        // Default targeting strategy
        this.targetingStrategy = new FirstTargetingStrategy();
    }
    
    @Override
    protected void updateLogic(double deltaTime) {
        timeSinceLastShot += deltaTime;
        
        // Try to attack if ready
        if (canAttack()) {
            attemptAttack();
        }
    }
    
    @Override
    protected void updateGraphics(double deltaTime) {
        // Graphics updates if needed
    }
    
    @Override
    protected void draw(Graphics2D g2d) {
        // Draw tower as colored rectangle
        g2d.setColor(color);
        int x = (int) (position.x - size / 2);
        int y = (int) (position.y - size / 2);
        g2d.fillRect(x, y, size, size);
        
        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, size, size);
        
        // Draw level indicator
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.valueOf(level), x + 2, y + size - 2);
        
        // Draw range indicator if tower is selected
        if (isSelected()) {
            drawRange(g2d);
        }
    }
    
    /**
     * Template method for attack sequence
     */
    public final void attemptAttack() {
        List<Enemy> enemiesInRange = getEnemiesInRange();
        if (!enemiesInRange.isEmpty()) {
            Enemy target = targetingStrategy.selectTarget(enemiesInRange, this);
            if (target != null) {
                performAttack(target);
                timeSinceLastShot = 0.0;
            }
        }
    }
    
    /**
     * Abstract method for specific attack implementation
     */
    protected abstract void performAttack(Enemy target);
    
    /**
     * Check if tower can attack (cooldown ready)
     */
    protected boolean canAttack() {
        return timeSinceLastShot >= (1.0 / fireRate);
    }
    
    /**
     * Get all enemies within range
     */
    protected List<Enemy> getEnemiesInRange() {
        List<Enemy> enemiesInRange = new ArrayList<>();
        List<Enemy> allEnemies = getCurrentEnemies(); // This will be provided by game state
        
        for (Enemy enemy : allEnemies) {
            if (enemy.isActive() && isInRange(enemy) && canTarget(enemy)) {
                enemiesInRange.add(enemy);
            }
        }
        
        return enemiesInRange;
    }
    
    /**
     * Check if enemy is within attack range
     */
    protected boolean isInRange(Enemy enemy) {
        return position.distanceTo(enemy.getPosition()) <= range;
    }
    
    /**
     * Check if this tower can target the given enemy
     */
    protected boolean canTarget(Enemy enemy) {
        // Basic implementation - subclasses can override for special targeting rules
        return canTargetFlying || !isFlying(enemy);
    }
    
    /**
     * Check if enemy is flying (simplified check based on enemy type)
     */
    protected boolean isFlying(Enemy enemy) {
        // This is a simplified implementation
        // In a real game, you might have an interface or property for flying enemies
        return enemy.getClass().getSimpleName().contains("Flying");
    }
    
    /**
     * Draw the tower's range circle
     */
    protected void drawRange(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 50)); // Semi-transparent white
        int diameter = (int) (range * 2);
        int x = (int) (position.x - range);
        int y = (int) (position.y - range);
        g2d.fillOval(x, y, diameter, diameter);
        
        g2d.setColor(Color.WHITE);
        g2d.drawOval(x, y, diameter, diameter);
    }
    
    /**
     * Upgrade the tower to the next level
     */
    public boolean upgrade() {
        if (canUpgrade()) {
            level++;
            applyUpgrade();
            return true;
        }
        return false;
    }
    
    /**
     * Check if tower can be upgraded
     */
    public boolean canUpgrade() {
        return level < getMaxLevel();
    }
    
    /**
     * Apply upgrade effects - to be implemented by subclasses
     */
    protected abstract void applyUpgrade();
    
    /**
     * Get maximum upgrade level
     */
    protected abstract int getMaxLevel();
    
    /**
     * Get current enemies from game state
     * This method will be overridden or provided by dependency injection
     */
    protected List<Enemy> getCurrentEnemies() {
        // This is a placeholder - will be provided by the game state
        return new ArrayList<>();
    }
    
    /**
     * Check if tower is currently selected
     */
    protected boolean isSelected() {
        // This will be managed by the UI system
        return false;
    }
    
    /**
     * Calculate total cost including upgrades
     */
    public int getTotalCost() {
        int totalCost = getBaseCost();
        for (int i = 1; i < level; i++) {
            totalCost += upgradeCost * i;
        }
        return totalCost;
    }
    
    /**
     * Get base cost of the tower
     */
    public abstract int getBaseCost();
    
    /**
     * Get sell value (typically 70% of total cost)
     */
    public int getSellValue() {
        return (int) (getTotalCost() * 0.7);
    }
    
    // Getters and setters
    public int getDamage() {
        return damage;
    }
    
    public double getRange() {
        return range;
    }
    
    public double getFireRate() {
        return fireRate;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getUpgradeCost() {
        return upgradeCost * level;
    }
    
    public TargetingStrategy getTargetingStrategy() {
        return targetingStrategy;
    }
    
    public void setTargetingStrategy(TargetingStrategy targetingStrategy) {
        this.targetingStrategy = targetingStrategy;
    }
    
    public int getTotalKills() {
        return totalKills;
    }
    
    public int getTotalDamageDealt() {
        return totalDamageDealt;
    }
    
    public void addKill() {
        totalKills++;
    }
    
    public void addDamageDealt(int damage) {
        totalDamageDealt += damage;
    }
    
    public String getStats() {
        return String.format("Level %d | Kills: %d | Damage: %d", level, totalKills, totalDamageDealt);
    }
}
