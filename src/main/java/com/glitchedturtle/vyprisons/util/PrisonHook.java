package com.glitchedturtle.vyprisons.util;

import me.drawethree.ultraprisoncore.UltraPrisonCore;
import me.drawethree.ultraprisoncore.gangs.UltraPrisonGangs;
import me.drawethree.ultraprisoncore.gangs.api.UltraPrisonGangsAPI;
import me.drawethree.ultraprisoncore.placeholders.UltraPrisonPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PrisonHook {

    public static UltraPrisonGangsAPI getGangAPI() {
        return UltraPrisonGangs.getInstance().getApi();
    }

    public static boolean isHooked() {
        return Bukkit.getPluginManager().getPlugin("UltraPrisonCore") != null;
    }

}
