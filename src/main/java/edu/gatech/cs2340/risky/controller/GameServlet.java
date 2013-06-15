package edu.gatech.cs2340.risky.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.gatech.cs2340.risky.model.Game;

@WebServlet(urlPatterns = {
        "/game", // GET
        "/game/create", // POST
        "/game/update/*", // PUT
        "/game/delete/*" // DELETE
})
public class GameServlet extends HttpServlet {

    Game game = null;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String operation = (String) request.getParameter("operation");
        if (null == operation) {
            operation = "POST";
        }
        
        if (operation.equalsIgnoreCase("PUT")) {
            doPut(request, response);
            
        } else if (operation.equalsIgnoreCase("DELETE")) {
            doDelete(request, response);
            
        } else {
            int lobbyId = Integer.parseInt(request.getParameter("lobbyId"));
            
            this.game = new Game(lobbyId);
            
            request.setAttribute("game", game);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/game.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Called when HTTP method is GET (e.g., from an <a href="...">...</a>
     * link).
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setAttribute("game", game);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/game.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String name = (String) request.getParameter("name");
        int id = getId(request);
        //game.players.get(id).name = name;
        
        request.setAttribute("game", game);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/game.jsp");
        dispatcher.forward(request, response);
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int id = getId(request);
        this.game = null;
        
        request.setAttribute("game", game);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/game.jsp");
        dispatcher.forward(request, response);
    }

    private int getId(HttpServletRequest request) {
        String uri = request.getPathInfo();
        String idStr = uri.substring(1, uri.length());// Strip off the leading slash, e.g. "/2" becomes "2"
        return Integer.parseInt(idStr);
    }

}
