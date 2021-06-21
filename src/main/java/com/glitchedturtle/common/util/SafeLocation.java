package com.glitchedturtle.common.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class SafeLocation {

    private double _x;
    private double _y;
    private double _z;

    private double _pitch = 0;
    private double _yaw = 0;

    public SafeLocation(double x, double y, double z, double pitch, double yaw) {
        _x = x;
        _y = y;
        _z = z;
        _pitch = pitch;
        _yaw = yaw;
    }

    public SafeLocation(double x, double y, double z) {
        _x = x;
        _y = y;
        _z = z;
    }

    public SafeLocation(Location loc) {
        this(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
    }

    public Location toLocation(World world) {
        return new Location(world, _x, _y, _z, (float) _pitch, (float) _yaw);
    }

    public Vector toVector() {
        return new Vector(_x, _y, _z);
    }

    @Override
    public String toString() {
        return "SafeLocation(" + _x + "," + _y + "," + _z + ")";
    }

}
