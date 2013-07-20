package edu.gatech.cs2340.risky.database;

import java.io.Serializable;
import java.util.Collection;

import edu.gatech.cs2340.risky.Model;

public abstract class ModelDb<T extends Model> implements Serializable  {

    public abstract T read(Object id);

    public abstract Collection<T> query();

    public abstract Object create(T model);

    public abstract T update(T model);

    public abstract T delete(Object id);

    public T delete(T model) {
        return delete(model.id);
    }
    
    public abstract void empty();
    
    public abstract <T extends Model> ModelDb<T> getInstance();
}
