package com.glitchedturtle.vyprisons.command.impl.mine;

import com.glitchedturtle.vyprisons.command.abs.VySubPlayerCommand;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VyUnloadCommand extends VySubPlayerCommand {

    private VyPlayerManager _playerManager;

    public VyUnloadCommand(VyPlayerManager playerManager) {
        super("unload", "vyprison.command.unload", "[name]", "Unload your loaded mine");
        _playerManager = playerManager;
    }

    @Override
    public void executeCommand(VyPlayer vyPlayer, String[] args) {

        Player ply = vyPlayer.getPlayer();
        VyPlayer tVyPlayer = vyPlayer;

        if(args.length > 0) {

            if(!ply.hasPermission("vyprison.command.unload.others")) {

                ply.sendMessage(Conf.CMD_MISSING_PERMISSION);
                return;

            }

            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                ply.sendMessage(Conf.CMD_UNLOAD_TARGET_NOT_EXIST
                    .replaceAll("%name%", args[0])
                );
                return;
            }

            tVyPlayer = _playerManager.getCachedPlayer(target.getUniqueId());
            if(tVyPlayer == null) {
                ply.sendMessage(Conf.CMD_UNLOAD_NOT_LOADED);
                return;
            }

        }

        if(tVyPlayer.getCachedMine() == null) {
            ply.sendMessage(Conf.CMD_UNLOAD_NOT_LOADED);
            return;
        }

        tVyPlayer.unload();
        ply.sendMessage(Conf.CMD_UNLOAD_SUCCESS);

    }

}
