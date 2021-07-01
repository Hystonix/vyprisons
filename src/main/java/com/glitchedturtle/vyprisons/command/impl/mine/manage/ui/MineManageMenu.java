package com.glitchedturtle.vyprisons.command.impl.mine.manage.ui;

import com.glitchedturtle.common.menu.AbstractMenu;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page.MineManageRoot;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;

public class MineManageMenu extends AbstractMenu<VyPrisonPlugin> {

    private VyPlayer _vyPlayer;
    private PlayerMineInstance _mineInstance;

    public MineManageMenu(VyPlayer vyPlayer, PlayerMineInstance instance) {

        _vyPlayer = vyPlayer;
        _mineInstance = instance;

        this.setRoot(new MineManageRoot(this));

    }

    public VyPlayer getVyPlayer() {
        return _vyPlayer;
    }
    public PlayerMineInstance getMineInstance() {
        return _mineInstance;
    }

}
