package com.glitchedturtle.vyprisons.player.mine;

import com.glitchedturtle.common.util.TAssert;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.data.DatabaseConnector;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.mine.action.FetchMineInstanceAction;
import com.glitchedturtle.vyprisons.player.mine.action.privacy.SetMineAccessLevelAction;
import com.glitchedturtle.vyprisons.player.mine.action.tier.IncrementMineTierAction;
import com.glitchedturtle.vyprisons.player.mine.action.tier.SetMineTierAction;
import com.glitchedturtle.vyprisons.player.mine.action.type.SetSchematicTypeAction;
import com.glitchedturtle.vyprisons.player.mine.lottery.MineLotteryHandler;
import com.glitchedturtle.vyprisons.player.mine.reset.MineResetManager;
import com.glitchedturtle.vyprisons.player.mine.reset.MineResetWorker;
import com.glitchedturtle.vyprisons.schematic.SchematicManager;
import com.glitchedturtle.vyprisons.schematic.SchematicType;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicInstance;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicPool;
import com.glitchedturtle.vyprisons.util.PrisonHook;
import com.glitchedturtle.vyprisons.util.TFormatter;
import com.google.common.base.Enums;
import me.drawethree.ultraprisoncore.UltraPrisonCore;
import me.drawethree.ultraprisoncore.gangs.models.Gang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PlayerMineInstance {

    private PlayerMineManager _mineManager;
    private UUID _ownerUuid;

    private SchematicType _activeSchematic;
    private SchematicInstance _schematicInstance = null;

    private int _blocksRemaining = -1;
    private MineResetWorker.ResetJob _resetJob = null;
    private boolean _hasReset = false;

    private Set<VyPlayer> _mineVisitors = new HashSet<>();

    private int _tier = 1;
    private MineAccessLevel _accessLevel = MineAccessLevel.PRIVATE;

    private MineLotteryHandler _lotteryHandler;
    private double _taxLevel = 0.10;

    PlayerMineInstance(PlayerMineManager manager, UUID ownerUuid) {

        _mineManager = manager;
        _ownerUuid = ownerUuid;

        _lotteryHandler = new MineLotteryHandler(this, manager.getDatabaseConnector(), new HashSet<>(), 0);

    }

    PlayerMineInstance(PlayerMineManager manager, UUID ownerUuid, FetchMineInstanceAction.Response res) {

        _mineManager = manager;
        _ownerUuid = ownerUuid;

        _tier = res.getTier();
        _activeSchematic = _mineManager.getSchematicManager().getTypeById(res.getActiveSchematicId());

        _accessLevel = Enums.getIfPresent(MineAccessLevel.class, res.getAccessLevel())
                .or(MineAccessLevel.PRIVATE);
        _lotteryHandler = new MineLotteryHandler(this, manager.getDatabaseConnector(),
                    res.getLotteryEntries().stream().map(UUID::fromString).collect(Collectors.toSet()),
                    res.getLotteryValue()
                );

    }

    void setSchematic(SchematicType type) {
        _activeSchematic = type;
    }

    public CompletableFuture<Void> setType(SchematicType type) {

        DatabaseConnector connector = _mineManager.getDatabaseConnector();

        return connector.execute(new SetSchematicTypeAction(_ownerUuid, type.getIdentifier())).whenComplete((v, ex) -> {

            if(ex != null) {

                ex.printStackTrace();
                return;

            }

            _resetJob = null;
            _activeSchematic = type;

            this.assignSchematicInstance();

        });

    }

    void assignSchematicInstance() {

        if(_schematicInstance != null)
            _schematicInstance.relinquish();

        SchematicManager manager = _mineManager.getSchematicManager();
        SchematicPool pool = manager.getPool(_activeSchematic);

        TAssert.assertTrue(pool != null, "No pool registered for given type");
        _schematicInstance = pool.reserveAvailable(this);

        if(_schematicInstance == null)
            return;

        Location toWarp;
        if(_schematicInstance.getState() == SchematicInstance.InstanceState.READY) {

            toWarp = _schematicInstance.getWarpPosition();
            this.resetMine();

        } else
            toWarp = Conf.DEFAULT_TP_POSITION.toLocation(Conf.DEFAULT_TP_WORLD.getWorld());

        for(Player ply : this.getPlayerVisitors())
            ply.teleport(toWarp);

    }

    public boolean attemptReassign() {

        SchematicManager manager = _mineManager.getSchematicManager();
        SchematicPool pool = manager.getPool(_activeSchematic);

        TAssert.assertTrue(pool != null, "No pool registered for given type");

        SchematicInstance potential =  pool.reserveAvailable(this, false);
        if(potential == null)
            return false;

        _schematicInstance = potential;
        return true;

    }

    public void resetMine() {

        TAssert.assertTrue(_resetJob == null, "Reset job already queued");

        TAssert.assertTrue(_schematicInstance != null, "Schematic instance is null");
        TAssert.assertTrue(_schematicInstance.getState() == SchematicInstance.InstanceState.READY,
                "Schematic instance not ready");

        MineResetManager resetManager = _mineManager.getResetManager();
        _resetJob = resetManager.enqueueReset(this, !_hasReset);

        _resetJob.setBeginCallback(() -> {

            for(Player ply : this.getPlayerVisitors()) {

                if(_schematicInstance.getMineRegion().isWithin(ply.getLocation()))
                    ply.teleport(_schematicInstance.getWarpPosition());
                ply.sendMessage(Conf.MINE_RESET_MSG);
            }

        });
        _resetJob.setCompleteCallback(() -> {
            _resetJob = null;
            _blocksRemaining = _schematicInstance.getMineRegion().getVolume();
        });

        _hasReset = true;
        System.out.println("[Mine] Enqueued mine reset for mine (owner: " + _ownerUuid.toString() + ")");

    }

    public void validateMineState() {

        int threshold =
                (int) Math.floor(_activeSchematic.getMineOffset().getVolume() * Conf.MINE_RESET_THRESHOLD);
        if(_blocksRemaining > threshold)
            return;
        if(_schematicInstance == null)
            return;
        if(_schematicInstance.getState() != SchematicInstance.InstanceState.READY)
            return;

        this.resetMine();

    }

    public SchematicType getType() {
        return _activeSchematic;
    }

    public SchematicInstance getSchematicInstance() {
        return _schematicInstance;
    }

    public Material randomMineType() {
        return _mineManager.getTierManager()
                .randomType(_tier);
    }

    public boolean canWarp() {

        if(this.getSchematicInstance() == null)
            return false;
        SchematicInstance instance = this.getSchematicInstance();

        return instance.getState() == SchematicInstance.InstanceState.READY;

    }
    public Location getWarpPosition() {
        return _schematicInstance.getWarpPosition();
    }

    public void unload() {

        if(_schematicInstance != null)
            _schematicInstance.relinquish();
        _schematicInstance = null;

        for(Player ply : this.getPlayerVisitors())
            ply.teleport(Conf.DEFAULT_TP_POSITION.toLocation(Conf.DEFAULT_TP_WORLD.getWorld()));

    }
    
    public void markBlockBreak(int i) {

        _blocksRemaining--;

        if(!this.isResetting())
            this.validateMineState();

    }

    public void addPlayer(VyPlayer vyPlayer) {
        _mineVisitors.add(vyPlayer);

        OfflinePlayer ownerPlayer = Bukkit.getOfflinePlayer(_ownerUuid);

        Player ply = vyPlayer.getPlayer();
        if(ply != null) {

            ply.sendMessage(Conf.MINE_ENTER_MSG
                .replaceAll("%name%", ownerPlayer.getName())
                .replaceAll("%tax_rate%", TFormatter.formatPercentage(_taxLevel))
            );

        }

    }

    public void removePlayer(VyPlayer vyPlayer) {
        _mineVisitors.remove(vyPlayer);
    }

    public Collection<VyPlayer> getVisitors() {
        return _mineVisitors;
    }

    public Collection<Player> getPlayerVisitors() {
        return _mineVisitors.stream().map(VyPlayer::getPlayer).collect(Collectors.toList());
    }

    public int getBlocksRemaining() {
        return _blocksRemaining;
    }

    public boolean isResetting() {
        return _resetJob != null;
    }

    public int getTier() {
        return _tier;
    }

    public CompletableFuture<Integer> incrementTier(int amount) {

        DatabaseConnector connector = _mineManager.getDatabaseConnector();

        return connector.execute(new IncrementMineTierAction(_ownerUuid, amount)).whenComplete((val, ex) -> {

            if(ex != null) {
                ex.printStackTrace();
                return;
            }

            _tier = val;
            this.doUpgradeEffects();
            if(!this.isResetting())
                this.resetMine();

        });

    }

    public CompletableFuture<Void> setTier(int newTier) {

        DatabaseConnector connector = _mineManager.getDatabaseConnector();

        return connector.execute(new SetMineTierAction(_ownerUuid, newTier)).whenComplete((v, ex) -> {

            if(ex != null) {
                ex.printStackTrace();
                return;
            }

            _tier = newTier;

            this.doUpgradeEffects();
            if(!this.isResetting())
                this.resetMine();

        });

    }

    private void doUpgradeEffects() {

        this.broadcast(Conf.MINE_TIER_UPGRADE);
        // TODO: more effects

    }

    public MineAccessLevel getAccessLevel() {
        return _accessLevel;
    }

    public boolean isPermitted(VyPlayer ply) {

        switch(_accessLevel) {

            case PUBLIC:
                return true;
            case GANG:
                if(!PrisonHook.isHooked())
                    return ply.getUniqueId().equals( _ownerUuid);

                OfflinePlayer owner = Bukkit.getOfflinePlayer(_ownerUuid);
                Optional<Gang> gang = PrisonHook.getGangAPI().getPlayerGang(owner);

                if(!gang.isPresent())
                    return ply.getUniqueId().equals( _ownerUuid);
                return gang.get().containsPlayer(ply.getPlayer());
            case PRIVATE:
                return ply.getUniqueId().equals( _ownerUuid);
            default:
                return false;

        }

    }

    public void setAccessLevel(MineAccessLevel accessLevel) {

        DatabaseConnector connector = _mineManager.getDatabaseConnector();

        String bcMessage;
        switch(accessLevel) {
            case PUBLIC:
                bcMessage = Conf.MINE_ACCESS_LEVEL_UPDATED_PUBLIC;
                break;
            case GANG:
                bcMessage = Conf.MINE_ACCESS_LEVEL_UPDATED_GANG;
                break;
            case PRIVATE:
                bcMessage = Conf.MINE_ACCESS_LEVEL_UPDATED_PRIVATE;
                break;
            default:
                return;
        }

        _accessLevel = accessLevel;
        connector.execute(new SetMineAccessLevelAction(_ownerUuid, accessLevel));

        for(VyPlayer vyPlayer : this.getVisitors()) {

            Player ply = vyPlayer.getPlayer();
            ply.sendMessage(bcMessage);

            if(!this.isPermitted(vyPlayer)) {
                vyPlayer.resetPosition();
                ply.sendMessage(Conf.MINE_ACCESS_LEVEL_UPDATED_DISPLACED);
            }

        }
    }

    public void broadcast(String msg) {

        for(Player ply : this.getPlayerVisitors())
            ply.sendMessage(msg);

    }

    public PlayerMineManager getMineManager() {
        return _mineManager;
    }

    public MineLotteryHandler getLotteryHandler() {
        return _lotteryHandler;
    }

    public UUID getOwnerUniqueId() {
        return _ownerUuid;
    }

    public double getTaxLevel() {
        return _taxLevel;
    }

}
