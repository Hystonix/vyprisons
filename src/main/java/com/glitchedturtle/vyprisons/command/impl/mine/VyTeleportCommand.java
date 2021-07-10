package com.glitchedturtle.vyprisons.command.impl.mine;

import com.glitchedturtle.vyprisons.command.abs.VySubPlayerCommand;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class VyTeleportCommand extends VySubPlayerCommand {

    private VyPlayerManager _playerManager;

    public VyTeleportCommand(VyPlayerManager playerManager) {
        super("teleport", "vyprison.command.teleport", "[target]", "Teleport to a player's mine");

        _playerManager = playerManager;

    }

    @Override
    public void executeCommand(VyPlayer vyPlayer, String[] args) {

        Player ply = vyPlayer.getPlayer();
        VyPlayer targetVyPlayer = vyPlayer;

        boolean other = args.length > 0;

        if(other) {

            Player targetPly = Bukkit.getPlayer(args[0]);
            if(targetPly == null) {

                ply.sendMessage(Conf.CMD_TELEPORT_TARGET_NOT_FOUND.replaceAll("%name%", args[0]));
                return;

            }

            targetVyPlayer = _playerManager.fetchPlayer(targetPly.getUniqueId());

        }

        CompletableFuture<PlayerMineInstance> mineFuture = targetVyPlayer.fetchMine();
        mineFuture.thenAccept(mine -> {

            if(mine == null) {

                String msg = Conf.CMD_TELEPORT_NO_MINE;
                if(other)
                    msg = Conf.CMD_TELEPORT_NO_MINE_OTHER.replaceAll("%name%", args[0]);

                ply.sendMessage(msg);
                return;

            }

            if(!mine.isPermitted(vyPlayer)) {

                ply.sendMessage(Conf.CMD_TELEPORT_PRIVACY);

            }

            vyPlayer.warpToMine(mine);
            ply.sendMessage(Conf.CMD_TELEPORT_SUCCESS);

        });
        mineFuture.exceptionally(ex -> {

            ex.printStackTrace();

            ply.sendMessage(Conf.CMD_TELEPORT_TARGET_FAILED_FETCH);
            return null;

        });
    }

}
