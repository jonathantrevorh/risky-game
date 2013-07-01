package edu.gatech.cs2340.risky.model;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Player {
    private String name;
    private int armies=0;
    private boolean playing = true;
    private HashMap<Integer,Integer> delegatedArmies=new HashMap<Integer,Integer>();


    public Player(String name, int armies) {
        this.name = name;
        this.armies = armies;
    }
     
    public Player(String name) {
        this(name, 0);
    }
    
    public int totalArmies(){
        int totalArmies=0;
        ArrayList<Integer> armyCount=(ArrayList<Integer>)delegatedArmies.values();
        for(Integer army : armyCount){
            totalArmies+=army;
        }
        return totalArmies;
    }

    public int armiesAtTerritory(int territory){
        return (delegatedArmies.get(territory)==null)?0:delegatedArmies.get(territory);
    }
    public void setArmiesPerTerritory(int territory, int armies){
        delegatedArmies.put(territory,armies);
    }

    public void setDead() {
        playing = false;
    }

    public boolean stillAlive() {
        return playing;
    }
    
    public int armies(){
        return armies;
    }
    
    public String name(){
        return name;
    }
    
    //JSON compatible territory-army pairs
    //{"territories":[{"id": terrId,"armies": armyNum}, ...]}
    public String JSON_TerritoryArmies() {
        String terrArmyPairs=new String();
        for(Map.Entry<Integer,Integer> entry : delegatedArmies.entrySet()) {
            terrArmyPairs=terrArmyPairs.concat(",{\"id\":"+entry.getKey()+",\"armies\":"+entry.getValue()+"}");
        }
        if(terrArmyPairs.length()>1){
            terrArmyPairs=terrArmyPairs.substring(1,terrArmyPairs.length());//remove first comma
        }
        return ("["+terrArmyPairs+"]");
    }
}
