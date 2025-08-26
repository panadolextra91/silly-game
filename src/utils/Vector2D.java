package utils;

/**
 * 2D Vector utility class for position and movement calculations
 */
public class Vector2D {
    public double x, y;
    
    public Vector2D() {
        this(0, 0);
    }
    
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2D(Vector2D other) {
        this.x = other.x;
        this.y = other.y;
    }
    
    // Vector operations
    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }
    
    public Vector2D subtract(Vector2D other) {
        return new Vector2D(x - other.x, y - other.y);
    }
    
    public Vector2D multiply(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }
    
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }
    
    public double distanceTo(Vector2D other) {
        return subtract(other).magnitude();
    }
    
    public Vector2D normalize() {
        double mag = magnitude();
        if (mag == 0) return new Vector2D();
        return new Vector2D(x / mag, y / mag);
    }
    
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2D vector2D = (Vector2D) obj;
        return Double.compare(vector2D.x, x) == 0 && Double.compare(vector2D.y, y) == 0;
    }
}
