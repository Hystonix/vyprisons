package com.glitchedturtle.vyprisons.player;

import com.glitchedturtle.vyprisons.PluginStartException;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.data.DatabaseConnector;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineManager;
import com.google.common.cache.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class VyPlayerManager {

    private DatabaseConnector _databaseConnector;
    private PlayerMineManager _mineManager;

    private ConfigurationSection _tierConf;

    private LoadingCache<UUID, VyPlayer> _playerCache;

    public VyPlayerManager(VyPrisonPlugin pluginInstance) {

        _databaseConnector = pluginInstance.getDatabaseConnector();
        _mineManager = new PlayerMineManager(pluginInstance, this);

        _playerCache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .removalListener(this::handleRemoval)
                    .build(new VyPlayerLoader(this));
        _tierConf = pluginInstance.getConfig().getConfigurationSection("tiers");

    }

    public void initialize() throws PluginStartException {
        _mineManager.initialize(_tierConf);
    }

    private void handleRemoval(RemovalNotification<UUID, VyPlayer> notif) {

        notif.getValue().unload();

    }

    public VyPlayer fetchPlayer(UUID uuid) {
        try {
            return _playerCache.get(uuid);
        } catch (ExecutionException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    PlayerMineManager getMineManager() {
        return _mineManager;
    }

    public Collection<VyPlayer> getCachedPlayers() {
        return _playerCache.asMap().values();
    }

    public VyPlayer getCachedPlayer(UUID uniqueId) {
        return _playerCache.getIfPresent(uniqueId);
    }

}
