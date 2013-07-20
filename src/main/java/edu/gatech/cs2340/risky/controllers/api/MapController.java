package edu.gatech.cs2340.risky.controllers.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import edu.gatech.cs2340.risky.ApiServlet;
import edu.gatech.cs2340.risky.api.annotations.ApiParams;
import edu.gatech.cs2340.risky.models.Lobby;
import edu.gatech.cs2340.risky.models.factories.MapFactory;

@WebServlet(urlPatterns = {
    "/api/map"
})
public class MapController extends ApiServlet {

    @Override
    @ApiParams({"request"})
    public Object read(HttpServletRequest request) throws Exception {
        Lobby lobby = Lobby.get(request);
        if (lobby != null) {
            return MapFactory.get(lobby.mapId);
        }
        throw new Exception("No lobby");
    }
    
}
