package core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event-driven architecture implementation
 * Manages game events and notifications using Observer pattern
 */
public class GameEventManager {
    private Map<EventType, List<GameEventListener>> listeners;
    private Queue<GameEvent> eventQueue;
    
    public GameEventManager() {
        this.listeners = new ConcurrentHashMap<>();
        this.eventQueue = new ArrayDeque<>();
        
        // Initialize all event types
        for (EventType type : EventType.values()) {
            listeners.put(type, new CopyOnWriteArrayList<>());
        }
    }
    
    /**
     * Register a listener for specific event type
     */
    public void addListener(EventType eventType, GameEventListener listener) {
        listeners.get(eventType).add(listener);
    }
    
    /**
     * Remove a listener
     */
    public void removeListener(EventType eventType, GameEventListener listener) {
        listeners.get(eventType).remove(listener);
    }
    
    /**
     * Fire event immediately
     */
    public void fireEvent(GameEvent event) {
        List<GameEventListener> eventListeners = listeners.get(event.getType());
        for (GameEventListener listener : eventListeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                System.err.println("Error in event listener: " + e.getMessage());
            }
        }
    }
    
    /**
     * Queue event for later processing
     */
    public void queueEvent(GameEvent event) {
        eventQueue.offer(event);
    }
    
    /**
     * Process all queued events
     */
    public void processQueuedEvents() {
        while (!eventQueue.isEmpty()) {
            GameEvent event = eventQueue.poll();
            fireEvent(event);
        }
    }
    
    /**
     * Clear all listeners and events
     */
    public void clear() {
        listeners.values().forEach(List::clear);
        eventQueue.clear();
    }
}


