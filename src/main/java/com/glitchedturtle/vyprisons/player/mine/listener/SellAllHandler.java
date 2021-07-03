package com.glitchedturtle.vyprisons.player.mine.listener;

import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.player.mine.lottery.MineLotteryHandler;
import com.glitchedturtle.vyprisons.util.VaultHook;
import me.clip.autosell.events.SellAllEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SellAllHandler implements Listener {

    private VyPlayerManager _playerManager;

    public SellAllHandler(VyPlayerManager playerManager) {
        _playerManager = playerManager;
    }

    @EventHandler
    public void onSellAll(SellAllEvent ev) {

        Player ply = ev.getPlayer();

        if(ply.getWorld() != Conf.MINE_WORLD.getWorld())
            return;

        VyPlayer vyPlayer = _playerManager.fetchPlayer(ply.getUniqueId());
        if(vyPlayer.getVisiting() == null)
            return;

        PlayerMineInstance instance = vyPlayer.getVisiting();
        MineLotteryHandler lotteryHandler = instance.getLotteryHandler();

        double taxLevel = instance.getTaxLevel();
        if(!lotteryHandler.isLotteryEnabled())
            taxLevel = 0;

        double tax = ev.getTotalCost() * taxLevel;
        ev.setTotalCost(ev.getTotalCost() - tax);

        Economy econ = VaultHook.getEconomy();
        econ.depositPlayer(Bukkit.getOfflinePlayer(instance.getOwnerUniqueId()), tax * Conf.TAX_TO_OWNER);
        lotteryHandler.increaseValue(tax * Conf.TAX_LOTTERY);

    }

}
