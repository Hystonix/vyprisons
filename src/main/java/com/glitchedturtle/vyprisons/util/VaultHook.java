package com.glitchedturtle.vyprisons.util;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy ECONOMY = null;

    public static Economy getEconomy() {

        if(ECONOMY != null)
            return ECONOMY;

        RegisteredServiceProvider<Economy> serviceProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(Economy.class);

        if(serviceProvider == null)
            return null;

        ECONOMY = serviceProvider.getProvider();
        return ECONOMY;

    }

}
