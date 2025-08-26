package core;

import utils.Vector2D;
import java.awt.Graphics2D;

/**
 * Abstract base class for all game objects
 * Implements Template Method pattern for consistent update lifecycle
 */
public abstract class GameObject {
    protected Vector2D position;
    protected boolean active;
    protected int id;
    private static int nextId = 1;
    
    public GameObject(double x, double y) {
        this.position = new Vector2D(x, y);
        this.active = true;
        this.id = nextId++;
    }
    
    /**
     * Template method - defines the update sequence
     * Subclasses cannot override this to ensure consistent behavior
     */
    public final void update(double deltaTime) {
        if (active) {
            updateLogic(deltaTime);
            updateGraphics(deltaTime);
        }
    }
    
    /**
     * Template method for rendering
     */
    public final void render(Graphics2D g2d) {
        if (active) {
            draw(g2d);
        }
    }
    
    // Abstract methods to be implemented by subclasses
    protected abstract void updateLogic(double deltaTime);
    protected abstract void updateGraphics(double deltaTime);
    protected abstract void draw(Graphics2D g2d);
    
    // Getters and setters
    public Vector2D getPosition() {
        return new Vector2D(position);
    }
    
    public void setPosition(Vector2D position) {
        this.position = new Vector2D(position);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public int getId() {
        return id;
    }
    
    /**
     * Called when the object should be destroyed
     */
    public void destroy() {
        this.active = false;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GameObject that = (GameObject) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
