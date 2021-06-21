package com.glitchedturtle.common.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.function.Function;

public class StringParser {

    public static Vector parseBlockVector(String serialized) {

        String[] split = serialized.split(",");
        return new Vector(Integer.parseInt(split[0]),
                Integer.parseInt(split[1]),
                Integer.parseInt(split[2]));

    }

    public static SafeLocation parsePosition(String position) {

        String[] split = position.split(",");
        if(split.length == 3)
            return new SafeLocation(Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2]));
        if(split.length == 5)
            return new SafeLocation(Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2]),
                    Double.parseDouble(split[3]),
                    Double.parseDouble(split[4]));

        return null;

    }

    public static String locationToString(Vector vec) {
        return vec.getX() + "," + vec.getY() + "," + vec.getZ();
    }

    public static String locationToString(Location vec) {
        return vec.getX() + "," + vec.getY() + "," + vec.getZ() + "," + vec.getPitch() + "," + vec.getYaw();
    }

    public static Function<String, String> chatColorMapper(char altCode) {
        return (String str) -> ChatColor.translateAlternateColorCodes(altCode, str);
    }

}
