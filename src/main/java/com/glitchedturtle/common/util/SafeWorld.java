package com.glitchedturtle.common.util;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class SafeWorld {

    private String _worldName;

    public SafeWorld(String worldName) {
        _worldName = worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld(_worldName);
    }

}
