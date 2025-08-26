package core;

import java.util.HashMap;
import java.util.Map;

/**
 * Base game event class
 */
public abstract class GameEvent {
    protected EventType type;
    protected long timestamp;
    protected Map<String, Object> data;
    
    public GameEvent(EventType type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.data = new HashMap<>();
    }
    
    public EventType getType() {
        return type;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setData(String key, Object value) {
        data.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) data.get(key);
    }
}
