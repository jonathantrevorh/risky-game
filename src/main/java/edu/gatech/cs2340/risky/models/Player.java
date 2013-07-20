package edu.gatech.cs2340.risky.models;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import edu.gatech.cs2340.risky.Database;
import edu.gatech.cs2340.risky.Model;
import edu.gatech.cs2340.risky.RiskyServlet;
import edu.gatech.cs2340.risky.database.ArrayListDbImpl;
import edu.gatech.cs2340.risky.database.ModelDb;

public class Player extends Model implements Comparable {
    
    private static String[] colors = {"#cc7719", "#e0d21b", "#841f87", "#235adb", "#cc2819", "#878787"};
    private static int instanceCount = 0;
    
    public String name;
    public int armies;
    public int armiesAvailableThisTurn;
    public String color;// e.g. #00FF00
    public boolean playing;
    public HashMap<Object, TerritoryDeed> territories;
    
    public Player() {
        this(null);
    }
    
    public Player(String name) {
        this(name, 0);
    }
    
    public Player(String name, int armies) {
        this.name = name;
        this.armies = armies;
        this.armiesAvailableThisTurn = this.armies;
        this.playing = false;
        this.id = instanceCount;
        this.color = Player.colors[instanceCount%colors.length];
        this.territories = new HashMap<Object, TerritoryDeed>();
        instanceCount++;
    }
    
    public void allocateArmies(int number) {
        this.armies += number;
        this.armiesAvailableThisTurn += number;
        System.out.println("giving " + number + " to player");
    }
    
    public int placeArmiesOnTerritory(int number, Object territoryId) {
        number = Math.min(number, this.armiesAvailableThisTurn);
        territories.get(territoryId).armies += number;
        this.armiesAvailableThisTurn -= number;
        System.out.println("player " + this.id + " giving " + number + " to " + territoryId);
        return number;// number of armies actually attacked with
    }
    
    public BattleRecord attack(String attackingTerritory, String defendingTerritory, int attackingDie, int defendingDie) throws Exception {
        Battle worldWarJava = new Battle(attackingTerritory, defendingTerritory, attackingDie, defendingDie);
        return worldWarJava.wage();
    }
    
    public void gainTerritory(Object territoryId, TerritoryDeed deed) {
        this.territories.put(territoryId, deed);
        // maybe return number of territories owned now?
        // or all the owned territories
    }
    
    public TerritoryDeed loseTerritory(Object territoryId) {
        return this.territories.remove(territoryId);
    }
    
    public Integer fortifyTerritory(TerritoryDeed from, TerritoryDeed to, int number) {
        if (from == null) {
            number = Math.min(number, this.armiesAvailableThisTurn);
            this.armiesAvailableThisTurn -= number;
            
        } else {
            number = Math.min(number, from.armies-1);
            from.armies -= number;
        }
        
        to.armies += number;
        return number;
    }
    
    public void setDead() {
        playing = false;
    }

    public boolean stillAlive() {
        return playing;
    }
    
    public void populateValidWith(Player p, boolean ignoreInvalidValues) {
        try {
            this.name = p.name;// should be a loop over matching the fields
            this.armies = p.armies;
            this.playing = p.playing;
            
        } catch (Exception e) {
            if (!ignoreInvalidValues) {
                throw e;
            }
        }
    }
    
    public static Player get(HttpServletRequest request) {
        return Player.get(request, null);
    }
    
    public static Player get(HttpServletRequest request, Integer id) {
        ModelDb<Player> playerDb = Database.getDb(Player.class, new ArrayListDbImpl<Player>());
        if (id == null) {
            try {
                id = RiskyServlet.getId(request);
            } catch (Exception e) {
                
            }
        }
        return playerDb.read(id);
    }
    
    public static Player get(Integer id) {
        ModelDb<Player> playerDb = Database.getDb(Player.class, new ArrayListDbImpl<Player>());
        return playerDb.read(id);
    }
    
    public static ModelDb<Player> getDb() {
        return Database.getDb(Player.class, new ArrayListDbImpl<Player>());
    }

    @Override
    public int compareTo(Object arg0) {
        return (Integer)this.id - (Integer)((Player)arg0).id;
    }
}
