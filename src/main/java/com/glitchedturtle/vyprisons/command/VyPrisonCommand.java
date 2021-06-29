package com.glitchedturtle.vyprisons.command;

import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.command.abs.VySubCommand;
import com.glitchedturtle.vyprisons.command.abs.VySubConsoleCommand;
import com.glitchedturtle.vyprisons.command.abs.VySubPlayerCommand;
import com.glitchedturtle.vyprisons.command.impl.amanage.VyAdminManageCommand;
import com.glitchedturtle.vyprisons.command.impl.mine.VyCreateCommand;
import com.glitchedturtle.vyprisons.command.impl.mine.VyTeleportCommand;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.VyManageCommand;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.VyPrivacyCommand;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.VyStyleCommand;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.VyTierCommand;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VyPrisonCommand implements CommandExecutor, TabCompleter {

    public VyPlayerManager _playerManager;
    public Map<String, VySubCommand> _commandMap = new HashMap<>();

    public VyPrisonCommand(VyPrisonPlugin pluginInstance) {

        _playerManager = pluginInstance.getPlayerManager();

        this.registerCommand(new VyAdminManageCommand(pluginInstance));

        this.registerCommand(new VyTeleportCommand(_playerManager));
        this.registerCommand(new VyCreateCommand(pluginInstance.getSchematicManager()));

        this.registerCommand(new VyManageCommand(pluginInstance.getMenuManager()));
        this.registerCommand(new VyPrivacyCommand(pluginInstance.getMenuManager()));
        this.registerCommand(new VyStyleCommand(pluginInstance.getMenuManager()));
        this.registerCommand(new VyTierCommand(pluginInstance.getMenuManager()));

    }

    public void registerCommand(VySubCommand subCommand) {
        _commandMap.put(subCommand.getName(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(args.length == 0) {

            this.printCommandList(sender);
            return true;

        }

        VySubCommand subCommand = _commandMap.get(args[0].toLowerCase());
        if(subCommand == null) {

            this.printCommandList(sender);
            return true;

        }

        if(!sender.hasPermission(subCommand.getPermissionNode())) {

            sender.sendMessage(Conf.CMD_MISSING_PERMISSION);
            return true;

        }

        String[] poppedArgs = Arrays.copyOfRange(args, 1, args.length);

        if(subCommand instanceof VySubConsoleCommand) {

            ((VySubConsoleCommand) subCommand).executeCommand(sender, poppedArgs);
            return true;

        }

        Player ply = (Player) sender;
        VySubPlayerCommand plyCommand = (VySubPlayerCommand) subCommand;

        VyPlayer vyPlayer = _playerManager.fetchPlayer(ply.getUniqueId());
        plyCommand.executeCommand(vyPlayer, poppedArgs);

        return true;

    }

    public void printCommandList(CommandSender sender) {

        sender.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString() + "VyPrisons Commands");
        for(VySubCommand cmd : _commandMap.values()) {

            if(!sender.hasPermission(cmd.getPermissionNode()))
                return;

            sender.sendMessage(
                    ChatColor.LIGHT_PURPLE + "/" + cmd.getName() + " " + cmd.getUsage()
                    + ChatColor.GRAY + " " + cmd.getDescription()
            );

        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        if(args.length <= 1) {

            return _commandMap.values().stream()
                    .filter(c -> sender.hasPermission(c.getPermissionNode()))
                    .map(VySubCommand::getName).collect(Collectors.toList());

        }

        return null;

    }
}
