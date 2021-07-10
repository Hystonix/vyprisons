package com.glitchedturtle.vyprisons.command.impl.mine;

import com.glitchedturtle.vyprisons.command.abs.VySubPlayerCommand;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.util.TFormatter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class VyTaxCommand extends VySubPlayerCommand {

    public VyTaxCommand() {
        super("tax", "vyprison.command.tax", "[new amount]", "View and set tax level");
    }

    @Override
    public void executeCommand(VyPlayer vyPlayer, String[] args) {

        Player ply = vyPlayer.getPlayer();

        if(args.length == 0) {

            PlayerMineInstance mineInstance = vyPlayer.getVisiting();
            if(mineInstance == null) {

                ply.sendMessage(Conf.CMD_TAX_NOT_VISITING);
                return;

            }

            OfflinePlayer owner = Bukkit.getOfflinePlayer(mineInstance.getOwnerUniqueId());
            ply.sendMessage(Conf.CMD_TAX_INFO
                .replaceAll("%name%", owner.getName() != null ? owner.getName() : "MissingName")
                .replaceAll("%tax%", TFormatter.formatPercentage(mineInstance.getTaxLevel()))
            );

            return;

        }

        double input;
        try {
            input = Double.parseDouble(args[0]);
        } catch(Exception ex) {

            ply.sendMessage(Conf.CMD_TAX_INVALID
                .replaceAll("%input%", args[0])
            );
            return;

        }

        if(input > 1)
            input /= 100;
        double newTaxLevel = Math.min(Math.max(input, Conf.TAX_MIN), Conf.TAX_MAX);

        vyPlayer.fetchMine().whenComplete((mine, ex) -> {

            if(ex != null) {

                ply.sendMessage(Conf.CMD_TAX_ERROR_FETCH);
                ex.printStackTrace();

                return;

            }

            if(mine == null) {

                ply.sendMessage(Conf.CMD_TAX_NOT_OWNED);
                return;

            }

            mine.setTaxLevel(newTaxLevel);
            ply.sendMessage(Conf.CMD_TAX_UPDATE_SUCCESS
                .replaceAll("%tax_level%", TFormatter.formatPercentage(newTaxLevel))
            );

        });

    }

}
