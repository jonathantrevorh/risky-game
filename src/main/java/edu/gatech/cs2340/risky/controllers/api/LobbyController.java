package edu.gatech.cs2340.risky.controllers.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import edu.gatech.cs2340.risky.ApiServlet;
import edu.gatech.cs2340.risky.Database;
import edu.gatech.cs2340.risky.api.annotations.ApiParams;
import edu.gatech.cs2340.risky.models.Lobby;
import edu.gatech.cs2340.risky.models.Player;

@WebServlet(urlPatterns = {
    "/api/lobby",
    "/api/lobby/*"
})
public class LobbyController extends ApiServlet {

    @Override
    @ApiParams({"request"})
    public Object create(HttpServletRequest request) throws Exception {
        throw new Exception("Lobby must be created through /risky/lobby");
    }

    @Override
    @ApiParams({"request"})
    public Object read(HttpServletRequest request) {
        Lobby lobby = Lobby.get(request);
        return lobby;
    }

    @Override
    @ApiParams({"request"})
    public Object update(HttpServletRequest request) throws Exception {
        Lobby lobby = Lobby.get(request);
        Lobby givenLobby = (Lobby) getPayloadObject(request, Player.class);
        
        try {
            lobby.populateValidWith(givenLobby);
            return lobby;
        } catch (Exception e) {
            throw new Exception("Failed to update lobby");
        }
    }

    @Override
    @ApiParams({"request"})
    public Object delete(HttpServletRequest request) {
        Lobby lobby = Lobby.get(request);
        for (Player player : lobby.getPlayers()) {
            player.playing = false;// release players from lobby
        }
        return Database.delete(lobby);
    }
    
    @ApiParams({"request"})
    public Object automateTerritorySelection(HttpServletRequest request) throws Exception {
        Lobby lobby = Lobby.get(request);
        try {
            lobby.assignTerritories();
            return lobby;
        } catch (Exception e) {
            throw new Exception("No player territory automation");
        }
    }
    
}
