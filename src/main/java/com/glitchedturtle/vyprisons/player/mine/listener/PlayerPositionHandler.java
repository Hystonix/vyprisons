package com.glitchedturtle.vyprisons.player.mine.listener;

import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerPositionHandler implements Listener {

    private VyPlayerManager _playerManager;

    public PlayerPositionHandler(VyPlayerManager playerManager) {
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

}
