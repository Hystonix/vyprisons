package com.glitchedturtle.vyprisons.command.impl.amanage;

import com.glitchedturtle.common.menu.MenuManager;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.command.abs.VySubPlayerCommand;
import com.glitchedturtle.vyprisons.command.impl.amanage.ui.AdminManageMenu;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VyAdminManageCommand extends VySubPlayerCommand {

    private VyPrisonPlugin _pluginInstance;

    public VyAdminManageCommand(VyPrisonPlugin pluginInstance) {
        super("admin", "vyprison.command.admin", "", "Open admin UI");

        _pluginInstance = pluginInstance;

    }

    @Override
    public void executeCommand(VyPlayer player, String[] args) {

        MenuManager menuManager = _pluginInstance.getMenuManager();
        Player ply = player.getPlayer();

        AdminManageMenu manageMenu = new AdminManageMenu(_pluginInstance);
        menuManager.openMenu(ply, manageMenu);

    }

}
