package com.glitchedturtle.vyprisons.player.mine.listener;

import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.schematic.SchematicManager;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicInstance;
import com.glitchedturtle.vyprisons.util.ElegantPair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerPositionHandler implements Listener {

    private VyPlayerManager _playerManager;
    private SchematicManager _schematicManager;

    public PlayerPositionHandler(SchematicManager schematicManager, VyPlayerManager playerManager) {
        _schematicManager = schematicManager;
        _playerManager = playerManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player ply = event.getPlayer();

        if(ply.getWorld() != Conf.MINE_WORLD.getWorld())
            return;

        VyPlayer vyPlayer = _playerManager.fetchPlayer(ply.getUniqueId());
        vyPlayer.resetPosition();

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player ply = event.getPlayer();

        VyPlayer vyPlayer = _playerManager.getCachedPlayer(ply.getUniqueId());
        if(vyPlayer == null)
            return;

        vyPlayer.setVisiting(null);
        vyPlayer.unloadMine();

    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent ev) {

        Player ply = ev.getPlayer();
        Location to = ev.getTo();

        VyPlayer vyPlayer = _playerManager.getCachedPlayer(ply.getUniqueId());

        if(to.getWorld() != Conf.MINE_WORLD.getWorld()) {

            if(vyPlayer == null)
                return;

            vyPlayer.setVisiting(null);
            return;

        }

        if(vyPlayer == null)
            vyPlayer = _playerManager.fetchPlayer(ply.getUniqueId());

        int blockSize = Conf.MINE_BLOCK_SIZE * 16;
        int id = ElegantPair.pair(
                Math.floorDiv(to.getBlockX(), blockSize),
                Math.floorDiv(to.getBlockZ(), blockSize)
        );

        SchematicInstance instance = _schematicManager.getById(id);
        if(instance == null
                || !instance.isReserved()) {

            ev.setCancelled(true);
            vyPlayer.resetPosition();

            return;

        }

        PlayerMineInstance reservedBy = instance.getReservedBy();

        if(!reservedBy.isPermitted(vyPlayer)) {

            ev.setCancelled(true);
            ply.sendMessage(Conf.INVALID_POSITION_PRIVACY_MSG);

            return;

        }

        vyPlayer.setVisiting(reservedBy);

    }

}
