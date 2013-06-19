package edu.gatech.cs2340.risky.model;

public class Player {
    
    public String name;
    public int armies;
    private boolean playing=true;

    public Player(String name, int armies) {
        this.name = name;
        this.armies = armies;
    }
    
    public Player(String name) {
        this(name, 0);
    }
    
    public void setDead() {
        playing=false;
    }

    public boolean stillAlive() {
        return playing;
    }
}
