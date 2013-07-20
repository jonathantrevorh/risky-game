package edu.gatech.cs2340.risky.controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cs2340.risky.RiskyServlet;

@WebServlet(urlPatterns = {
    "/lobby", // GET
    "/lobby/", // GET
    "/lobby/*" // GET
})
public class LobbyServlet extends RiskyServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/lobby.jsp");
        dispatcher.forward(request, response);
    }

}