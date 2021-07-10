package com.glitchedturtle.vyprisons.player.mine;

import com.glitchedturtle.vyprisons.PluginStartException;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.data.DatabaseConnector;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import com.glitchedturtle.vyprisons.player.mine.action.CreateMineInstanceAction;
import com.glitchedturtle.vyprisons.player.mine.action.FetchMineInstanceAction;
import com.glitchedturtle.vyprisons.player.mine.listener.MineManipulationHandler;
import com.glitchedturtle.vyprisons.player.mine.listener.PlayerMoveHandler;
import com.glitchedturtle.vyprisons.player.mine.listener.PlayerPositionHandler;
import com.glitchedturtle.vyprisons.player.mine.listener.SellAllHandler;
import com.glitchedturtle.vyprisons.player.mine.reset.MineResetManager;
import com.glitchedturtle.vyprisons.schematic.SchematicManager;
import com.glitchedturtle.vyprisons.schematic.SchematicType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerMineManager {

    private DatabaseConnector _databaseConnector;
    private SchematicManager _schematicManager;

    private MineResetManager _resetManager;
    private MineTierManager _tierManager;

    private MineManipulationHandler _mineManipulationHandler;
    private PlayerPositionHandler _minePositionHandler;
    private SellAllHandler _sellAllHandler;
    private PlayerMoveHandler _moveHandler;

    public PlayerMineManager(VyPrisonPlugin plugin, VyPlayerManager playerManager) {

        _databaseConnector = plugin.getDatabaseConnector();
        _schematicManager = plugin.getSchematicManager();

        _resetManager = new MineResetManager(plugin);

        _tierManager = new MineTierManager();

        _mineManipulationHandler = new MineManipulationHandler(playerManager);
        _minePositionHandler = new PlayerPositionHandler(_schematicManager, playerManager);
        _sellAllHandler = new SellAllHandler(playerManager);
        _moveHandler = new PlayerMoveHandler(playerManager);

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(_mineManipulationHandler, plugin);
        pm.registerEvents(_minePositionHandler, plugin);
        pm.registerEvents(_sellAllHandler, plugin);
        pm.registerEvents(_moveHandler, plugin);

    }

    public void initialize(ConfigurationSection section) throws PluginStartException {
        _tierManager.loadConfiguration(section);
    }

    public CompletableFuture<PlayerMineInstance> loadMine(UUID uuid) {

        CompletableFuture<FetchMineInstanceAction.Response> dbFuture
                = _databaseConnector.execute(new FetchMineInstanceAction(uuid));

        return dbFuture.thenApply(res -> {

            if(!res.doesExist())
                return null;

            SchematicType type = _schematicManager.getTypeById(res.getActiveSchematicId());
            if(type == null)
                type = _schematicManager.getDefaultType();

            PlayerMineInstance instance = new PlayerMineInstance(this, uuid, res);
            instance.assignSchematicInstance();

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

            instance.setSchematic(type);
            instance.assignSchematicInstance();

            return instance;

        });

    }

    SchematicManager getSchematicManager() {
        return _schematicManager;
    }
    MineResetManager getResetManager() { return _resetManager; }

    public MineTierManager getTierManager() {
        return _tierManager;
    }

    DatabaseConnector getDatabaseConnector() {
        return _databaseConnector;
    }

}
