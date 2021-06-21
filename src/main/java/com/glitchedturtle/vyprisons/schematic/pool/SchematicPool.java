package com.glitchedturtle.vyprisons.schematic.pool;

import com.glitchedturtle.common.util.TAssert;
import com.glitchedturtle.vyprisons.PluginStartException;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.data.DatabaseConnector;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.schematic.SchematicType;
import com.glitchedturtle.vyprisons.schematic.placer.SchematicWorkerManager;
import com.glitchedturtle.vyprisons.schematic.pool.action.CreateProtoSchematicInstanceAction;
import com.glitchedturtle.vyprisons.schematic.pool.action.FetchSchematicInstancesOfTypeAction;
import com.glitchedturtle.vyprisons.schematic.pool.action.UpdateSchematicInstanceAction;
import com.google.common.collect.Iterables;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class SchematicPool {

    private DatabaseConnector _databaseConnector;
    private SchematicWorkerManager _placerManager;

    private SchematicType _type;

    private LinkedList<SchematicInstance> _availableInstances = new LinkedList<>();
    private LinkedList<SchematicInstance> _reservedInstances = new LinkedList<>();

    public SchematicPool(DatabaseConnector databaseConnector,
                         SchematicWorkerManager placerManager,
                         SchematicType type) {

        _databaseConnector = databaseConnector;
        _placerManager = placerManager;

        _type = type;

    }

    public void validateAvailable() {

        int toCreate = Math.max(
                Conf.MINE_POOL_MIN - this.getTotalInstances(),
                Conf.MINE_POOL_MIN_AVAILABLE - _availableInstances.size()
        );

        System.out.println("[Schematic] Creating " + toCreate + " more instances of mine '" + _type.getName() + "'");
        for(int i = 0; i < toCreate; i++) this.attemptCreateInstance();

    }

    public SchematicInstance reserveAvailable(PlayerMineInstance reserveFor) {
        return this.reserveAvailable(reserveFor, true);
    }

    public SchematicInstance reserveAvailable(PlayerMineInstance reserveFor, boolean allowUnready) {

        SchematicInstance instance;

        if(_availableInstances.size() > 0) {

            instance = _availableInstances.getFirst();
            if(allowUnready && instance.getState() != SchematicInstance.InstanceState.READY)
                return null;

        } else
            instance = this.attemptCreateInstance();

        this.reserveInstance(instance, reserveFor);
        return instance;

    }

    /*
        The process of creating an instance is asynchronous by design - the steps of the task can take a significant amount of time,
        and if they were all performed in sequence all in the main server thread, under high server load, the server will probably lag...

        So, instances are stored in both the server memory (in this class) and in the database in different forms during the creation process,
        Although, all but the final state (in both the db and the server) can be considered as placeholders

     */
    SchematicInstance attemptCreateInstance() {

        TAssert.assertTrue(this.getTotalInstances() < Conf.MINE_POOL_MAX,
                    "Schematic pool is full");

        // First, a placeholder value is inserted into the available instances collection to prevent more instances from being created
        // (due to the pool size being too small)
        SchematicInstance instance = new SchematicInstance(this, _type);
        _availableInstances.add(instance);

        // Then, the instance is inserted into the database, and it's identifier (AUTO_INCREMENT in the database) is returned
        // Once the identifier has been received, the instance on the server is 'upgraded' to it's proto form, where it has an
        // identifier but it's not ready for use yet (as it hasn't been placed in the world)
        //
        // At the same time, a runnable is started that begins to place the instance in the world
        CompletableFuture<Integer> insertProtoFuture =
                _databaseConnector.execute(new CreateProtoSchematicInstanceAction(_type.getIdentifier()));

        insertProtoFuture.thenAccept(id -> instance.schedulePlace(_placerManager, id, this.createPlaceCompleteFuture(instance)));
        insertProtoFuture.exceptionally((ex) -> {

            this.relinquishInstance(instance);
            _availableInstances.remove(instance);

            ex.printStackTrace();
            return null;

        });

        return instance;
    }

    private CompletableFuture<Void> createPlaceCompleteFuture(SchematicInstance instance) {

        // TODO: Clean this up

        CompletableFuture<Void> completeFuture = new CompletableFuture<>();
        completeFuture.thenAccept((v) -> {

            CompletableFuture<Void> dbUpdate =
                    _databaseConnector.execute(new UpdateSchematicInstanceAction(instance.getIdentifier(), instance.getOrigin().toVector()));
            dbUpdate.thenAccept((v2) -> instance.flagReady());
            dbUpdate.exceptionally((ex) -> {

                ex.printStackTrace();

                this.relinquishInstance(instance);
                _availableInstances.remove(instance);

                return null;

            });

        });
        completeFuture.exceptionally((ex) -> {

            ex.printStackTrace();

            this.relinquishInstance(instance);
            _availableInstances.remove(instance);

            return null;

        });

        return completeFuture;

    }

    public void reserveInstance(SchematicInstance instance, PlayerMineInstance reserveFor) {

        TAssert.assertTrue(_availableInstances.remove(instance),
                "Instance not entry of available instances collection");
        _reservedInstances.add(instance);

        instance.reserve(reserveFor);
        this.validateAvailable();

    }

    public void relinquishInstance(SchematicInstance instance) {

        TAssert.assertTrue(_reservedInstances.remove(instance),
                "Instance not entry of reserved instances collection");
        instance.relinquish();

        _availableInstances.add(instance);
        this.validateAvailable();

    }

    public void initialize() throws PluginStartException {

        FetchSchematicInstancesOfTypeAction action = new FetchSchematicInstancesOfTypeAction(_type.getIdentifier());

        try {

            for(FetchSchematicInstancesOfTypeAction.SchematicData data : _databaseConnector.executeSync(action))
                _availableInstances.add(new SchematicInstance(this, _type, data.getId(), data.getOrigin()));

        } catch(SQLException ex) {
            throw new PluginStartException(ex, "Schematic Pool", "Failed to fetch existing schematic instances from database");
        }

        this.validateAvailable();

    }

    public Iterable<SchematicInstance> getAll() {
        return Iterables.concat(_availableInstances, _reservedInstances);
    }

    public int getTotalInstances() {
        return _availableInstances.size() + _reservedInstances.size();
    }
    public boolean hasAvailable() {
        return _availableInstances.size() > 0 || this.getTotalInstances() < Conf.MINE_POOL_MAX;
    }

}
