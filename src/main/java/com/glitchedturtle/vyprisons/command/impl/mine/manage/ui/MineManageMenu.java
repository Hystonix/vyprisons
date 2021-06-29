package com.glitchedturtle.vyprisons.command.impl.mine.manage.ui;

import com.glitchedturtle.common.menu.AbstractMenu;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page.MineManageRoot;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;

public class MineManageMenu extends AbstractMenu<VyPrisonPlugin> {

    private PlayerMineInstance _mineInstance;

    public MineManageMenu(PlayerMineInstance instance) {

        _mineInstance = instance;

        this.setRoot(new MineManageRoot(this));

    }

    public PlayerMineInstance getMineInstance() {
        return _mineInstance;
    }

}
