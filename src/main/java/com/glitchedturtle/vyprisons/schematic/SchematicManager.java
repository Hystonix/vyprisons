package com.glitchedturtle.vyprisons.schematic;

import com.glitchedturtle.common.region.Region;
import com.glitchedturtle.common.util.StringParser;
import com.glitchedturtle.vyprisons.PluginStartException;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.schematic.placer.SchematicWorkerManager;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicInstance;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicPool;
import com.glitchedturtle.vyprisons.schematic.pool.action.PurgeOrphanedInstanceAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SchematicManager {

    private VyPrisonPlugin _pluginInstance;

    private SchematicWorkerManager _placerManager;

    private Map<Integer, SchematicType> _typeRegistry = new HashMap<>();
    private SchematicType _defaultType = null;

    private Map<SchematicType, SchematicPool> _poolRegistry = new HashMap<>();
    private Map<Integer, SchematicInstance> _instanceMap = new HashMap<>();

    public SchematicManager(VyPrisonPlugin pluginInstance) {

        _pluginInstance = pluginInstance;
        _placerManager = new SchematicWorkerManager(pluginInstance);

    }

    public void initialize() throws PluginStartException {

        _pluginInstance.getDatabaseConnector()
                .execute(new PurgeOrphanedInstanceAction());

        File mineDirectory = new File(_pluginInstance.getDataFolder(), "mines");
        if(!mineDirectory.exists())
            throw new PluginStartException("Schematic Manager", "Failed to load mine type data, as the mines folder does not exist!");

        File[] ymlFiles = mineDirectory.listFiles((File dir, String name) -> name.endsWith(".yml"));
        if(ymlFiles == null)
            throw new PluginStartException("Schematic Manager", "Failed to load .yml files in mines directory (for some reason)");

        if(Conf.MINE_WORLD_GENERATE) {
            Bukkit.createWorld(new WorldCreator(Conf.MINE_WORLD.getName()));
        } else if(Conf.MINE_WORLD.getWorld() == null)
            throw new PluginStartException("Schematic Manager", "Mine world does not exist");

        for(File ymlFile : ymlFiles) {

            YamlConfiguration conf;
            try {
                conf = YamlConfiguration.loadConfiguration(ymlFile);
            } catch(Exception ex) { // TODO: Be more specific on the type of exception being caught
                throw new PluginStartException(ex,
                        "Schematic Manager",
                        "Failed to parse file '" + ymlFile.getName() + "'");
            }

            SchematicType type = new SchematicType(

                    conf.getInt("id"),

                    new File(_pluginInstance.getDataFolder(), conf.getString("schematic_path")),
                    conf.getString("name"),
                    conf.getStringList("description"),

                    Material.valueOf(conf.getString("icon")),

                    StringParser.parseBlockVector(conf.getString("spawn_offset")),
                    new Region(
                            StringParser.parseBlockVector(conf.getString("mine_region_a_offset")),
                            StringParser.parseBlockVector(conf.getString("mine_region_b_offset"))
                    ),
                    new Region(
                            StringParser.parseBlockVector(conf.getString("bounding_region_a_offset")),
                            StringParser.parseBlockVector(conf.getString("bounding_region_b_offset"))
                    )

            );

            if(conf.contains("permission"))
                type.setPermissionNode(conf.getString("permission"));

            SchematicPool pool = new SchematicPool(this, _pluginInstance.getDatabaseConnector(), _placerManager, type);
            pool.initialize();

            System.out.println("[Schematic] Created mine pool for mine '" + type.getName() + "'");

            if(conf.contains("default") && conf.getBoolean("default"))
                _defaultType = type;

            _typeRegistry.put(type.getIdentifier(), type);
            _poolRegistry.put(type, pool);

        }

        if(_defaultType == null) {
            throw new PluginStartException(
                    "Schematic Manager",
                    "No default schematic defined"
            );
        }

    }

    public SchematicPool getPool(SchematicType type) {
        return _poolRegistry.get(type);
    }

    public Set<SchematicType> getRegisteredTypes() {
        return _poolRegistry.keySet();
    }

    public SchematicType getTypeById(int id) {
        return _typeRegistry.get(id);
    }

    public SchematicInstance getById(int id) {
        return _instanceMap.get(id);
    }

    public void registerInstance(SchematicInstance instance) {
        _instanceMap.put(instance.getIdentifier(), instance);
    }

    public SchematicType getDefaultType() {
        return _defaultType;
    }


}
