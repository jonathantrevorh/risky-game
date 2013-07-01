package edu.gatech.cs2340.risky.database;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class HashMapDbImpl<T> implements ModelDb<T> {

    private HashMap<Object, T> values = new HashMap<Object, T>();

    public T get(Object id) {
        return values.get(id);
    }
    
    public Collection<T> query() {
        return values.values();
    }

    public Object create(T value) {
        Integer newId = values.size();
        values.put(newId, value);
        return newId;
    }

    public T update(Object id, T value) {
        return values.put(id, value);
    }

    public T delete(Object id) {
        return values.remove(id);
    }
}
