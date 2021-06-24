package com.glitchedturtle.vyprisons.player.mine;

import com.glitchedturtle.common.util.TAssert;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.data.DatabaseConnector;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.mine.action.IncrementMineTierAction;
import com.glitchedturtle.vyprisons.player.mine.action.SetMineTierAction;
import com.glitchedturtle.vyprisons.player.mine.reset.MineResetManager;
import com.glitchedturtle.vyprisons.player.mine.reset.MineResetWorker;
import com.glitchedturtle.vyprisons.schematic.SchematicManager;
import com.glitchedturtle.vyprisons.schematic.SchematicType;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicInstance;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicPool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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

    PlayerMineInstance(PlayerMineManager manager, UUID ownerUuid) {

        _mineManager = manager;
        _ownerUuid = ownerUuid;

    }

    void setSchematic(SchematicType type) {
        _activeSchematic = type;
    }

    void assignSchematicInstance() {

        SchematicManager manager = _mineManager.getSchematicManager();
        SchematicPool pool = manager.getPool(_activeSchematic);

        TAssert.assertTrue(pool != null, "No pool registered for given type");
        _schematicInstance = pool.reserveAvailable(this);

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
        return _mineManager.getCompositionManager()
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

    public void destroy() {

        if(_schematicInstance != null)
            _schematicInstance.relinquish();
        _schematicInstance = null;

    }
    
    public void markBlockBreak(int i) {

        _blocksRemaining--;

        if(!this.isResetting())
            this.validateMineState();

    }

    public void addPlayer(VyPlayer vyPlayer) {
        _mineVisitors.add(vyPlayer);
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

            _tier += val;

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

        });

    }

    public MineAccessLevel getAccessLevel() {
        return _accessLevel;
    }

    public boolean isPermitted(VyPlayer ply) {

        switch(_accessLevel) {

            case PUBLIC:
                return true;
            case GANG:
                return true; // TODO: Implement gang logic
            case PRIVATE:
                return ply.getUniqueId().equals( _ownerUuid);
            default:
                return false;

        }

    }

    public void setAccessLevel(MineAccessLevel accessLevel) {

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
        for(VyPlayer vyPlayer : this.getVisitors()) {

            Player ply = vyPlayer.getPlayer();
            ply.sendMessage(bcMessage);

            if(!this.isPermitted(vyPlayer)) {
                vyPlayer.resetPosition();
                ply.sendMessage(Conf.MINE_ACCESS_LEVEL_UPDATED_DISPLACED);
            }

        }

    }

}
