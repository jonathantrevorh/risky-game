package edu.gatech.cs2340.risky.Util;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Objects {
    
    public static Method[] getMethodsByName(Class c, String name) {
        System.out.println("getMethodsByName");
        System.out.println(c.getName());
        System.out.println(name);
        Method[] candidates = c.getDeclaredMethods();
        ArrayList<Method> methods = new ArrayList<Method>();
        for (Method candidate : candidates) {
            System.out.println("\t" + candidate.getName());
            if (candidate.getName().equals(name)) {
                methods.add(candidate);
            }
        }
        
        return methods.toArray(new Method[0]);
    }
    
}
