package com.glitchedturtle.vyprisons.command.abs;

import org.bukkit.command.CommandSender;

public abstract class VySubConsoleCommand extends VySubCommand {

    public VySubConsoleCommand(String name, String permissionNode, String usage, String description) {
        super(name, permissionNode, usage, description);
    }

    public abstract void executeCommand(CommandSender sender, String[] args);

}
