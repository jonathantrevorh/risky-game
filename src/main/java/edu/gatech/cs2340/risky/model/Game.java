package edu.gatech.cs2340.risky.model;

import java.util.ArrayList;

public class Game {
    private ArrayList<Player> players = new ArrayList<Player>();
    private TurnManager turnManager = new TurnManager();
    private int playerNum;
    private int territories=23;//todo, get this number from lobby.html with a post request
    //i've had issues with angular overwriting my input field

    public Game() {

    }

    public int players() {
        return playerNum;
    }

    public Player getNextPlayer() {
        return turnManager.getNextPlayer();
    }

    public void setTerritoryNum(int territories){
        this.territories=territories;
    }

    public void addPlayer(Player p) {
        players.add(p);
        turnManager.addPlayer(p); 
        playerNum++;
    }

    public boolean hasPlayers(){
        return (players.size()>0);
    }

    public int numofTerritories() {
        return territories;
    }

    private int calculateArmies(int numPlayers) {
        switch (numPlayers) {
        case 3:
            return 35;
        case 4:
            return 30;
        case 5:
            return 25;
        case 6:
            return 20;
        }
        return 0;
    }

    public void allocateArmies() {
        int territoriesPerPlayer=territories/players.size();
        int armiesPerTerritory=territoriesPerPlayer/calculateArmies(playerNum);
        int extraArmies=territoriesPerPlayer%calculateArmies(playerNum);
        
        int territoryIterator=0;
        for (Player player : players) {
            for(int i=0; i<territoriesPerPlayer; ++i) {
                if(i==0) {
                    player.setArmiesPerTerritory(territoryIterator++, armiesPerTerritory+extraArmies);
                    continue;
                }
                player.setArmiesPerTerritory(territoryIterator++, armiesPerTerritory);
            }
        }
        //lucky player... todo fix?
        while(players.get(0)!=null && territoryIterator<=territories){
            players.get(0).setArmiesPerTerritory(territoryIterator++, armiesPerTerritory);
        }
    } 
    
    public String getPlayerOrder() {
        return turnManager.JSONplayerOrder();
    }
    
    public String getTerritoryArmyFromPlayers() {
        ArrayList<Player> orderedPlayers=turnManager.playerOrder();
        String playerData=new String();
        for(Player p : orderedPlayers){
            playerData=playerData.concat(", {\"name\" : \""+p.name()+"\",\"territories\" : "+p.JSON_TerritoryArmies()+"}");
        }
        playerData=playerData.substring(1,playerData.length());
        return "{\"players\" : ["+playerData+"]}";
    }


    public void calculateTurnOrder(){
        turnManager.shuffleOrder();
    }
}
