package com.glitchedturtle.vyprisons.player;

import com.google.common.cache.CacheLoader;

import java.util.UUID;

public class VyPlayerLoader extends CacheLoader<UUID, VyPlayer> {

    private VyPlayerManager _playerManager;

    public VyPlayerLoader(VyPlayerManager playerManager) {
        _playerManager = playerManager;
    }

    @Override
    public VyPlayer load(UUID uuid) {
        return new VyPlayer(_playerManager, uuid);
    }

}
