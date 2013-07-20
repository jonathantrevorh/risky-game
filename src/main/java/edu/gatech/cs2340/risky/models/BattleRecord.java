package edu.gatech.cs2340.risky.models;

import edu.gatech.cs2340.risky.Model;

public class BattleRecord extends Model {
    
    public Object winner;
    public Object attackingTerritory;
    public Object loser;
    public Object defendingTerritory;
    public int winnerCasualties;
    public int loserCasualties;
    
    
    public BattleRecord() {
        
    }
}
