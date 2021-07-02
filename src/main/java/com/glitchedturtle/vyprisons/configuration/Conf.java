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
    @ConfKey(value="generate_world", required=false)
    public static boolean MINE_WORLD_GENERATE = true; // TODO: change to false

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

    @ConfKey("mine.default_tp_world_name")
    public static SafeWorld DEFAULT_TP_WORLD = new SafeWorld("world");
    @ConfKey("mine.default_tp_position")
    public static SafeLocation DEFAULT_TP_POSITION = new SafeLocation(0, 30, 0);

    public static String INVALID_POSITION_MSG
            = ChatColor.RED + "Oops... You aren't supposed to be here!";
    public static Sound INVALID_POSITION_SOUND
            = Sound.BLOCK_BEACON_ACTIVATE;

    public static String INVALID_BLOCK_MODIFY_MSG
            = ChatColor.RED + "You can't place or break this block!";
    public static boolean INVALID_BLOCK_MODIFY_MSG_SEND_TO_CHAT = true;
    public static boolean INVALID_BLOCK_MODIFY_MSG_SEND_TO_ACTION = true;
    public static Sound INVALID_BLOCK_MODIFY_SOUND
            = Sound.ENTITY_ITEM_BREAK;

    public static String MINE_RESET_MSG
            = ChatColor.GOLD + "The mine is resetting";

    public static long MINE_RESET_DELAY
            = 10;
    public static String MINE_RESET_WARN_MSG
            = ChatColor.YELLOW + "The mine will reset in 10 seconds";

    public static double MINE_RESET_THRESHOLD = 0.99;

    @ConfKey("mine.reset.blocks_per_tick")
    public static int MINE_RESET_BLOCKS_PER_TICK = 1248;
    @ConfKey("mine.reset.max_workers")
    public static int MINE_RESET_MAX_WORKERS = 2;

    public static String MINE_ACCESS_LEVEL_UPDATED_PUBLIC
            = ChatColor.GOLD + "The mine's access level has set to public";
    public static String MINE_ACCESS_LEVEL_UPDATED_GANG
            = ChatColor.GOLD + "The mine's access level has set to gang-only";
    public static String MINE_ACCESS_LEVEL_UPDATED_PRIVATE
            = ChatColor.GOLD + "The mine's access level has set to private";
    public static String MINE_ACCESS_LEVEL_UPDATED_DISPLACED
            = ChatColor.RED + "Hence, you have been evicted from the mine";
    public static String MINE_TIER_UPGRADE
            = ChatColor.GRAY + "The mine has leveled up to " + ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "TIER %tier%!!";
    public static String MINE_LOTTERY_ENTRY
            = ChatColor.GRAY + "%name% has joined the mine's lottery";

    public static long MINE_STYLE_UPDATE_COOLDOWN = 15 * 60 * 1000;
    public static long MINE_ACCESS_UPDATE_COOLDOWN = 60 * 1000;

    public static double LOTTERY_MAX_VALUE = 15000;

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

    public static String CMD_MANAGE_NOT_MINE
            = ChatColor.RED + "You do not have a mine to manage! To create a mine, type /vyprison create";
    public static String CMD_MANAGE_MINE_FETCH_FAILED
            = ChatColor.RED + "An error occured while loading your mine, please try again later";

    public static String CMD_TIER_UPDATE_FAILED
            = ChatColor.RED + "Something went wrong while upgrading your tier";
    public static String CMD_TIER_INSUFFICIENT_BALANCE
            = ChatColor.GRAY + "You do not have sufficient balance to purchase this tier";

    public static String CMD_LOTTERY_NOT_VISITING
            = ChatColor.RED + "You are not currently visting a mine";
    public static String CMD_LOTTERY_NOT_OWNER
            = ChatColor.RED + "You are not the owner of this mine";
    public static String CMD_LOTTERY_DISABLED
            = ChatColor.RED + "This mine's lottery is currently disabled";
    public static String CMD_LOTTERY_ALREADY_ENTERED
            = ChatColor.RED + "You are already a memebr of this mine's lottery";
    public static String CMD_LOTTERY_JOINED
            = ChatColor.GREEN + "You have joined the lottery";
    public static String CMD_LOTTERY_INFO
            = ChatColor.GRAY + "The lottery is currently worth " + ChatColor.YELLOW + "$%value%"
            + ChatColor.GRAY + ", and has " + ChatColor.YELLOW + "%entry_count% participants";
}
