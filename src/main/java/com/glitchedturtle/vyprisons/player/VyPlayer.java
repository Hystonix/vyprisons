package com.glitchedturtle.vyprisons.player;

import com.glitchedturtle.common.util.TAssert;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineManager;
import com.glitchedturtle.vyprisons.schematic.SchematicType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VyPlayer {

    private VyPlayerManager _playerManager;

    private UUID _ownerId;

    private PlayerMineInstance _mine = null;

    private CompletableFuture<PlayerMineInstance> _mineFuture = null;
    private CompletableFuture<PlayerMineInstance> _mineCreateFuture = null;

    VyPlayer(VyPlayerManager playerManager, UUID ownerId) {
        _playerManager = playerManager;
        _ownerId = ownerId;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(_ownerId);
    }

    public CompletableFuture<PlayerMineInstance> fetchMine() {

        if(_mine != null)
            return CompletableFuture.completedFuture(_mine);

        if(_mineFuture != null)
            return _mineFuture;
        if(_mineCreateFuture != null)
            return _mineCreateFuture;

        PlayerMineManager mineManager = _playerManager.getMineManager();
        _mineFuture = mineManager.loadMine(_ownerId);

        _mineFuture.whenComplete((mine, ex) -> {
            _mine = mine;
            _mineFuture = null;
        });

        return _mineFuture;

    }

    public CompletableFuture<PlayerMineInstance> createMine(SchematicType type) {

        TAssert.assertTrue(_mine == null, "Mine already registered");
        if(_mineCreateFuture != null)
            return _mineCreateFuture;

        PlayerMineManager mineManager = _playerManager.getMineManager();
        _mineCreateFuture = mineManager.createMine(_ownerId, type);

        _mineCreateFuture.whenComplete((mine, ex) -> {
            _mine = mine;
            _mineCreateFuture = null;
        });

        return _mineCreateFuture;

    }

}
