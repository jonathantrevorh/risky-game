package edu.gatech.cs2340.risky.controllers.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import edu.gatech.cs2340.risky.ApiServlet;
import edu.gatech.cs2340.risky.api.annotations.ApiParams;
import edu.gatech.cs2340.risky.models.Lobby;
import edu.gatech.cs2340.risky.models.TurnOrder;

@WebServlet(urlPatterns = {
    "/api/turnOrder",
    "/api/turnOrder/*"
})
public class TurnOrderController extends ApiServlet {
    
    @Override
    @ApiParams({"request"})
    public Object read(HttpServletRequest request) throws Exception {
        Lobby lobby = Lobby.get(request);
        if (lobby == null) {
            throw new Exception("No lobby to use");
        }
        
        return lobby.turnOrder;
    }
    
    @ApiParams({"request"})
    public Object nextAction(HttpServletRequest request) throws Exception {
        TurnOrder order = TurnOrder.get(request);
        order.nextAction();
        return order;
    }
    
    @ApiParams({"request"})
    public Object nextTurn(HttpServletRequest request) throws Exception {
        TurnOrder order = TurnOrder.get(request);
        order.nextTurn();
        return order;
    }
    
    @ApiParams({"request"})
    public Object automateSetup(HttpServletRequest request) throws Exception {
        TurnOrder order = TurnOrder.get(request);
        order.automateSetup();
        return order;
    }
    
    @ApiParams({"request"})
    public Object automatePlacearmies(HttpServletRequest request) throws Exception {
        TurnOrder order = TurnOrder.get(request);
        order.automatePlacearmies();
        return order;
    }
    
}
