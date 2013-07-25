package edu.gatech.cs2340.risky.controllers.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import edu.gatech.cs2340.risky.ApiServlet;
import edu.gatech.cs2340.risky.api.annotations.ApiParams;
import edu.gatech.cs2340.risky.database.ModelDb;
import edu.gatech.cs2340.risky.models.BattleRecord;
import edu.gatech.cs2340.risky.models.Lobby;
import edu.gatech.cs2340.risky.models.Map;
import edu.gatech.cs2340.risky.models.Player;
import edu.gatech.cs2340.risky.models.TerritoryDeed;

@WebServlet(urlPatterns = {
    "/api/player",
    "/api/player/*"
})
public class PlayerController extends ApiServlet {

    @Override
    @ApiParams({"request"})
    public synchronized Object create(HttpServletRequest request) throws Exception {// R1
        ModelDb<Player> playerDb = Player.getDb();
        Player player = (Player) getPayloadObject(request, Player.class);
        
        Collection<Player> players = playerDb.query();
        if (players.size() >= 6) {
            throw new Exception("Too many players");
        }
        
        for (Player opponent : players) {
            if (player.name.equalsIgnoreCase(opponent.name)) {
                throw new Exception("Invalid player: Cannot have same name");
            }
        }
        
        players.add(player);
        return player;
    }

    @Override
    @ApiParams({"request"})
    public Object read(HttpServletRequest request) {
        ModelDb<Player> playerDb = Player.getDb();
        
        try {
            Integer playerId = getId(request);
            return playerDb.read(playerId);
            
        } catch (Exception e) {
            Collection<Player> results = playerDb.query();
            results = this.filterResults(results, (String) request.getParameter("filter"), (String) request.getParameter("arg"));
            return results;
        }
    }

    @Override
    @ApiParams({"request"})
    public synchronized Object update(HttpServletRequest request) throws Exception {
        Player givenPlayer = (Player) getPayloadObject(request, Player.class);
        Player player = Player.get(request);
        
        try {
            player.populateValidWith(givenPlayer);
            return player;
            
        } catch (Exception e) {
            throw new Exception("Failed to update player");
        }
    }

    @Override
    @ApiParams({"request"})
    public synchronized Object delete(HttpServletRequest request) throws Exception {
        ModelDb<Player> playerDb = Player.getDb();
        int playerId = getId(request);
        Player player = playerDb.delete(playerId);
        return player;
    }
    
    @ApiParams({"request", "attacking", "defending", "attackingDie", "defendingDie"})
    public Object attack(HttpServletRequest request, String attacking, String defending, String attackingDie, String defendingDie) throws Exception {
        Player player = Player.get(request);
        BattleRecord record = player.attack(attacking, defending, Integer.parseInt(attackingDie), Integer.parseInt(defendingDie));

        Lobby lobby = Lobby.get(request);
        Map map = Map.get(lobby.mapId);
        TerritoryDeed attacker = map.deeds.get(record.attackingTerritory);
        TerritoryDeed attackee = map.deeds.get(record.defendingTerritory);
        
        System.out.println("ASDFKSADJFLKSAJDFLKSADJFLKJSADFKLJASDFKLJSADFKLJASDFLKJSADFKLSJADFLKJASDFLKJ");
        System.out.println(record.attackingTerritory);
        System.out.println(record.defendingTerritory);
        System.out.println(attacker);
        System.out.println(attacker.playerId);
        System.out.println(attackee);
        System.out.println(attackee.playerId);
        System.out.println(attackee.playerId.toString());
        System.out.println(Integer.parseInt(attackee.playerId.toString()));
        System.out.println(Player.get(Integer.parseInt(attackee.playerId.toString())));
        System.out.println(Player.get(request, Integer.parseInt(attackee.playerId.toString())));
        System.out.println("ASDFKSADJFLKSAJDFLKSADJFLKJSADFKLJASDFKLJSADFKLJASDFLKJSADFKLSJADFLKJASDFLKJ");
        
        attacker.armies -= record.attackingCasualties;
        attackee.armies -= record.defendingCasualties;
        
        if (attacker.armies <= 0) {
            // give attacker to defending player
            System.out.println("here");
            this.transferTerritory(request, Player.get(request, Integer.parseInt(attackee.playerId.toString())), attacking);
            
        } else if (attackee.armies <= 0) {
            // give attackee to attacking player
            System.out.println("there");
            this.transferTerritory(request, player, defending);
        }
        
        return player;
    }
    
