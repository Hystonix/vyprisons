package com.glitchedturtle.vyprisons.command.impl.mine;

import com.glitchedturtle.vyprisons.command.abs.VySubPlayerCommand;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.schematic.SchematicManager;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicInstance;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class VyCreateCommand extends VySubPlayerCommand {

    private SchematicManager _schematicManager;

    public VyCreateCommand(SchematicManager schematicManager) {
        super("create", "vyprison.command.create", "", "Create a new mine");

        _schematicManager = schematicManager;

    }

    @Override
    public void executeCommand(VyPlayer vyPlayer, String[] args) {

        Player ply = vyPlayer.getPlayer();

        CompletableFuture<PlayerMineInstance> checkFuture = vyPlayer.fetchMine();
        checkFuture.whenComplete((existingMine, ex) -> {

            if(ex != null) {

                ply.sendMessage(Conf.CMD_CREATE_FAILED);

                ex.printStackTrace();
                return;

            }

            if(existingMine != null) {

                ply.sendMessage(Conf.CMD_CREATE_ALREADY_OWNER);
                return;

            }

            CompletableFuture<PlayerMineInstance> createFuture = vyPlayer.createMine(_schematicManager.getDefaultType());
            createFuture.thenAccept(mine -> {

                if(mine.isPermitted(vyPlayer)) {

                    ply.teleport(mine.getWarpPosition());
                    mine.validateMineState();

                } else {

                    ply.sendMessage(Conf.CMD_CREATE_PLACE_IN_PROGRESS);
                    return;

                }

                ply.sendMessage(Conf.CMD_CREATE_SUCCESS);
                ply.playSound(ply.getEyeLocation(), Conf.CMD_CREATE_SUCCESS_SOUND, 1, 2);

            });

        });

    }

}
