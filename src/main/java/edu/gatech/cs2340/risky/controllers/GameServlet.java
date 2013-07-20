package edu.gatech.cs2340.risky.controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cs2340.risky.Database;
import edu.gatech.cs2340.risky.RiskyServlet;
import edu.gatech.cs2340.risky.database.ModelDb;
import edu.gatech.cs2340.risky.models.Lobby;
import edu.gatech.cs2340.risky.models.Player;

@WebServlet(urlPatterns = {
    "/game",
    "/game/",
    "/game/start",
    "/game/quit"
})
public class GameServlet extends RiskyServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String action = this.getLastUrlSegment(request);
        if ("start".equalsIgnoreCase(action)) {
            startMatch(request, response);
        } else if ("quit".equalsIgnoreCase(action)) {
            quitGame(request, response);
        } else {
            Lobby lobby = Lobby.get(request);
            if (!lobby.isReadyToPlay()) {
                response.sendRedirect("/risky/lobby");
                return;
            }
            
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/game.jsp");
            dispatcher.forward(request, response);
        }
    }

    protected void startMatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Lobby lobby = Lobby.get(request);
        
        ModelDb<Player> playerDb = Player.getDb();
        for (Player candidate : playerDb.query()) {
            if (candidate.playing == false) {System.out.println("a");
                candidate.playing = true;
                lobby.players.add(candidate.id);
            } else {
                System.out.println("a");
            }
        }
        
        if (!lobby.isReadyToPlay()) {
            response.sendRedirect("/risky/lobby");
            return;
        }
        
        lobby.turnOrder.shuffleOrder();// R2
        lobby.allocateArmies();// R3
        
        if (!lobby.isReadyToPlay()) {
            response.sendRedirect("/risky/lobby");
            return;
        }
        
        response.sendRedirect("/risky/game");
    }

    protected void quitGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Database.clear();
        response.sendRedirect("/risky/lobby");
    }

}