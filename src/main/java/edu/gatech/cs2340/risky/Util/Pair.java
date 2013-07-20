package edu.gatech.cs2340.risky.Util;

public class Pair {
    public Object peanutButter;
    public Object jelly;
    
    public Pair(Object peanutButter, Object jelly) {
        this.peanutButter = peanutButter;
        this.jelly = jelly;
    }
    
    public boolean equals(Pair p) {
        if (p.peanutButter.equals(this.peanutButter) && p.jelly.equals(this.jelly)) {
            return true;
        } else if (p.peanutButter.equals(this.jelly) && p.jelly.equals(this.peanutButter)) {
            return true;
        }
        return false;
    }
    
}
