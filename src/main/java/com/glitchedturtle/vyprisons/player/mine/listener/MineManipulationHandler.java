package com.glitchedturtle.vyprisons.player.mine.listener;

import com.glitchedturtle.common.region.Region;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MineManipulationHandler implements Listener {

    private VyPlayerManager _playerManager;

    public MineManipulationHandler(VyPlayerManager playerManager) {
        _playerManager = playerManager;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Player ply = event.getPlayer();
        VyPlayer vyPlayer = _playerManager.fetchPlayer(ply.getUniqueId());

        if(Conf.MINE_WORLD.getWorld() != ply.getWorld()) {
            return;
        }

        if(vyPlayer == null) {

            event.setCancelled(true);
            return;

        }

        PlayerMineInstance instance = vyPlayer.getVisiting();
        if(instance == null) {
            vyPlayer.resetPosition();
            return;
        }

        SchematicInstance schematicInstance = instance.getSchematicInstance();
        if(schematicInstance == null || schematicInstance.getState() != SchematicInstance.InstanceState.READY) {
            vyPlayer.resetPosition();
            return;
        }

        Block block = event.getBlock();

        Region mineRegion = schematicInstance.getMineRegion();
        if(!mineRegion.isWithin(block.getLocation())) {

            if(Conf.INVALID_BLOCK_MODIFY_MSG_SEND_TO_CHAT)
                ply.sendMessage(Conf.INVALID_BLOCK_MODIFY_MSG);

            //TODO: Send to action bar

            ply.playSound(ply.getEyeLocation(), Conf.INVALID_BLOCK_MODIFY_SOUND, 1, 2);
            event.setCancelled(true);

            return;

        }

        instance.markBlockBreak(1);

    }

}
