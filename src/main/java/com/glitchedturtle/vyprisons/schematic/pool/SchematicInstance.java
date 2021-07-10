package com.glitchedturtle.vyprisons.schematic.pool;

import com.glitchedturtle.common.region.Region;
import com.glitchedturtle.common.util.SafeLocation;
import com.glitchedturtle.common.util.TAssert;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.schematic.SchematicType;
import com.glitchedturtle.vyprisons.schematic.placer.SchematicWorker;
import com.glitchedturtle.vyprisons.schematic.placer.SchematicWorkerManager;
import com.glitchedturtle.vyprisons.util.ElegantPair;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;

public class SchematicInstance {

    public enum InstanceState {

        PLACEHOLDER,
        PLACING,

        READY;

    }

    private SchematicPool _pool;

    private int _id;
    private SchematicType _type;

    private SafeLocation _origin;

    private InstanceState _state;
    private SchematicWorker.PlaceJob _placeJob = null;

    private WeakReference<PlayerMineInstance> _reservedBy = null;

    SchematicInstance(SchematicPool pool, SchematicType type, int id, SafeLocation origin) {

        _pool = pool;
        _id = id;

        _type = type;
        _origin = origin;

        _state = InstanceState.READY;

    }

    SchematicInstance(SchematicPool pool, SchematicType type) {

        _pool = pool;
        _id = -1;
        _type = type;

        _state = InstanceState.PLACEHOLDER;

    }

    void schedulePlace(SchematicWorkerManager workerManager, int identifier, CompletableFuture<Void> completableFuture) {

        TAssert.assertTrue(_state == InstanceState.PLACEHOLDER,
                "Schematic not in placeholder form");

        _id = identifier;
        _state = InstanceState.PLACING;

        World world = Conf.MINE_WORLD.getWorld();
        int[] gridPos = this.getGridPosition();
        int blockSize = Conf.MINE_BLOCK_SIZE * 16;

        Location mid = new Location(world, Math.floor(gridPos[0] * blockSize + 0.5 * blockSize), 0,
                Math.floor(gridPos[1] * blockSize + 0.5 * blockSize));
        mid.add(Conf.MINE_ORIGIN.toVector());
        SafeLocation midPoint = new SafeLocation(mid);

        _placeJob = new SchematicWorker.PlaceJob(midPoint, _type, Conf.MINE_WORKER_BLOCKS_PER_TICK, Conf.MINE_WORKER_PLACES_PER_TICK, completableFuture);
        _origin = midPoint;

        try {
            workerManager.addJob(_placeJob);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }

    void flagReady() {

        TAssert.assertTrue(_state == InstanceState.PLACING, "Invalid state");
        _state = InstanceState.READY;

    }

    public int[] getGridPosition() {
        return ElegantPair.unpair(_id);
    }

    public int getIdentifier() {
        return _id;
    }

    public InstanceState getState() {
        return _state;
    }

    public SafeLocation getOrigin() {
        return _origin;
    }

    public SchematicType getType() {
        return _type;
    }

    public SchematicWorker.PlaceJob getPlaceJob() {
        return _placeJob;
    }

    public boolean isReserved() {

        if(_reservedBy == null)
            return false;
        return _reservedBy.get() != null;

    }

    public Location getWarpPosition() {
        return _origin.toLocation(Conf.MINE_WORLD.getWorld())
                .add(_type.getSpawnOffset());
    }

    public PlayerMineInstance getReservedBy() {

        if(_reservedBy == null)
            return null;
        return _reservedBy.get();

    }

    void reserve(PlayerMineInstance reserveFor) {
        _reservedBy = new WeakReference<>(reserveFor);
    }

    void relinquishInternal() {
        _reservedBy = null;
    }

    public void relinquish() {
        _pool.relinquishInstance(this);
    }

    public Region getMineRegion() {
        return _type.getMineOffset().addOffset(Conf.MINE_WORLD.getWorld(), _origin);
    }

    public Region getRegion() {
        return _type.getRegionOffset().addOffset(Conf.MINE_WORLD.getWorld(), _origin);
    }

}
