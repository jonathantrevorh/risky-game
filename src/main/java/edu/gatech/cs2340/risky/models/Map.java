package edu.gatech.cs2340.risky.models;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import edu.gatech.cs2340.risky.Database;
import edu.gatech.cs2340.risky.Model;
import edu.gatech.cs2340.risky.Util.Pair;
import edu.gatech.cs2340.risky.database.HashMapDbImpl;

public class Map extends Model {
    public ArrayList<Territory> territories;
    public HashMap<String, TerritoryDeed> deeds;
    
    public Map() {
        this.territories = new ArrayList<Territory>();
        this.deeds = new HashMap<String, TerritoryDeed>();
    }
    
    public void addTerritory(Territory t) {
        territories.add(t);
    }
    
    public boolean allTerritoriesOccupied() {
        return this.deeds.keySet().size() == this.territories.size();
    }
    
    public boolean territoriesAreAdjacent(Object t1, Object t2) {
        // find both territories and ask them both whether they're adjacent, return the OR of their answers
        return false;
    }
    
    public static Map get(HttpServletRequest request) {
        Lobby lobby = Lobby.get(request);
        return Map.get(lobby.mapId);
    }
    
    public static Map get(Object mapId) {
        return Database.getModel(Map.class, mapId, new HashMapDbImpl<Map>());
    }
}
