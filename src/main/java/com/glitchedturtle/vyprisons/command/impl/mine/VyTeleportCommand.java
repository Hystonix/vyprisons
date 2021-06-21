package com.glitchedturtle.vyprisons.command.impl.mine;

import com.glitchedturtle.vyprisons.command.abs.VySubPlayerCommand;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

                ply.sendMessage(other ? Conf.CMD_TELEPORT_NO_MINE_OTHER : Conf.CMD_TELEPORT_NO_MINE);
                return;

            }

            SchematicInstance instance = mine.getSchematicInstance();
            if(instance.getState() != SchematicInstance.InstanceState.READY) {

                if(!mine.attemptReassign()) {

                    ply.sendMessage(Conf.CMD_TELEPORT_PLACE_IN_PROGRESS);
                    return;

                } else {
                    instance = mine.getSchematicInstance();
                }

            }

            Location warpPos = instance.getWarpPosition();
            ply.teleport(warpPos);

            ply.playSound(ply.getEyeLocation(), Conf.CMD_TELEPORT_SOUND, 1, 1);
            ply.sendMessage(Conf.CMD_TELEPORT_SUCCESS);

        });
        mineFuture.exceptionally(ex -> {

            ex.printStackTrace();

            ply.sendMessage(Conf.CMD_TELEPORT_TARGET_FAILED_FETCH);
            return null;

        });
    }

}
