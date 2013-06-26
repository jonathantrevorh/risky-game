package edu.gatech.cs2340.risky.models;

import java.util.ArrayList;

public class Lobby {
    
    public static final int MAX_PLAYERS = 6;
    
    public String name;
    public ArrayList<Player> players = new ArrayList<Player>();
    
    public Lobby(String name) {
        this.name = name;
    }

    public ArrayList<Player> getPlayers() {
        return players; 
    }

    public String getName() {
        return name;
    }
    
    public boolean hasEnoughPlayers() {
        return this.players.size() > 3;
    }
    
    public boolean hasTooManyPlayers() {
        return this.players.size() > 6;
    }
    
    public int calculateArmies(int numPlayers) {
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
        for (Player player : this.players) {
            player.armies = this.calculateArmies(this.players.size());
        }
    } 
}