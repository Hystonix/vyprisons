package com.glitchedturtle.vyprisons.player.mine;

import com.glitchedturtle.vyprisons.data.DatabaseConnector;
import com.glitchedturtle.vyprisons.player.mine.action.CreateMineInstanceAction;
import com.glitchedturtle.vyprisons.player.mine.action.FetchMineInstanceAction;
import com.glitchedturtle.vyprisons.schematic.SchematicManager;
import com.glitchedturtle.vyprisons.schematic.SchematicType;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerMineManager {

    private DatabaseConnector _databaseConnector;
    private SchematicManager _schematicManager;

    public PlayerMineManager(DatabaseConnector databaseConnector, SchematicManager schematicManager) {
        _databaseConnector = databaseConnector;
        _schematicManager = schematicManager;
    }

    public CompletableFuture<PlayerMineInstance> loadMine(UUID uuid) {

        CompletableFuture<FetchMineInstanceAction.Response> dbFuture
                = _databaseConnector.execute(new FetchMineInstanceAction(uuid));

        return dbFuture.thenApply(res -> {

            if(!res.doesExist())
                return null;

            SchematicType type = _schematicManager.getById(res.getActiveSchematicId());
            if(type == null)
                type = _schematicManager.getDefaultType();

            PlayerMineInstance instance = new PlayerMineInstance(this, uuid);
            instance.setActiveSchematic(type);

            return instance;

        });

    }

    public CompletableFuture<PlayerMineInstance> createMine(UUID uuid, SchematicType type) {

        CompletableFuture<Boolean> dbFuture
                = _databaseConnector.execute(new CreateMineInstanceAction(uuid, type));

        return dbFuture.thenApply(success -> {

            if(!success)
                return null;

            PlayerMineInstance instance = new PlayerMineInstance(this, uuid);
            instance.setActiveSchematic(type);

            return instance;

        });

    }

    SchematicManager getSchematicManager() {
        return _schematicManager;
    }

}
