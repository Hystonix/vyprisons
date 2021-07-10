package com.glitchedturtle.vyprisons.command.impl.mine;

import com.glitchedturtle.common.menu.MenuManager;
import com.glitchedturtle.common.menu.impl.ConfirmPage;
import com.glitchedturtle.common.menu.impl.EmptyMenu;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.command.abs.VySubPlayerCommand;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.data.DatabaseConnector;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import com.glitchedturtle.vyprisons.player.mine.action.DeleteMineInstanceAction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class VyResetCommand extends VySubPlayerCommand {

    private MenuManager _menuManager;
    private DatabaseConnector _dbConnector;
    private VyPlayerManager _playerManager;

    public VyResetCommand(MenuManager menuManager, DatabaseConnector dbConnector, VyPlayerManager playerManager) {
        super("reset", "vyprison.command.reset", "[name]", "Reset your mine");

        _menuManager = menuManager;
        _dbConnector = dbConnector;
        _playerManager = playerManager;

    }

    @Override
    public void executeCommand(VyPlayer vyPlayer, String[] args) {

        Player ply = vyPlayer.getPlayer();

        String name = ply.getName();
        VyPlayer tVyPlayer = vyPlayer;

        if(args.length > 0) {

            if(!ply.hasPermission("vyprison.command.reset.others")) {

                ply.sendMessage(Conf.CMD_MISSING_PERMISSION);
                return;

            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            name = target.getName();
            tVyPlayer = _playerManager.fetchPlayer(target.getUniqueId());

        }

        EmptyMenu<VyPrisonPlugin> menu = new EmptyMenu<>();
        VyPlayer finalTVyPlayer = tVyPlayer;
        menu.openPage(new ConfirmPage<EmptyMenu<?>>(menu,
                "Reset " + name + "'s Mine?",
                Arrays.asList("This process will permanently delete all data",
                            "related to their mine, including tiers and lottery state"
                        ),
                false,
                (res) -> {

                    ply.closeInventory();

                    if(!res)
                        return;

                    _dbConnector.execute(new DeleteMineInstanceAction(finalTVyPlayer.getUniqueId())).thenAccept((v) -> {

                        ply.sendMessage(Conf.CMD_RESET_SUCCESS);
                        finalTVyPlayer.unload();

                    });

                }
        ));

        _menuManager.openMenu(ply, menu);

    }

}
