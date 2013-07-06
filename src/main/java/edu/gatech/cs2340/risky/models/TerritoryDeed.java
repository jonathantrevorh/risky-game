package edu.gatech.cs2340.risky.models;

import edu.gatech.cs2340.risky.Model;

public class TerritoryDeed extends Model {
    public int armies;
    public Player player;
    
    public TerritoryDeed(Player player) {
        this.player = player;
    }
}
