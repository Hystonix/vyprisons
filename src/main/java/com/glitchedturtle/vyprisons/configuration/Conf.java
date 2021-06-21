package com.glitchedturtle.vyprisons.configuration;

import com.glitchedturtle.common.util.SafeLocation;
import com.glitchedturtle.common.util.SafeWorld;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

public class Conf {

    @ConfKey(value="db.min_idle", required=false)
    public static int DB_MIN_IDLE = 3;
    @ConfKey(value="db.max_pool_size", required=false)
    public static int DB_MAX_POOL_SIZE = 5;

    @ConfKey(value="db.source_class_name", required=false)
    public static String DB_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
    @ConfKey("db.server_name")
    public static String DB_SERVER_NAME = "127.0.0.1";
    @ConfKey("db.server_port")
    public static int DB_SERVER_PORT = 3306;
    @ConfKey("db.database_name")
    public static String DB_DATABASE_NAME = "prison";
    @ConfKey("db.username")
    public static String DB_AUTH_USERNAME = "root";
    @ConfKey("db.password")
    public static String DB_AUTH_PASSWORD = "pass";

    @ConfKey("minepool.world_name")
    public static SafeWorld MINE_WORLD = new SafeWorld("world");
    @ConfKey("minepool.origin")
    public static SafeLocation MINE_ORIGIN = new SafeLocation(0, 30, 0);
    @ConfKey(value="minepool.block_size", required=false)
    public static int MINE_BLOCK_SIZE = 16;
    @ConfKey(value="minepool.block_module_size", required=false)
    public static int MINE_BLOCK_MODULE_SIZE = 3;

    @ConfKey("minepool.min_instances")
    public static int MINE_POOL_MIN = 4;
    @ConfKey("minepool.min_available_instances")
    public static int MINE_POOL_MIN_AVAILABLE = 2;
    @ConfKey("minepool.max_instances")
    public static int MINE_POOL_MAX = 16;

    @ConfKey("minepool.worker.max_instances")
    public static int MINE_WORKER_MAX_INSTANCES = 2;
    @ConfKey("minepool.worker.blocks_per_tick")
    public static int MINE_WORKER_BLOCKS_PER_TICK = 16;
    @ConfKey("minepool.worker.burst_blocks_per_tick")
    public static int MINE_WORKER_BURST_BLOCKS_PER_TICK = 1248;

    public static String CMD_MISSING_PERMISSION
            = ChatColor.RED + "You lack the required permissions to execute this command";
    public static String CMD_FAILED_PROFILE_FETCH
            = ChatColor.RED + "Failed to fetch your prison profile. Please try again shortly.";

    public static String CMD_TELEPORT_TARGET_NOT_FOUND
            = ChatColor.RED + "Failed to find the player '%name%', are they online?";
    public static String CMD_TELEPORT_NO_MINE
            = ChatColor.RED + "You do not have a mine to teleport to! To create a mine, type /vyprison create";
    public static String CMD_TELEPORT_NO_MINE_OTHER
            = ChatColor.RED + "You do not have a mine to teleport to! To create a mine, type /vyprison create";
    public static String CMD_TELEPORT_TARGET_FAILED_FETCH
            = ChatColor.RED + "Failed to load the mine, please try again later.";
    public static String CMD_TELEPORT_PLACE_IN_PROGRESS
            = ChatColor.RED + "Please wait while your mine is placed in the world";
    public static String CMD_TELEPORT_SUCCESS
            = ChatColor.GREEN + "You have teleported to your mine!";
    public static String CMD_TELEPORT_SUCCESS_OTHER
            = ChatColor.GREEN + "You have teleported to %name%'s mine!";
    public static Sound CMD_TELEPORT_SOUND
            = Sound.ENTITY_ENDERMAN_TELEPORT;

    public static String CMD_CREATE_FAILED
            = ChatColor.RED + "An error occured during the creation of your mine, please try again later";
    public static String CMD_CREATE_ALREADY_OWNER
            = ChatColor.RED + "You can not create another mine!";
    public static String CMD_CREATE_SUCCESS
            = ChatColor.GREEN + "Created mine";
    public static Sound CMD_CREATE_SUCCESS_SOUND
            = Sound.ENTITY_PLAYER_LEVELUP;


}
