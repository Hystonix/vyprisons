package com.glitchedturtle.vyprisons.util;

import java.util.Arrays;

public class ElegantPair {

    public static int pair(int x, int y) {

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
