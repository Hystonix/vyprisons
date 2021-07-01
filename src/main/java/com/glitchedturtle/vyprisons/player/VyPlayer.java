package com.glitchedturtle.vyprisons.player;

import com.glitchedturtle.common.util.TAssert;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineManager;
import com.glitchedturtle.vyprisons.schematic.SchematicType;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicInstance;
import com.glitchedturtle.vyprisons.util.TFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VyPlayer {

    private VyPlayerManager _playerManager;

    private UUID _ownerId;

    private PlayerMineInstance _mine = null;
    private PlayerMineInstance _visiting = null;

    private CompletableFuture<PlayerMineInstance> _mineFuture = null;
    private CompletableFuture<PlayerMineInstance> _mineCreateFuture = null;

    private Map<String, Long> _cooldownMap = new HashMap<>();

    VyPlayer(VyPlayerManager playerManager, UUID ownerId) {
        _playerManager = playerManager;
        _ownerId = ownerId;
    }

    public void destroy() {

        if(_mineFuture != null)
            _mineFuture.cancel(true);
        if(_mineCreateFuture != null)
            _mineCreateFuture.cancel(true);

        _mineFuture = null;
        _mineCreateFuture = null;

        if(_mine != null)
            _mine.destroy();
        _mine = null;

        if(_visiting != null)
            _visiting.removePlayer(this);
        _visiting = null;

    }

    public Player getPlayer() {
        return Bukkit.getPlayer(_ownerId);
    }

    public String getName() {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(_ownerId);
        return offlinePlayer.getName();

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

    public PlayerMineInstance getCachedMine() {
        return _mine;
    }

    public UUID getUniqueId() {
        return _ownerId;
    }

    public PlayerMineInstance getVisiting() {
        return _visiting;
    }

    public void setVisiting(PlayerMineInstance visiting) {

        if(_visiting != null)
            _visiting.removePlayer(this);
        if(visiting != null)
            visiting.addPlayer(this);

        _visiting = visiting;

    }

    public void warpToMine(PlayerMineInstance mine) {

        Player ply = this.getPlayer();

        SchematicInstance instance = mine.getSchematicInstance();
        if(instance == null)
            return;
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

        mine.validateMineState();
        this.setVisiting(mine);

        ply.playSound(ply.getEyeLocation(), Conf.CMD_TELEPORT_SOUND, 1, 1);

    }

    public void resetPosition() {

        Player ply = this.getPlayer();
        if(ply == null)
            return;

        this.setVisiting(null);

        ply.sendMessage(Conf.INVALID_POSITION_MSG);
        ply.playSound(ply.getEyeLocation(), Conf.INVALID_POSITION_SOUND, 1, 1);

        ply.teleport(Conf.DEFAULT_TP_POSITION.toLocation(Conf.DEFAULT_TP_WORLD.getWorld()));

    }

    public boolean doCooldown(String token, long duration) {

        if(!_cooldownMap.containsKey(token)) {

            _cooldownMap.put(token, System.currentTimeMillis() + duration);
            return false;

        }

        long expireAt = _cooldownMap.get(token);
        if(expireAt > System.currentTimeMillis()) {

            Player ply = this.getPlayer();
            if(ply != null) {

                ply.sendMessage(ChatColor.GRAY + "You can not " + ChatColor.LIGHT_PURPLE + token
                        + ChatColor.GRAY + "for another " + ChatColor.LIGHT_PURPLE +
                        TFormatter.formatMs(expireAt - System.currentTimeMillis()));

            }

            return true;

        }

        _cooldownMap.remove(token);
        return false;

    }

}
