package edu.gatech.cs2340.risky.model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class that will act as a reference
 * to current players and their turns
 */
public class TurnManager {
    private static final int MAX_PLAYERS = 6;

    private ArrayList<Player> turnList;
    private int playerIndex = 0;
    private int round = 0;
    
    public TurnManager() {
        turnList = new ArrayList<Player>(MAX_PLAYERS);
    }

    public boolean addPlayer(Player p) {
        if (round == 0) {
            if (turnList.size() <= MAX_PLAYERS) {
                turnList.add(p);
                return true;
            }
        }
        return false;
    }
    
    public void shuffleOrder() {
        Collections.shuffle(turnList);    
    }

    public int getRound() {
        return round;
    }
    
    private Player nextPlayer() {
        if (playerIndex < turnList.size()) {
            return turnList.get(playerIndex++);
        } else {
            playerIndex = 0;
            round++;
            return turnList.get(playerIndex++);
        }   
    }
    
    //this method will only work if there is atleast 1 living player
    //round will be updated if next/first player is returned
    //@dare-- do it with iterators...
    public Player getNextPlayer() {
        if(round == 0) {
            round++;//ensures players are only added on game start
        }
    
        Player p = nextPlayer();
        while (!p.stillAlive()) {
            p = nextPlayer();
            if (round > 9001){
                break;//just in case
            }
        }
        return p;
    }   

    public ArrayList<Player> playerOrder(){
        ArrayList<Player> lineup=new ArrayList<Player>();
        for (Player p : turnList) {
            lineup.add(p); 
        }
        return lineup;
    }

    //JSON compatible player list
    public String JSONplayerOrder() {
        String order = new String();
        for (Player p : turnList){
            order = order.concat(", {\"name\" : \""+p.name()+"\"}");
        }
        if(order.length()>1){
            order=order.substring(1,order.length());//remove first comma
        }
        return order;
    }

}
