package com.glitchedturtle.vyprisons.configuration;

import com.glitchedturtle.common.util.SafeLocation;
import com.glitchedturtle.common.util.SafeWorld;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.List;

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
    public static int MINE_WORKER_BLOCKS_PER_TICK = 1024;
    @ConfKey("minepool.worker.place_per_tick")
    public static int MINE_WORKER_PLACES_PER_TICK = 128;

    @ConfKey("mine.default_tp_world_name")
    public static SafeWorld DEFAULT_TP_WORLD = new SafeWorld("world");
    @ConfKey("mine.default_tp_position")
    public static SafeLocation DEFAULT_TP_POSITION = new SafeLocation(0, 30, 0);

    @ConfKey("mine.invalid_position_msg")
    public static String INVALID_POSITION_MSG
            = ChatColor.RED + "Oops... You aren't supposed to be here!";
    @ConfKey("mine.invalid_position_privacy_msg")
    public static String INVALID_POSITION_PRIVACY_MSG
            = ChatColor.RED + "Yikes! You dont have permission to be here!";
    @ConfKey("mine.invalid_position_sound")
    public static Sound INVALID_POSITION_SOUND
            = Sound.BLOCK_BEACON_ACTIVATE;

    @ConfKey("mine.invalid_block_break_msg")
    public static String INVALID_BLOCK_MODIFY_MSG
            = ChatColor.RED + "You can't place or break this block!";
    @ConfKey("mine.invalid_block_break_msg_send_to_chat")
    public static boolean INVALID_BLOCK_MODIFY_MSG_SEND_TO_CHAT = true;
    @ConfKey("mine.invalid_block_break_msg_send_to_action")
    public static boolean INVALID_BLOCK_MODIFY_MSG_SEND_TO_ACTION = true;
    @ConfKey("mine.invalid_block_break_sound")
    public static Sound INVALID_BLOCK_MODIFY_SOUND
            = Sound.ENTITY_ITEM_BREAK;

    @ConfKey("mine.reset.reset_msg")
    public static String MINE_RESET_MSG
            = ChatColor.GOLD + "The mine is resetting";
    @ConfKey("mine.reset.reset_delay")
    public static long MINE_RESET_DELAY
            = 10;

    @ConfKey("mine.reset.reset_threshold")
    public static double MINE_RESET_THRESHOLD = 0.99;

    @ConfKey("mine.reset.blocks_per_tick")
    public static int MINE_RESET_BLOCKS_PER_TICK = 1248;
    @ConfKey("mine.reset.max_workers")
    public static int MINE_RESET_MAX_WORKERS = 2;

    @ConfKey("cooldown.execute_command")
    public static long EXECUTE_COMMAND_COOLDOWN = 1000;

    @ConfKey("cooldown.style_update")
    public static long MINE_STYLE_UPDATE_COOLDOWN = 15 * 60 * 1000;
    @ConfKey("cooldown.access_update")
    public static long MINE_ACCESS_UPDATE_COOLDOWN = 60 * 1000;
    @ConfKey("cooldown.lottery_join")
    public static long MINE_LOTTERY_JOIN_COOLDOWN = 60 * 1000;
    @ConfKey("cooldown.lottery_roll")
    public static long MINE_LOTTERY_ROLL_COOLDOWN = 15 * 1000;

    @ConfKey("mine.lottery.max_value")
    public static double LOTTERY_MAX_VALUE = 15000;
    @ConfKey("mine.lottery.min_value")
    public static double LOTTERY_MIN_VALUE = 10;
    @ConfKey("mine.lottery.min_entries")
    public static int LOTTERY_MIN_ENTRIES = 2;

    @ConfKey("mine.tax.min_value")
    public static double TAX_MIN = 0;
    @ConfKey("mine.tax.max_value")
    public static double TAX_MAX = 0.3;

    @ConfKey("mine.tax.lottery_mult")
    public static double TAX_LOTTERY = 0.10;
    @ConfKey("mine.tax.to_owner_mult")
    public static double TAX_TO_OWNER = 0.40;

    @ConfKey("mine_message.access_level_public")
    public static String MINE_ACCESS_LEVEL_UPDATED_PUBLIC
            = ChatColor.GOLD + "The mine's access level has set to public";
    @ConfKey("mine_message.access_level_gang")
    public static String MINE_ACCESS_LEVEL_UPDATED_GANG
            = ChatColor.GOLD + "The mine's access level has set to gang-only";
    @ConfKey("mine_message.access_level_private")
    public static String MINE_ACCESS_LEVEL_UPDATED_PRIVATE
            = ChatColor.GOLD + "The mine's access level has set to private";
    @ConfKey("mine_message.access_update_displaced")
    public static String MINE_ACCESS_LEVEL_UPDATED_DISPLACED
            = ChatColor.RED + "Hence, you have been evicted from the mine";

    @ConfKey("mine_message.tier_upgrade")
    public static String MINE_TIER_UPGRADE
            = ChatColor.GRAY + "The mine has leveled up to " + ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "TIER %tier%!!";
    @ConfKey("mine_message.lottery_entry")
    public static String MINE_LOTTERY_ENTRY
            = ChatColor.GRAY + "%name% has joined the mine's lottery";
    @ConfKey("mine_message.lottery_winners")
    public static String MINE_LOTTERY_WINNERS
            = ChatColor.GRAY + "The wheel has spun! " + ChatColor.YELLOW + "%winners%"
                + ChatColor.GRAY + " have each won "
                + ChatColor.YELLOW + "$%prize%";
    @ConfKey("mine_message.lottery_winner_msg")
    public static String MINE_LOTTERY_WINNER_MSG
            = ChatColor.GOLD + "You have won $%prize% from %name%'s lottery";
    @ConfKey("mine_message.mine_enter")
    public static String MINE_ENTER_MSG
            = ChatColor.GRAY + "Welcome to " + ChatColor.YELLOW + "%name%'s "
            + ChatColor.GRAY + " mine! Current tax rate: " + ChatColor.YELLOW + "%tax_rate%";

    @ConfKey("cmd_message.missing_permission")
    public static String CMD_MISSING_PERMISSION
            = ChatColor.RED + "You lack the required permissions to execute this command";
    @ConfKey("cmd_message.failed_profile_fetch")
    public static String CMD_FAILED_PROFILE_FETCH
            = ChatColor.RED + "Failed to fetch your prison profile. Please try again shortly.";

    @ConfKey("cmd_message.teleport_target_not_found")
    public static String CMD_TELEPORT_TARGET_NOT_FOUND
            = ChatColor.RED + "Failed to find the player '%name%', are they online?";
    @ConfKey("cmd_message.teleport_no_mine")
    public static String CMD_TELEPORT_NO_MINE
            = ChatColor.RED + "You do not have a mine to teleport to! To create a mine, type /vyprison create";
    @ConfKey("cmd_message.teleport_no_mine_other")
    public static String CMD_TELEPORT_NO_MINE_OTHER
            = ChatColor.RED + "%name% do not have a mine to teleport to!";
    @ConfKey("cmd_message.teleport_target_failed_fetch")
    public static String CMD_TELEPORT_TARGET_FAILED_FETCH
            = ChatColor.RED + "Failed to load the mine, please try again later.";
    @ConfKey("cmd_message.teleport_place_in_progress")
    public static String CMD_TELEPORT_PLACE_IN_PROGRESS
            = ChatColor.RED + "Please wait while your mine is placed in the world";
    @ConfKey("cmd_message.teleport_privacy")
    public static String CMD_TELEPORT_PRIVACY
            = ChatColor.RED + "You can not access this mine at this time";
    @ConfKey("cmd_message.teleport_success")
    public static String CMD_TELEPORT_SUCCESS
            = ChatColor.GREEN + "You have teleported to your mine!";
    @ConfKey("cmd_message.teleport_success_other")
    public static String CMD_TELEPORT_SUCCESS_OTHER
            = ChatColor.GREEN + "You have teleported to %name%'s mine!";
    @ConfKey("cmd_message.teleport_success_sound")
    public static Sound CMD_TELEPORT_SOUND
            = Sound.ENTITY_ENDERMAN_TELEPORT;

    @ConfKey("cmd_message.create_failed")
    public static String CMD_CREATE_FAILED
            = ChatColor.RED + "An error occurred during the creation of your mine, please try again later";
    @ConfKey("cmd_message.create_already_owner")
    public static String CMD_CREATE_ALREADY_OWNER
            = ChatColor.RED + "You can not create another mine!";
    @ConfKey("cmd_message.create_success")
    public static String CMD_CREATE_SUCCESS
            = ChatColor.GREEN + "Created mine";
    @ConfKey("cmd_message.create_place_in_progress")
    public static String CMD_CREATE_PLACE_IN_PROGRESS;
    @ConfKey("cmd_message.create_success_sound")
    public static Sound CMD_CREATE_SUCCESS_SOUND
            = Sound.ENTITY_PLAYER_LEVELUP;

    @ConfKey("cmd_message.manage_no_mine")
    public static String CMD_MANAGE_NOT_MINE
            = ChatColor.RED + "You do not have a mine to manage! To create a mine, type /vyprison create";
    @ConfKey("cmd_message.manage_fetch_failed")
    public static String CMD_MANAGE_FETCH_FAILED
            = ChatColor.RED + "An error occurred while loading your mine, please try again later";

    @ConfKey("cmd_message.tier_update_failed")
    public static String CMD_TIER_UPDATE_FAILED
            = ChatColor.RED + "Something went wrong while upgrading your tier";
    @ConfKey("cmd_message.tier_insufficient_balance")
    public static String CMD_TIER_INSUFFICIENT_BALANCE
            = ChatColor.GRAY + "You do not have sufficient balance to purchase this tier";

    @ConfKey("cmd_message.lottery_not_visiting")
    public static String CMD_LOTTERY_NOT_VISITING
            = ChatColor.RED + "You are not currently visiting a mine";
    @ConfKey("cmd_message.lottery_not_owner")
    public static String CMD_LOTTERY_NOT_OWNER
            = ChatColor.RED + "You are not the owner of this mine";
    @ConfKey("cmd_message.lottery_disabled")
    public static String CMD_LOTTERY_DISABLED
            = ChatColor.RED + "This mine's lottery is currently disabled";
    @ConfKey("cmd_message.lottery_already_entered")
    public static String CMD_LOTTERY_ALREADY_ENTERED
            = ChatColor.RED + "You are already a member of this mine's lottery";
    @ConfKey("cmd_message.lottery_joined")
    public static String CMD_LOTTERY_JOINED
            = ChatColor.GREEN + "You have joined the lottery";
    @ConfKey("cmd_message.lottery_min_entries")
    public static String CMD_LOTTERY_MIN_ENTRIES
            = ChatColor.RED + "The lottery can only be rolled after 5 people have joined";
    @ConfKey("cmd_message.lottery_min_value")
    public static String CMD_LOTTERY_MIN_VALUE
            = ChatColor.RED + "The lottery can only be rolled after it's worth has reached at least $10";
    @ConfKey("cmd_message.lottery_info")
    public static String CMD_LOTTERY_INFO
            = ChatColor.GRAY + "The lottery is currently worth " + ChatColor.YELLOW + "$%value%"
            + ChatColor.GRAY + ", and has " + ChatColor.YELLOW + "%entry_count% participants";

    @ConfKey("cmd_message.unload_not_found")
    public static String CMD_UNLOAD_NOT_LOADED
            = ChatColor.RED + "The mine of this player is not currently loaded";
    @ConfKey("cmd_message.unload_target_not_exist")
    public static String CMD_UNLOAD_TARGET_NOT_EXIST
            = ChatColor.RED + "Failed to find player under name '%name%'";
    @ConfKey("cmd_message.unload_success")
    public static String CMD_UNLOAD_SUCCESS
            = ChatColor.GREEN + "The mine has been unloaded";

    @ConfKey("cmd_message.privacy_invalid_setting")
    public static String CMD_PRIVACY_INVALID_SETTING
            = ChatColor.RED + "Invalid option! Available: Public, gang, private";
    @ConfKey("cmd_message.privacy_success")
    public static String CMD_PRIVACY_SUCCESS
            = ChatColor.GREEN + "Mine privacy set to %privacy%";
    @ConfKey("cmd_message.privacy_insufficient_permission")
    public static String CMD_PRIVACY_INSUFFICIENT_PERMISSION
            = ChatColor.RED + "You lack the required permission to change your mine to %privacy_level%";

    @ConfKey("cmd_message.reset_target_not_found")
    public static String CMD_RESET_TARGET_NOT_FOUND
            = ChatColor.RED + "Failed to find player under name '%name%'";
    @ConfKey("cmd_message.reset_success")
    public static String CMD_RESET_SUCCESS
            = ChatColor.GREEN + "The mine has been reset";

    @ConfKey("cmd_message.tax_info")
    public static String CMD_TAX_INFO
            = ChatColor.GRAY + "The current tax level of %name%'s mine is %tax%";
    @ConfKey("cmd_message.tax_not_visiting")
    public static String CMD_TAX_NOT_VISITING
            = ChatColor.RED + "You are not currently visiting a mine";
    @ConfKey("cmd_message.tax_error_fetch")
    public static String CMD_TAX_ERROR_FETCH
            = ChatColor.RED + "An error occurred while loading your mine";
    @ConfKey("cmd_message.tax_not_owned")
    public static String CMD_TAX_NOT_OWNED
            = ChatColor.RED + "You do not own a mine";
    @ConfKey("cmd_message.tax_input_invalid")
    public static String CMD_TAX_INVALID
            = ChatColor.RED + "'%input%' is not a valid tax level!";
    @ConfKey("cmd_message.tax_update_success")
    public static String CMD_TAX_UPDATE_SUCCESS
            = ChatColor.GREEN + "Updated your mine's tax level to %tax_level%";

    @ConfKey("ui_elem.confirm_btn_name")
    public static String UI_ELEM_CONFIRM_BTN;
    @ConfKey("ui_elem.cancel_btn_name")
    public static String UI_ELEM_CANCEL_BTN;

    @ConfKey("ui_elem.root_page_name")
    public static String UI_ELEM_ROOT_PAGE_NAME;
    @ConfKey("ui_elem.lottery_page_name")
    public static String UI_ELEM_LOTTERY_PAGE_NAME;
    @ConfKey("ui_elem.privacy_page_name")
    public static String UI_ELEM_PRIVACY_PAGE_NAME;
    @ConfKey("ui_elem.style_page_name")
    public static String UI_ELEM_STYLE_PAGE_NAME;
    @ConfKey("ui_elem.tier_page_name")
    public static String UI_ELEM_TIER_PAGE_NAME;

    @ConfKey("ui_elem.go_back_name")
    public static String UI_ELEM_GO_BACK_NAME;
    @ConfKey("ui_elem.teleport_btn_name")
    public static String UI_ELEM_TELEPORT_BTN_NAME;

    @ConfKey("ui_elem.tier_btn_name")
    public static String UI_ELEM_TIER_BTN_NAME;
    @ConfKey("ui_elem.tier_btn_description")
    public static List<String> UI_ELEM_TIER_BTN_DESCRIPTION;
    @ConfKey("ui_elem.privacy_btn_name")
    public static String UI_ELEM_PRIVACY_BTN_NAME;
    @ConfKey("ui_elem.privacy_btn_description")
    public static List<String> UI_ELEM_PRIVACY_BTN_DESCRIPTION;
    @ConfKey("ui_elem.lottery_btn_name")
    public static String UI_ELEM_LOTTERY_BTN_NAME;
    @ConfKey("ui_elem.lottery_btn_description")
    public static List<String> UI_ELEM_LOTTERY_BTN_DESCRIPTION;
    @ConfKey("ui_elem.style_btn_name")
    public static String UI_ELEM_STYLE_BTN_NAME;
    @ConfKey("ui_elem.style_btn_description")
    public static List<String> UI_ELEM_STYLE_BTN_DESCRIPTION;

    @ConfKey("ui_elem.privacy_tag_permission")
    public static String UI_ELEM_PRIVACY_TAG_PERMISSION;
    @ConfKey("ui_elem.privacy_tag_current_setting")
    public static String UI_ELEM_PRIVACY_TAG_CURRENT_SETTING;
    @ConfKey("ui_elem.privacy_tag_cooldown_prefix")
    public static String UI_ELEM_PRIVACY_TAG_COOLDOWN_PREFIX;
    @ConfKey("ui_elem.privacy_tag_call_to_action")
    public static String UI_ELEM_PRIVACY_TAG_CALL_TO_ACTION;

    @ConfKey("ui_elem.privacy_update_confirm_title")
    public static String UI_ELEM_PRIVACY_UPDATE_CONFIRM_TITLE;
    @ConfKey("ui_elem.privacy_update_confirm_description")
    public static List<String> UI_ELEM_PRIVACY_UPDATE_CONFIRM_DESCRIPTION;

    @ConfKey("cooldown_message")
    public static String COOLDOWN_MESSAGE
            = "Missing COOLDOWN_MESSAGE";

}
