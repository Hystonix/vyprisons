package com.glitchedturtle.vyprisons.command.impl.mine.manage;

import com.glitchedturtle.common.menu.MenuManager;
import com.glitchedturtle.vyprisons.command.abs.VySubPlayerCommand;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.MineManageMenu;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page.MinePrivacyPage;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.mine.MineAccessLevel;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class VyPrivacyCommand extends VySubPlayerCommand {

    private MenuManager _menuManager;

    public VyPrivacyCommand(MenuManager menuManager) {

        super("privacy", "vyprison.command.privacy", "[setting]", "Manage your mine's privacy setting");
        _menuManager = menuManager;

    }

    @Override
    public void executeCommand(VyPlayer vyPlayer, String[] args) {

        Player ply = vyPlayer.getPlayer();

        CompletableFuture<PlayerMineInstance> mineFuture = vyPlayer.fetchMine();
        mineFuture.thenAccept((mine) -> {

            if(mine == null) {

                ply.sendMessage(Conf.CMD_MANAGE_NOT_MINE);
                return;

            }

            if(args.length > 0) {

                MineAccessLevel level;
                try {
                    level = MineAccessLevel.valueOf(args[0].toUpperCase());
                } catch(Exception ex) {

                    ply.sendMessage(Conf.CMD_PRIVACY_INVALID_SETTING);
                    return;

                }

                if(!ply.hasPermission(level.getPermissionNode())) {

                    ply.sendMessage(Conf.CMD_PRIVACY_INSUFFICIENT_PERMISSION
                        .replaceAll("%privacy_level%", level.toString())
                    );
                    return;

                }

                if(vyPlayer.cooldown("Update privacy", Conf.MINE_ACCESS_UPDATE_COOLDOWN))
                    return;

                mine.setAccessLevel(level);
                ply.sendMessage(Conf.CMD_PRIVACY_SUCCESS
                    .replaceAll("%privacy%", level.toString())
                );
                return;

            }

            MineManageMenu menu = new MineManageMenu(vyPlayer, mine);
            menu.openPage(new MinePrivacyPage(menu));

            _menuManager.openMenu(ply, menu);

        });

        mineFuture.exceptionally((ex) -> {

            ex.printStackTrace();

            ply.sendMessage(Conf.CMD_MANAGE_FETCH_FAILED);
            return null;

        });

    }

}
