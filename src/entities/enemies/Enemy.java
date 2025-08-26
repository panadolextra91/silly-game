package entities.enemies;

import core.GameObject;
import core.GameState;
import entities.towers.Tower;
import utils.Vector2D;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.List;

/**
 * Abstract base class for all enemies
 * Implements Template Method pattern for enemy behavior
 */
public abstract class Enemy extends GameObject {
    protected int maxHp;
    protected int currentHp;
    protected double speed;
    protected int reward;
    protected int damage; // Damage dealt to player when reaching end
    protected int towerDamage; // Damage dealt to towers
    protected List<Vector2D> path;
    protected int currentPathIndex;
    protected double pathProgress; // 0.0 to 1.0, how far along the entire path
    protected Color color;
    protected int size;
    
    // Ranged attack
    protected double rangedRange = 90.0;
    protected double rangedFireRate = 0.5; // shots per second against towers
    protected double timeSinceLastRangedShot = 0.0;
    
    // Resistance system
    protected double physicalResistance;
    protected double magicalResistance;
    protected double fireResistance;
    protected double iceResistance;
    protected double poisonResistance;
    
    // Status effects
    protected boolean frozen;
    protected boolean poisoned;
    protected double freezeTimeRemaining;
    protected double poisonTimeRemaining;
    protected int poisonDamagePerSecond;
    
    public Enemy(double x, double y, int hp, double speed, int reward, Color color) {
        super(x, y);
        this.maxHp = hp;
        this.currentHp = hp;
        this.speed = speed;
        this.reward = reward;
        this.damage = 1;
        this.towerDamage = 5; // Default tower damage
        this.color = color;
        this.size = 12;
        this.currentPathIndex = 0;
        this.pathProgress = 0.0;
        
        // Default resistances (0.0 = no resistance, 1.0 = immune)
        this.physicalResistance = 0.0;
        this.magicalResistance = 0.0;
        this.fireResistance = 0.0;
        this.iceResistance = 0.0;
        this.poisonResistance = 0.0;
        
        // Status effects
        this.frozen = false;
        this.poisoned = false;
        this.freezeTimeRemaining = 0.0;
        this.poisonTimeRemaining = 0.0;
        this.poisonDamagePerSecond = 0;
    }
    
    @Override
    protected void updateLogic(double deltaTime) {
        // Timers
        timeSinceLastRangedShot += deltaTime;
        updateStatusEffects(deltaTime);
        
        if (!frozen) {
            moveAlongPath(deltaTime);
        }
        
        // Check if enemy reached the end
        if (currentPathIndex >= path.size() - 1) {
            reachedEnd();
        }
        
        // Check if enemy is dead
        if (currentHp <= 0) {
            die();
        }
        
        // Attack towers (ranged first, then melee if very close)
        attemptRangedAttack();
        attackNearbyTowers();
    }
    
    @Override
    protected void updateGraphics(double deltaTime) {
        // Graphics updates can be handled here if needed
        // For simple colored circles, most work is done in draw()
    }
    
    @Override
    protected void draw(Graphics2D g2d) {
        // Draw the enemy as a colored circle
        g2d.setColor(getDisplayColor());
        int x = (int) (position.x - size / 2);
        int y = (int) (position.y - size / 2);
        g2d.fillOval(x, y, size, size);
        
        // Draw health bar
        drawHealthBar(g2d);
        
        // Draw status effect indicators
        drawStatusEffects(g2d);
    }
    
    /**
     * Move the enemy along its assigned path
     */
    private void moveAlongPath(double deltaTime) {
        if (path == null || path.isEmpty() || currentPathIndex >= path.size() - 1) {
            return;
        }
        
        Vector2D currentTarget = path.get(currentPathIndex + 1);
        Vector2D direction = currentTarget.subtract(position).normalize();
        Vector2D movement = direction.multiply(speed * deltaTime);
        
        // Move towards next waypoint
        position = position.add(movement);
        
        // Check if we've reached the current waypoint
        if (position.distanceTo(currentTarget) < 5.0) {
            currentPathIndex++;
            updatePathProgress();
        }
    }
    
    /**
     * Update the overall progress along the path (0.0 to 1.0)
     */
    private void updatePathProgress() {
        if (path == null || path.isEmpty()) {
            pathProgress = 0.0;
            return;
        }
        
        pathProgress = (double) currentPathIndex / (path.size() - 1);
    }
    
    /**
     * Update status effects like freeze and poison
     */
    private void updateStatusEffects(double deltaTime) {
        // Handle freeze effect
        if (frozen) {
            freezeTimeRemaining -= deltaTime;
            if (freezeTimeRemaining <= 0) {
                frozen = false;
            }
        }
        
        // Handle poison effect
        if (poisoned) {
            poisonTimeRemaining -= deltaTime;
            if (poisonTimeRemaining <= 0) {
                poisoned = false;
            } else {
                // Apply poison damage
                takeDamage((int) (poisonDamagePerSecond * deltaTime), DamageType.POISON);
            }
        }
    }
    
    /**
     * Apply damage to the enemy with resistance calculations
     */
    public void takeDamage(int damage, DamageType damageType) {
        double resistance = getResistance(damageType);
        int actualDamage = (int) (damage * (1.0 - resistance));
        int before = currentHp;
        currentHp -= actualDamage;
        System.out.println("[DMG][Enemy] id=" + getId() + " type=" + getClass().getSimpleName() +
            " dmg=" + actualDamage + " (" + damageType + ") from=" + before + " -> " + currentHp);
        
        if (currentHp < 0) {
            currentHp = 0;
        }
    }
    