    @ApiParams({"request", "territory"})
    public Object seizeTerritory(HttpServletRequest request, String territory) throws Exception {
        Lobby lobby = Lobby.get(request);
        Map map = Map.get(lobby.mapId);
        TerritoryDeed deed = map.deeds.get(territory);
        Player player = Player.get(request);
        
        if (player == null) {
            throw new Exception("No such player");
        }

        /*System.out.println("player: " + player.id);
        if (deed == null) {
            System.out.println("null deed");
        } else {
            if (deed.playerId == null) {
                System.out.println("null deed.playerId");
            } else {
                System.out.println("owner: " + deed.playerId);
                System.out.println(deed.playerId.equals(player.id)? "equals" : "not equals");
                System.out.println(deed.playerId.getClass().getSimpleName());
                System.out.println(player.id.getClass().getSimpleName());
            }
        }*/
        
        if (deed == null || deed.playerId == null) {// territory not yet owned, or owned by player 
            if (deed == null) {
                deed = new TerritoryDeed(player.id);
            }
            deed.playerId = player.id;
            map.deeds.put(territory, deed);
            player.territories.put(territory, deed);
            player.placeArmiesOnTerritory(1, territory);
            return player;
            
        } else if (deed.playerId.equals(player.id)) {
            throw new Exception("You already own this territory");
            
        } else if (deed.armies == 0) {// after a battle, when the territory is being lost
            this.transferTerritory(request, player, territory);
            return player;
            
        }
        
        throw new Exception("Could not seize territory");
    }
    
    private void transferTerritory(HttpServletRequest request, Player player, String territory) {
        Lobby lobby = Lobby.get(request);
        Map map = Map.get(lobby.mapId);
        TerritoryDeed deed = map.deeds.get(territory);
        Player opponent = Player.get(request, (Integer) deed.playerId);
        if (opponent == null) {
            throw new Error("No opponent to take from");
        }
        Integer key = Integer.parseInt(territory);
        
        System.out.print("Key: ");
        System.out.println(key == null ? "null territory" : (key.getClass().getSimpleName() + ":" + key));
        for (Entry<Object, TerritoryDeed> entry : opponent.territories.entrySet()) {
            System.out.print(entry.getKey().getClass().getSimpleName() + ":" + entry.getKey() + ": ");
            System.out.println(entry.getValue());
        }
        deed = opponent.territories.remove(key);
        System.out.println("taking from " + opponent.name);

        System.out.println(deed == null ? "null deed" : deed);
        
        deed.playerId = player.id;
        deed.armies = 1;

        System.out.println("giving to " + player.name);
        player.territories.put(key, deed);
    }
    
    @ApiParams({"request", "from", "to", "armies"})
    public Object fortifyTerritory(HttpServletRequest request, String from, String to, String armies) throws Exception {
        Player player = Player.get(request);
        Map map = Map.get(request);
        TerritoryDeed fromDeed = map.deeds.get(from);
        TerritoryDeed toDeed = map.deeds.get(to);
        
        System.out.println(to);
        System.out.println(to.getClass().getName());
        
        for (Entry<String, TerritoryDeed> deed : map.deeds.entrySet()) {
            System.out.println(deed.getKey() + " " + deed.getValue().playerId);
        }
        
        if (!toDeed.playerId.equals(player.id)) {
            throw new Exception("Not your territory");
        }
        
        int number = Integer.parseInt(armies);
        player.fortifyTerritory(fromDeed, toDeed, number);
        return player;
    }
    
    //regular update didn't work for some reason
    @ApiParams({"request", "armies"})
    public Object updateArmies(HttpServletRequest request, String armies) throws Exception {
        Player player = Player.get(request);
        player.armies=Integer.parseInt(armies);
        if(player.armies<1){
            player.playing=false;
        }
        return player;
    }

    protected synchronized Collection<Player> filterResults(Collection<Player> players, String filter, String arg) {
        if (filter == null) {
            return players;
        }
        ArrayList<Player> filteredResult = new ArrayList<Player>();
        if ("isNotPlaying".equals(filter)) {
            for (Player player : players) {
                if (player.playing == false) {
                    filteredResult.add(player);
                }
            }
        } else if ("inLobby".equals(filter)) {
            Lobby lobby = Lobby.get(arg);
            if (lobby != null) {
               filteredResult = lobby.getPlayers();
            }
        }
        
        return filteredResult;
    }
    
}
