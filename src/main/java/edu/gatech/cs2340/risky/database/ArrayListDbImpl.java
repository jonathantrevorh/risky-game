package edu.gatech.cs2340.risky.database;

import java.util.ArrayList;

import edu.gatech.cs2340.risky.Model;

public class ArrayListDbImpl<T extends Model> extends ModelDb<T> {

    private ArrayList<T> values = new ArrayList<T>();

    public T read(Object id) {
        for (T item : values) {
            if (item.id.equals(id)) {
                return item;
            }
        }
        return null;
    }
    
    public ArrayList<T> query() {
        return values;
    }

    public Object create(T model) {
        Integer newId = values.size();
        values.add(model);
        return newId;
    }

    public T update(T model) {
        for (int i=0 ; i < values.size() ; i++) {
            if (values.get(i).id.equals(model.id)) {
                T temp = values.get(i);
                values.set(i, model);
                return temp;
            }
        }
        return null;
    }

    public T delete(Object id) {
        for (int i=0 ; i < values.size() ; i++) {
            if (values.get(i).id.equals(id)) {
                T item = values.get(i);
                values.remove(i);
                return item;
            }
        }
        return null;
    }

    public T delete(T model) {
        return delete(model.id);
    }
    
    public void empty() {
        this.values = new ArrayList<T>();
    }
    
    public <T extends Model> ArrayListDbImpl<T> getInstance() {
        return new ArrayListDbImpl<T>();
    }
}
