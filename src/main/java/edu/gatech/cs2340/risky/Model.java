package edu.gatech.cs2340.risky;

import java.util.UUID;

public class Model {
    
    public Object id;
    
    public void populateValidWith(Model m) {
        
    }
    
    public static String getRandomId() {
        return UUID.randomUUID().toString();
    }
}
