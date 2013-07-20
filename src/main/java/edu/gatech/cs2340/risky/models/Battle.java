package edu.gatech.cs2340.risky.models;

import edu.gatech.cs2340.risky.Model;

public class Battle extends Model {
    
    private String attackingTerritory;
    private int attackingDie;
    private String defendingTerritory;
    private int defendingDie;
    
    public Battle() {
        
    }
    
    public Battle(String fromTerritory, String toTerritory, int attackingDie, int defendingDie) {
        this.attackingTerritory = fromTerritory;
        this.defendingTerritory = toTerritory;
        this.attackingDie = attackingDie;
        this.defendingDie = defendingDie;
    }
    
    public boolean isReadyToWage() {
        return this.attackingTerritory != null && this.attackingDie > 0 && this.defendingTerritory != null && this.defendingDie > 0;
    }
    
    public void wage() throws Exception {
        if (!this.isReadyToWage()) {
            throw new Exception("Yowzers, battle not ready to wage");
        }
        
        // TODO: implement logic of an attack as defined in R15
        // more specifically, under Rules of Risk -> Gameplay -> Attacking in 
        // http://www.cc.gatech.edu/~simpkins/teaching/gatech/cs2340/projects/cs2340-summer2013-project.html
        
        BattleRecord record = new BattleRecord();
        
        record.attackingTerritory = this.attackingTerritory;
        record.defendingTerritory = this.defendingTerritory;
        // record.loser = this.defendingTerritory.getOwner() ? doesn't exist yet, need a reverse lookup on a playerId field in Territory
        // record.winner = this.attackingTerritory.getOwner()
        record.winnerCasualties = 1;
        record.loserCasualties = 2;
        
    }
    
}
