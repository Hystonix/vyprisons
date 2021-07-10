package com.glitchedturtle.common.region;

import com.glitchedturtle.common.util.SafeLocation;
import com.glitchedturtle.common.util.StringParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Region {

    private String _worldName;

    private Vector _min;
    private Vector _max;

    public Region(Vector a, Vector b) {

        _worldName = null;

        _min = new Vector(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ())
        );

        _max = new Vector(
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ())
        );

    }

    public Region(Location a, Location b) {

        _worldName = a.getWorld().getName();

        _min = new Vector(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ())
        );

        _max = new Vector(
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ())
        );

    }

    public World getWorld() {
        return Bukkit.getWorld(_worldName);
    }

    public Location getMinimum() {
        return _min.toLocation(this.getWorld()).clone();
    }

    public Location getMaximum() {
        return _max.toLocation(this.getWorld()).clone();
    }

    public Vector getDimensions() {
        return _max.clone().subtract(_min);
    }

    public Region addOffset(World world, SafeLocation origin) {

        Location loc = origin.toLocation(world);

        return new Region(
                loc.clone().add(_min),
                loc.clone().add(_max)
        );

    }

    public boolean isWithin(Location loc) {

        if(!loc.getWorld().getName().equals(_worldName))
            return false;

        if(_min.getX() > loc.getX() || _min.getY() > loc.getY() || _min.getZ() > loc.getZ())
            return false;
        if(_max.getX() < loc.getX() || _max.getY() < loc.getY() || _max.getZ() < loc.getZ())
            return false;

        return true;

    }

    public int getVolume() {

        Vector dim = this.getDimensions();
        return (dim.getBlockX() + 1) * (dim.getBlockY() + 1) * (dim.getBlockZ() + 1);

    }

    @Override
    public String toString() {
        return StringParser.locationToString(_min) + " -> " + StringParser.locationToString(_max);
    }

}
