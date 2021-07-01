package com.glitchedturtle.vyprisons.command.impl.mine.manage;

import com.glitchedturtle.common.menu.MenuManager;
import com.glitchedturtle.vyprisons.command.abs.VySubPlayerCommand;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.MineManageMenu;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page.MinePrivacyPage;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page.MineStylePage;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class VyPrivacyCommand extends VySubPlayerCommand {

    private MenuManager _menuManager;

    public VyPrivacyCommand(MenuManager menuManager) {

        super("privacy", "vyprison.command.privacy", "", "Manage your mine's privacy setting");
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

            MineManageMenu menu = new MineManageMenu(vyPlayer, mine);
            menu.openPage(new MinePrivacyPage(menu));

            _menuManager.openMenu(ply, menu);

        });

        mineFuture.exceptionally((ex) -> {

            ex.printStackTrace();

            ply.sendMessage(Conf.CMD_MANAGE_MINE_FETCH_FAILED);
            return null;

        });

    }

}