    /**
     * Get resistance value for a specific damage type
     */
    private double getResistance(DamageType damageType) {
        switch (damageType) {
            case PHYSICAL: return physicalResistance;
            case MAGICAL: return magicalResistance;
            case FIRE: return fireResistance;
            case ICE: return iceResistance;
            case POISON: return poisonResistance;
            default: return 0.0;
        }
    }
    
    /**
     * Apply freeze effect
     */
    public void applyFreeze(double duration) {
        if (iceResistance < 1.0) {
            frozen = true;
            freezeTimeRemaining = duration * (1.0 - iceResistance);
        }
    }
    
    /**
     * Apply poison effect
     */
    public void applyPoison(double duration, int damagePerSecond) {
        if (poisonResistance < 1.0) {
            poisoned = true;
            poisonTimeRemaining = duration * (1.0 - poisonResistance);
            this.poisonDamagePerSecond = (int) (damagePerSecond * (1.0 - poisonResistance));
        }
    }
    
    /**
     * Get the display color (may be modified by status effects)
     */
    private Color getDisplayColor() {
        if (frozen) {
            return new Color(
                (color.getRed() + 100) / 2,
                (color.getGreen() + 100) / 2,
                255
            );
        } else if (poisoned) {
            return new Color(
                Math.min(255, color.getRed() + 50),
                Math.max(0, color.getGreen() - 50),
                Math.min(255, color.getBlue() + 100)
            );
        }
        return color;
    }
    
    /**
     * Draw health bar above the enemy
     */
    private void drawHealthBar(Graphics2D g2d) {
        // Always show health bar for better visibility
        int barWidth = size;
        int barHeight = 4;
        int x = (int) (position.x - barWidth / 2);
        int y = (int) (position.y - size / 2 - barHeight - 2);
        
        // Background
        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, barWidth, barHeight);
        
        // Health
        g2d.setColor(Color.GREEN);
        int healthWidth = (int) (barWidth * ((double) currentHp / maxHp));
        g2d.fillRect(x, y, healthWidth, barHeight);
        
        // Border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, barWidth, barHeight);
    }
    
    /**
     * Draw status effect indicators
     */
    private void drawStatusEffects(Graphics2D g2d) {
        int indicatorSize = 6;
        int x = (int) (position.x + size / 2 + 2);
        int y = (int) (position.y - size / 2);
        
        if (frozen) {
            g2d.setColor(Color.CYAN);
            g2d.fillOval(x, y, indicatorSize, indicatorSize);
            y += indicatorSize + 1;
        }
        
        if (poisoned) {
            g2d.setColor(new Color(128, 255, 0));
            g2d.fillOval(x, y, indicatorSize, indicatorSize);
        }
    }
    
    /**
     * Called when enemy reaches the end of the path
     */
    protected void reachedEnd() {
        // This will be handled by the game state
        destroy();
    }
    
    /**
     * Called when enemy dies
     */
    protected void die() {
        // This will be handled by the game state
        destroy();
    }
    
    // Getters and setters
    public void setPath(List<Vector2D> path) {
        this.path = path;
        if (path != null && !path.isEmpty()) {
            setPosition(path.get(0));
            currentPathIndex = 0;
            updatePathProgress();
        }
    }
    
    public List<Vector2D> getPath() {
        return path;
    }
    
    public double getPathProgress() {
        return pathProgress;
    }
    
    public int getCurrentHp() {
        return currentHp;
    }
    
    public int getMaxHp() {
        return maxHp;
    }
    
    public int getReward() {
        return reward;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public int getTowerDamage() {
        return towerDamage;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public boolean isFrozen() {
        return frozen;
    }
    
    public boolean isPoisoned() {
        return poisoned;
    }
    
    /**
     * Set enemy speed (for status effects)
     */
    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
    }
    
    /**
     * Attack nearby towers
     */
    private void attackNearbyTowers() {
        if (!isActive()) return;
        
        List<Tower> towers = GameState.getInstance().getTowers();
        for (Tower tower : towers) {
            if (tower.isActive() && !tower.isDestroyed()) {
                double distance = position.distanceTo(tower.getPosition());
                if (distance <= 15) { // Attack range
                    tower.takeDamage(towerDamage);
                    System.out.println("[ENEMY][Melee] enemy=" + getId() + " -> tower=" + tower.getId() + " dmg=" + towerDamage);
                    break; // Only attack one tower at a time
                }
            }
        }
    }

    private void attemptRangedAttack() {
        if (!isActive()) return;
        if (timeSinceLastRangedShot < (1.0 / rangedFireRate)) return;
        
        Tower targetTower = findNearestTowerWithin(rangedRange);
        if (targetTower != null) {
            targetTower.takeDamage(Math.max(1, towerDamage / 2)); // ranged a bit weaker than melee
            timeSinceLastRangedShot = 0.0;
            System.out.println("[ENEMY][Ranged] enemy=" + getId() + " -> tower=" + targetTower.getId());
        }
    }
    
    private Tower findNearestTowerWithin(double range) {
        Tower best = null;
        double bestDist = Double.MAX_VALUE;
        for (Tower t : GameState.getInstance().getTowers()) {
            if (t.isActive() && !t.isDestroyed()) {
                double d = position.distanceTo(t.getPosition());
                if (d <= range && d < bestDist) {
                    bestDist = d;
                    best = t;
                }
            }
        }
        return best;
    }
}


