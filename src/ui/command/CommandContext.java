package ui.command;

import java.util.HashMap;
import java.util.Map;

public class CommandContext {
    private final Map<String, Object> ContextData;
    
    public CommandContext() {
        this.ContextData = new HashMap<>();
    }

    public void put(String key, Object value) {
        ContextData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) ContextData.get(key);
    }

    public boolean has(String key) {
        return ContextData.containsKey(key);
    }

    public void remove(String key) {
        ContextData.remove(key);
    }

    public void clear() {
        ContextData.clear();
    }
}