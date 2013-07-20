package edu.gatech.cs2340.risky.models;

import javax.servlet.http.HttpServletRequest;

import edu.gatech.cs2340.risky.Model;

public class Territory extends Model {
    public Integer[][] vertexes;
    public Integer[] center;
    public Object[] adjacencies;
    
    public Territory(int id, Integer[][] vertexes, Object[] adjacencies) {
        this.id = id;
        this.vertexes = vertexes;
        this.center = null;
        this.adjacencies = adjacencies;
    }
    
    public Territory(int id, Integer[][] vertexes, Integer[] center, Object[] adjacencies) {
        this.id = id;
        this.vertexes = vertexes;
        this.center = center;
        this.adjacencies = adjacencies;
    }
    
    public static TerritoryDeed get(HttpServletRequest request, Object territoryId) {
        Map map = Map.get(request);
        if (map == null) {
            return null;
        }
        return map.deeds.get(territoryId);
    }
}