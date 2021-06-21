package com.glitchedturtle.vyprisons.command.abs;

import com.glitchedturtle.vyprisons.player.VyPlayer;

public abstract class VySubPlayerCommand extends VySubCommand {

    public VySubPlayerCommand(String name, String permissionNode, String usage, String description) {
        super(name, permissionNode, usage, description);
    }

    public abstract void executeCommand(VyPlayer player, String[] args);

}
