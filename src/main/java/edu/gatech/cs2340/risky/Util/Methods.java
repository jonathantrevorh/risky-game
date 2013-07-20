package edu.gatech.cs2340.risky.Util;

import java.util.Map;

import edu.gatech.cs2340.risky.api.annotations.ApiParams;

public class Methods {
    
    public static int getArgumentDifference(Map<String, Object> availableArguments, String[] params) {
        int difference = 0;
        
        if (params == null) {
            return Integer.MAX_VALUE;
        }
        
        for (String param : params) {
            if (!availableArguments.containsKey(param)) {
                difference++;
            }
        }
        return difference;
    }

    public static Object[] mapArguments(Map<String, Object> arguments, String[] argumentNames) {
        Object[] args = new Object[argumentNames.length];
        
        for (int i=0 ; i < args.length ; i++) {
            if (i > argumentNames.length) {
                args[i] = null;
            } else {
                args[i] = arguments.get(argumentNames[i]);
            }
        }
        
        return args;
    }
    
}