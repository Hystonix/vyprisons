package com.glitchedturtle.vyprisons.player.mine;

import com.glitchedturtle.common.util.TAssert;
import com.glitchedturtle.vyprisons.schematic.SchematicManager;
import com.glitchedturtle.vyprisons.schematic.SchematicType;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicInstance;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicPool;
import org.bukkit.Location;

import java.util.UUID;

public class PlayerMineInstance {

    private PlayerMineManager _mineManager;
    private UUID _ownerUuid;

    private SchematicType _activeSchematic;
    private SchematicInstance _schematicInstance = null;

    PlayerMineInstance(PlayerMineManager manager, UUID ownerUuid) {

        _mineManager = manager;
        _ownerUuid = ownerUuid;

    }

    void setActiveSchematic(SchematicType type) {

        SchematicManager manager = _mineManager.getSchematicManager();
        SchematicPool pool = manager.getPool(type);

        TAssert.assertTrue(pool != null, "No pool registered for given type");

        _activeSchematic = type;
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

    public SchematicType getType() {
        return _activeSchematic;
    }

    public SchematicInstance getSchematicInstance() {
        return _schematicInstance;
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

}
