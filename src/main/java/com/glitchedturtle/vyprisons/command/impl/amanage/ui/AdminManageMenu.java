package com.glitchedturtle.vyprisons.command.impl.amanage.ui;

import com.glitchedturtle.common.menu.AbstractMenu;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.command.impl.amanage.ui.page.AdminRootPage;

public class AdminManageMenu extends AbstractMenu {

    private VyPrisonPlugin _pluginInstance;

    public AdminManageMenu(VyPrisonPlugin pluginInstance) {

        _pluginInstance = pluginInstance;

        this.setRoot(new AdminRootPage(this));

    }

    public VyPrisonPlugin getPluginInstance() {
        return _pluginInstance;
    }

}
