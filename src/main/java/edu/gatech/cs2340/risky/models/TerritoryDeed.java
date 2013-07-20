package edu.gatech.cs2340.risky.models;

import edu.gatech.cs2340.risky.Model;

public class TerritoryDeed extends Model {
    public int armies;
    public Object playerId;
    private static int instanceCount = 0;
    
    public TerritoryDeed(Object playerId) {
        this.id = this.instanceCount++;
        this.playerId = playerId;
    }
}
