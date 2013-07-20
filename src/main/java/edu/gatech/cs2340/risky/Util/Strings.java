package edu.gatech.cs2340.risky.Util;

public class Strings {
    
    public static String capitalize(String s) {
        if (s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    public static String join(String[] parts) {
        StringBuilder sb = new StringBuilder();
        for (int i=0 ; i < parts.length ; i++) {
            sb.append(parts[i]);
        }
        return sb.toString();
    }
    
    public static String dashedToCamel(String s) {
        // from /do/stuff-is-cool to stuffIsCool
        String[] parts = s.split("-");
        for (int i=1 ; i < parts.length ; i++) {
            parts[i] = Strings.capitalize(parts[i]);
        }
        return Strings.join(parts);
    }
    
}
