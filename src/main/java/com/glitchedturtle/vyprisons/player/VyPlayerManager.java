package com.glitchedturtle.vyprisons.player;

import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.data.DatabaseConnector;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public class VyPlayerManager {

    private DatabaseConnector _databaseConnector;
    private PlayerMineManager _mineManager;

    public VyPlayerManager(VyPrisonPlugin pluginInstance) {

        _databaseConnector = pluginInstance.getDatabaseConnector();
        _mineManager = new PlayerMineManager(_databaseConnector, pluginInstance.getSchematicManager());

    }

    public VyPlayer fetchPlayer(UUID uuid) {
        return new VyPlayer(this, uuid);
    }

    PlayerMineManager getMineManager() {
        return _mineManager;
    }


}
