package edu.gatech.cs2340.risky.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

public class TurnOrder {
    
    private String[] states = {"setup", "placearmies", "play", "gameover"};

    private String lobbyId;
    public int playerIndex = 0;
    public int round = 0;
    
    public String state = "setup";
    public int action = 0;
    
    public TurnOrder(Object lobbyId) {
        this.lobbyId = lobbyId.toString();
    }
    
    public void shuffleOrder() {
        Lobby lobby = Lobby.get(this.lobbyId);
        if (lobby == null) return;
        Collections.shuffle(lobby.players);
    }
    
    public void nextState() {
        int i=0;
        for ( ; i < this.states.length ; i++) {// find current state
            if (this.state.equalsIgnoreCase(this.states[i])) {
                break;
            }
        }
        i++;// increment it
        if (i >= this.states.length) {
            // yikes
        }
        this.playerIndex = 0;
        this.action = 0;
        this.state = this.states[i];// set it
    }
    
    public int nextAction() throws Exception {
        Lobby lobby = Lobby.get(this.lobbyId);
        if (lobby == null) throw new Exception("No lobby");
        Map map = Map.get(lobby.mapId);
        this.action++;
        
        if (this.state.equals("setup")) {
            this.action = 0;
            if (map.allTerritoriesOccupied()) {
                this.nextState();
            } else {
                this.nextTurn();
            }
            
        } else if (this.state.equals("placearmies")) {
            this.action = 0;
            int sum = 0;
            
            for (Player player : lobby.getPlayers()) {
                sum += player.armiesAvailableThisTurn;
            }
            
            if (sum > 0) {// armies still left to place
                this.nextTurn();
                
            } else {// no armies left
                this.nextState();
            }
            
        } else if (this.state.equals("play")) {
            int winner = lobby.getWinner();
            if (winner >= 0) {
                this.playerIndex = winner;
                this.state = this.states[this.states.length-1];
                this.action = 0;
            }
            this.action %= 4;
            
        }
        
        this.handleActionTransition();
        
        return this.action;
    }
    
    protected void handleActionTransition() {
        Lobby lobby = Lobby.get(this.lobbyId);
        Player player = lobby.getPlayers().get(this.playerIndex);
        if ("setup".equals(this.state)) {
            
        } else if ("placearmies".equals(this.state)) {
            
        } else if ("play".equals(this.state)) {
            switch (this.action) {
            case 0:
                player.armiesAvailableThisTurn += (int) Math.max(3.0, Math.ceil(player.territories.size()/3.0));
                break;
            }
            
        }
    }

    public int nextTurn() throws Exception {
        Lobby lobby = Lobby.get(this.lobbyId);
        if (lobby == null) throw new Exception("No lobby");
        this.playerIndex++;
        if (this.playerIndex >= lobby.players.size()) {
            this.round++;
            this.playerIndex %= lobby.players.size(); 
        }
        return this.playerIndex;
    }
    
    public void automateSetup() throws Exception {
        Lobby lobby = Lobby.get(this.lobbyId);
        if (lobby == null) throw new Exception("No lobby");
        Map map = Map.get(lobby.mapId);
        ArrayList<Player> players = lobby.getPlayers();
        
        for (int i=0 ; i < map.territories.size() ; i++) {
            Player player = players.get(i % players.size());
            Object territoryId = map.territories.get(i).id;
            TerritoryDeed deed = new TerritoryDeed(player.id);
            deed.playerId = player.id;
            map.deeds.put(territoryId, deed);
            player.territories.put(territoryId, deed);
            player.placeArmiesOnTerritory(1, territoryId);
        }
    }
    
    public void automatePlacearmies() throws Exception {
        Lobby lobby = Lobby.get(this.lobbyId);
        if (lobby == null) throw new Exception("No lobby");
        Map map = Map.get(lobby.mapId);
        ArrayList<Player> players = lobby.getPlayers();
        
        for (Player player : players) {
            int available = player.armiesAvailableThisTurn;
            int each = available / player.territories.size();
            int i=0;
            for (Entry<Object, TerritoryDeed> entry : player.territories.entrySet()) {
                Object territoryId = entry.getKey();
                int amount = each;
                if (i == 0) {// account for armies lost due to integer division
                    amount = available - each * player.territories.size();
                }
                player.placeArmiesOnTerritory(amount, territoryId);
                i++;
            }
        }
    }
    
    public static TurnOrder get(HttpServletRequest request) {
        Lobby lobby = Lobby.get(request);
        if (lobby == null) {
            return null;
        }
        return lobby.turnOrder;
    }

}
