package edu.gatech.cs2340.risky;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class RiskyServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!this.preDo(request, response)) {
            return;
        }
        
        // delegate on operation parameter for plain ol' html forms
        String operation = (String) request.getParameter("operation");
        
        if (operation != null && operation.equalsIgnoreCase("POST")) {
            doPost(request, response);
            
        } else if (operation != null && operation.equalsIgnoreCase("PUT")) {
            doPut(request, response);
            
        } else if (operation != null && operation.equalsIgnoreCase("DELETE")) {
            doDelete(request, response);
            
        } else {
            super.service(request, response);
        }
    }
    
    protected boolean preDo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // meant to be overridden in subclasses to save from having to override service() and call super.service()
        // good for getting databases and such
        return true;
    }

    public static int getId(HttpServletRequest request) throws Exception {
        String uri = request.getPathInfo();
        System.out.println(uri);
        if (uri == null || "".equals(uri)) {
            throw new Exception("Invalid getId");
        }
        String idStr = uri.substring(1).split("/")[0];// Strip off the leading slash, e.g. "/2/what" becomes "2"
        return Integer.parseInt(idStr);
    }
    
    public static String getSessionId(HttpServletRequest request) {
        // seem silly when the following line could just be used everywhere this method is used
        // but if/when it comes time to move to many-session-to-one-lobby, migration *should* be easy
        return request.getSession().getId();
    }
    
    protected String getLastPathSegment(HttpServletRequest request) {
        String[] segments = this.getPathSegments(request);
        if (segments.length == 0) {
            return null;
        }
        return segments[segments.length-1];
    }
    
    protected String getPathSegment(HttpServletRequest request, int index) {
        String[] actions = getPathSegments(request);
        if (index < actions.length) {
            return actions[index];
        }
        return null;// watch out! null if outside range
    }
    
    protected String[] getPathSegments(HttpServletRequest request) {
        // pathInfo does not include the app context (risky), or servlet context (maybe /api/player)
        // so this.game.is.gre.at/risky/api/player/0/attack
        // would have this method return {"0", "attack"}
        String pathInfo = request.getPathInfo();
        System.out.println(pathInfo + " <- path info");
        System.out.println(request.getServletPath() + " <- path");
        System.out.println(request.getQueryString() + " <- query string");
        System.out.println(request.getRequestURI() + " <- uri");
        if (pathInfo == null) {            
            return new String[0];
        }
        String[] segments = pathInfo.substring(1).split("\\/");
        for (String s : segments) { System.out.print(s + " "); } System.out.println(" <- segments");
        return segments;
    }
    
    protected String getLastUrlSegment(HttpServletRequest request) {
        String[] segments = this.getUrlSegments(request);
        if (segments.length == 0) {
            return null;
        }
        return segments[segments.length-1];
    }
    
    protected String[] getUrlSegments(HttpServletRequest request) {
        String path = request.getServletPath();
        if (path == null) {
            return new String[0];
        }
        return path.substring(1).split("/");
    }
    
    
    
}
