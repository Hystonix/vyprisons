package com.glitchedturtle.vyprisons.player.mine.listener;

import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveHandler implements Listener {

    private VyPlayerManager _playerManager;

    public PlayerMoveHandler(VyPlayerManager playerManager) {
        _playerManager = playerManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent ev) {

        Player ply = ev.getPlayer();
        World mineWorld = Conf.MINE_WORLD.getWorld();

        if(ply.getWorld() != mineWorld)
            return;
        if(ev.getTo().getChunk().equals(ev.getFrom().getChunk()))
            return;

        VyPlayer vyPlayer = _playerManager.getCachedPlayer(ply.getUniqueId());
        if(vyPlayer == null) {

            ply.teleport(Conf.DEFAULT_TP_POSITION.toLocation(mineWorld));
            ply.sendMessage(Conf.INVALID_POSITION_MSG);

            return;

        }

        if(!vyPlayer.isInValidPosition())
            vyPlayer.resetPosition();

    }

}
