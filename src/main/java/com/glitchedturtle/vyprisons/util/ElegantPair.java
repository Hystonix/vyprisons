package com.glitchedturtle.vyprisons.util;

public class ElegantPair {

    public static int pair(int[] pair) {

        int x = pair[0];
        int y = pair[1];

        if(y > x)
            return y * y + x;
        else
            return x * (x + 1) + y;

    }

    public static int[] unpair(int z) {

        int floorRoot = (int)Math.floor(Math.sqrt(z));
        int discrim = z - floorRoot * floorRoot;

        if (discrim < floorRoot) return new int[]{
                discrim,
                floorRoot
        };
        else return new int[] {
                floorRoot,
                discrim - floorRoot
        };

    }

}
