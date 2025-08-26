package core;

/**
 * Event types for the tower defense game
 */
public enum EventType {
    ENEMY_SPAWNED,
    ENEMY_KILLED,
    ENEMY_REACHED_END,
    TOWER_PLACED,
    TOWER_UPGRADED,
    TOWER_SOLD,
    WAVE_STARTED,
    WAVE_COMPLETED,
    GAME_OVER,
    PLAYER_HEALTH_CHANGED,
    PLAYER_MONEY_CHANGED,
    PROJECTILE_FIRED,
    PROJECTILE_HIT
}
