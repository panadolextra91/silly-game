package entities.enemies;

import java.awt.Color;

/**
 * Basic enemy type - balanced stats, no special resistances
 */
public class BasicEnemy extends Enemy {
    public BasicEnemy(double x, double y, int waveNumber) {
        super(x, y, 
              50 + (waveNumber * 10), // HP scales with wave
              60.0, // pixels per second
              10 + waveNumber, // reward scales with wave
              Color.RED
        );
        
        this.size = 12;
        // No special resistances - all remain at 0.0
    }
}

/**
 * Fast enemy - lower HP but higher speed
 */
class FastEnemy extends Enemy {
    public FastEnemy(double x, double y, int waveNumber) {
        super(x, y,
              30 + (waveNumber * 5), // Lower HP
              120.0, // Much faster
              15 + waveNumber,
              Color.YELLOW
        );
        
        this.size = 10; // Smaller size
    }
}

/**
 * Armored enemy - high physical resistance, slow but tanky
 */
class ArmoredEnemy extends Enemy {
    public ArmoredEnemy(double x, double y, int waveNumber) {
        super(x, y,
              100 + (waveNumber * 20), // High HP
              30.0, // Slow speed
              25 + (waveNumber * 2),
              Color.GRAY
        );
        
        this.size = 16; // Larger size
        this.physicalResistance = 0.5; // 50% physical resistance
    }
}

/**
 * Flying enemy - immune to some tower types, medium stats
 */
class FlyingEnemy extends Enemy {
    public FlyingEnemy(double x, double y, int waveNumber) {
        super(x, y,
              40 + (waveNumber * 8),
              80.0,
              20 + waveNumber,
              Color.CYAN
        );
        
        this.size = 10;
        // Flying enemies might be immune to certain ground-based attacks
    }
}

/**
 * Fire elemental - immune to fire, weak to ice
 */
class FireElemental extends Enemy {
    public FireElemental(double x, double y, int waveNumber) {
        super(x, y,
              60 + (waveNumber * 12),
              70.0,
              30 + (waveNumber * 2),
              Color.ORANGE
        );
        
        this.size = 14;
        this.fireResistance = 1.0; // Immune to fire
        this.iceResistance = -0.5; // Takes 50% more ice damage
    }
}

/**
 * Ice elemental - immune to ice, weak to fire
 */
class IceElemental extends Enemy {
    public IceElemental(double x, double y, int waveNumber) {
        super(x, y,
              60 + (waveNumber * 12),
              50.0, // Slower than fire elemental
              30 + (waveNumber * 2),
              Color.BLUE
        );
        
        this.size = 14;
        this.iceResistance = 1.0; // Immune to ice
        this.fireResistance = -0.5; // Takes 50% more fire damage
    }
}

/**
 * Regenerating enemy - slowly heals over time
 */
class RegeneratingEnemy extends Enemy {
    private double regenTimer = 0.0;
    private final double REGEN_INTERVAL = 1.0; // seconds
    private final int REGEN_AMOUNT = 5;
    
    public RegeneratingEnemy(double x, double y, int waveNumber) {
        super(x, y,
              80 + (waveNumber * 15),
              45.0,
              35 + (waveNumber * 2),
              Color.GREEN
        );
        
        this.size = 15;
        this.poisonResistance = 0.7; // High poison resistance
    }
    
    @Override
    protected void updateLogic(double deltaTime) {
        super.updateLogic(deltaTime);
        
        // Regeneration logic
        regenTimer += deltaTime;
        if (regenTimer >= REGEN_INTERVAL) {
            regenTimer = 0.0;
            if (currentHp < maxHp && currentHp > 0) {
                currentHp = Math.min(maxHp, currentHp + REGEN_AMOUNT);
            }
        }
    }
}

/**
 * Boss enemy - appears every 10 waves, very strong
 */
class BossEnemy extends Enemy {
    public BossEnemy(double x, double y, int waveNumber) {
        super(x, y,
              200 + (waveNumber * 50), // Very high HP
              25.0, // Slow but unstoppable
              100 + (waveNumber * 10), // High reward
              Color.MAGENTA
        );
        
        this.size = 24; // Much larger
        this.damage = 5; // Deals more damage when reaching end
        
        // Moderate resistance to all damage types
        this.physicalResistance = 0.3;
        this.magicalResistance = 0.3;
        this.fireResistance = 0.3;
        this.iceResistance = 0.3;
        this.poisonResistance = 0.3;
    }
}
