package com.glitchedturtle.vyprisons.command.impl.amanage.ui;

import com.glitchedturtle.common.menu.AbstractMenu;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.command.impl.amanage.ui.page.AdminRootPage;
import com.glitchedturtle.vyprisons.player.VyPlayer;

public class AdminManageMenu extends AbstractMenu {

    private VyPrisonPlugin _pluginInstance;
    private VyPlayer _vyPlayer;

    public AdminManageMenu(VyPrisonPlugin pluginInstance, VyPlayer vyPlayer) {

        _pluginInstance = pluginInstance;
        _vyPlayer = vyPlayer;

        this.setRoot(new AdminRootPage(this));

    }

    public VyPrisonPlugin getPluginInstance() {
        return _pluginInstance;
    }

    public VyPlayer getVyPlayer() {
        return _vyPlayer;
    }

}
