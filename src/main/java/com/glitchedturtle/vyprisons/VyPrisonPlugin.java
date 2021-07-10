package com.glitchedturtle.vyprisons;

import com.glitchedturtle.common.menu.MenuManager;
import com.glitchedturtle.vyprisons.command.VyPrisonCommand;
import com.glitchedturtle.vyprisons.configuration.ConfigurationInjector;
import com.glitchedturtle.vyprisons.data.DatabaseConnector;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import com.glitchedturtle.vyprisons.schematic.SchematicManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class VyPrisonPlugin extends JavaPlugin {

    private DatabaseConnector _databaseConnector;
    private MenuManager _menuManager;

    private SchematicManager _schematicManager;
    private VyPlayerManager _playerManager;

    @Override
    public void onEnable() {

        try {
            this.initializePlugin();
        } catch (PluginStartException ex) {

            System.out.println("");
            System.out.println("~~ \u2639 Failed to start VyPrisons ~~~");
            System.out.println(ex.getTitle());
            System.out.println(ex.getDescription());
            System.out.println("");

            Bukkit.getPluginManager().disablePlugin(this);

        }

    }

    @Override
    public void onDisable() {

        if(_playerManager != null)
            _playerManager.unload();

    }

    private void initializePlugin() throws PluginStartException {

        try {
            this.createFileStructure();
        } catch (IOException ex) {
            throw new PluginStartException(ex, "File Structure", "Failed to create the required file structure");
        }

        ConfigurationInjector confInjector = new ConfigurationInjector();
        confInjector.initialize(this.getConfig());

        _databaseConnector = new DatabaseConnector();
        _databaseConnector.initialize();

        _menuManager = new MenuManager(this);

        _schematicManager = new SchematicManager(this);
        _schematicManager.initialize();

        _playerManager = new VyPlayerManager(this);
        _playerManager.initialize();

        VyPrisonCommand commandHandle = new VyPrisonCommand(this);
        this.getCommand("vyprison")
                .setExecutor(commandHandle);
        this.getCommand("vyprison")
                .setTabCompleter(commandHandle);

    }

    public void createFileStructure() throws IOException {

        File dataDirectory = this.getDataFolder();

        File configFile = new File(dataDirectory, "config.yml");
        if(!configFile.exists())
            this.saveResource("config.yml", true);

        File minesDirectory = new File(dataDirectory, "mines");
        if(!minesDirectory.exists()) {

            minesDirectory.mkdir();
            this.saveResource("mines/example_mine.yml", true);

        }

    }

    public DatabaseConnector getDatabaseConnector() {
        return _databaseConnector;
    }
    public MenuManager getMenuManager() {
        return _menuManager;
    }
    public SchematicManager getSchematicManager() {
        return _schematicManager;
    }
    public VyPlayerManager getPlayerManager() {
        return _playerManager;
    }

}
