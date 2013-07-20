package edu.gatech.cs2340.risky.database;

import java.util.Collection;
import java.util.HashMap;

import edu.gatech.cs2340.risky.Model;

public class HashMapDbImpl<T extends Model> extends ModelDb<T> {

    private HashMap<Object, T> values = new HashMap<Object, T>();

    public T read(Object id) {
        return values.get(id);
    }
    
    public Collection<T> query() {
        return values.values();
    }

    public Object create(T model) {
        Integer newId = values.size();
        values.put(newId, model);
        return newId;
    }

    public T update(T model) {
        return values.put(model.id, model);
    }

    public T delete(Object id) {
        return values.remove(id);
    }
    
    public void empty() {
        this.values = new HashMap<Object, T>();
    }
    
    public <T extends Model> HashMapDbImpl<T> getInstance() {
        return new HashMapDbImpl<T>();
    }
}
