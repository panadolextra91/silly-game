package core;

//import core.GameObject;
//import utils.Vector2D;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;

/**
 * The house that players must protect
 */
public class House extends GameObject {
    private int maxHealth;
    private int currentHealth;
    private Color color;
    private int size;
    
    public House(double x, double y) {
        super(x, y);
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.color = new Color(139, 69, 19); // Brown house color
        this.size = 60; // Bigger house
    }
    
    @Override
    protected void updateLogic(double deltaTime) {
        // House doesn't need logic updates
    }
    
    @Override
    protected void updateGraphics(double deltaTime) {
        // House doesn't need graphics updates
    }
    
    @Override
    protected void draw(Graphics2D g2d) {
        // Draw house as a brown rectangle
        g2d.setColor(color);
        int x = (int) (position.x - size / 2);
        int y = (int) (position.y - size / 2);
        g2d.fillRect(x, y, size, size);
        
        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, size, size);
        
        // Draw house symbol
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("H", x + size/2 - 8, y + size/2 + 8);
        
        // Draw health bar
        drawHealthBar(g2d);
    }
    
    /**
     * Draw health bar above the house
     */
    private void drawHealthBar(Graphics2D g2d) {
        int barWidth = size;
        int barHeight = 6;
        int x = (int) (position.x - barWidth / 2);
        int y = (int) (position.y - size / 2 - barHeight - 5);
        
        // Background
        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, barWidth, barHeight);
        
        // Health
        g2d.setColor(Color.GREEN);
        int healthWidth = (int) (barWidth * ((double) currentHealth / maxHealth));
        g2d.fillRect(x, y, healthWidth, barHeight);
        
        // Border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, barWidth, barHeight);
    }
    
    /**
     * Take damage from enemy attack
     */
    public void takeDamage(int damage) {
        int before = currentHealth;
        currentHealth -= damage;
        System.out.println("[DMG][House] dmg=" + damage + " from=" + before + " -> " + currentHealth);
        if (currentHealth <= 0) {
            currentHealth = 0;
            destroy();
        }
    }
    
    /**
     * Check if house is destroyed
     */
    public boolean isDestroyed() {
        return currentHealth <= 0;
    }
    
    /**
     * Get current health
     */
    public int getCurrentHealth() {
        return currentHealth;
    }
    
    /**
     * Get max health
     */
    public int getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * Get health percentage
     */
    public double getHealthPercentage() {
        return (double) currentHealth / maxHealth;
    }
}
