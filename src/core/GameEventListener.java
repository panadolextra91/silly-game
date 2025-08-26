package core;

/**
 * Interface for objects that can listen to game events
 */
public interface GameEventListener {
    void onEvent(GameEvent event);
}
