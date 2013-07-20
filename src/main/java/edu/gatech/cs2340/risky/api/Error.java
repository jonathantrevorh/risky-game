package edu.gatech.cs2340.risky.api;

import javax.servlet.http.HttpServletResponse;

public class Error extends java.lang.Error {
    
    public String error = "";
    public int code = 500;
    public Object culprit = new Object();
    
    public Error(String error, Object culprit) {
        this(error, culprit, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    
    public Error(String error, Object culprit, int code) {
        this.error = error;
        this.culprit = culprit;
        this.code = code;
    }
}
