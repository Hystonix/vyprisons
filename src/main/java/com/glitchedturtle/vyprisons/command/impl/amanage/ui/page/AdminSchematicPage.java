package com.glitchedturtle.vyprisons.command.impl.amanage.ui.page;

import com.glitchedturtle.common.menu.AbstractMenuPage;
import com.glitchedturtle.common.util.ItemBuilder;
import com.glitchedturtle.vyprisons.command.impl.amanage.ui.AdminManageMenu;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.schematic.SchematicManager;
import com.glitchedturtle.vyprisons.schematic.SchematicType;
import com.glitchedturtle.vyprisons.schematic.placer.SchematicWorker;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicInstance;
import com.glitchedturtle.vyprisons.schematic.pool.SchematicPool;
import com.glitchedturtle.vyprisons.util.TFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class AdminSchematicPage extends AbstractMenuPage<AdminManageMenu> {

    private HashMap<Integer, SchematicInstance> _clickMap = new HashMap<>();

    public AdminSchematicPage(AdminManageMenu menu) {
        super(menu, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "VyPrison > " + ChatColor.DARK_GRAY + "Schematics", 54);
        this.updateOnTick(10);

    }

    @Override
    public void populatePage(Inventory inv) {

        for(int i = 0; i < 9; i++)
            inv.setItem(i, ItemBuilder.create(Material.WHITE_STAINED_GLASS_PANE).build());

        inv.setItem(0, ItemBuilder.create(Material.BLUE_BED)
            .displayName(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "... Go back")
                .build()
        );
        inv.setItem(7, ItemBuilder.create(Material.HOPPER)
            .displayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Filter")
                .build()
        );

        this.updatePage(inv);

    }

    @Override
    public void updatePage(Inventory inv) {

        _clickMap.clear();

        AdminManageMenu menu = this.getMenu();
        SchematicManager schematicManager =
                menu.getPluginInstance().getSchematicManager();


        int slot = 10;

        Set<SchematicType> types = schematicManager.getRegisteredTypes();
        for(SchematicType type : types) {

            SchematicPool pool = schematicManager.getPool(type);
            if(pool == null)
                continue;

            for(SchematicInstance instance : pool.getAll()) {

                boolean isReady = instance.getState() == SchematicInstance.InstanceState.READY;

                String name = ChatColor.GOLD.toString() + ChatColor.BOLD + type.getName()
                        + ChatColor.GRAY + " (id: #" + instance.getIdentifier() + ")";

                ArrayList<String> lore = new ArrayList<>();
                if(isReady) {

                    if(instance.isReserved()) {

                        PlayerMineInstance mineInstance;
                        lore.add(ChatColor.GREEN + "\u274C Currently in-use by %name%");

                    } else {
                        lore.add(ChatColor.GREEN + "\u2713 Available for use");
                    }

                } else {
                    lore.add(ChatColor.RED + "\u274C Not ready");
                }

                lore.add("");
                lore.add(ChatColor.GRAY + "Grid position: " + ChatColor.YELLOW + Arrays.toString(instance.getGridPosition()));
                lore.add(ChatColor.GRAY + "State: " + ChatColor.YELLOW + instance.getState().toString());
                lore.add("");
                if(instance.getState() == SchematicInstance.InstanceState.PLACING) {

                    SchematicWorker.PlaceJob placeJob = instance.getPlaceJob();
                    if(placeJob.getStatus() == SchematicWorker.JobStatus.IN_PROGRESS) {

                        lore.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "\u231B" + ChatColor.GRAY + " In progress");
                        lore.add(ChatColor.GRAY + "Progress: " + ChatColor.YELLOW + TFormatter.formatPercentage(placeJob.getProgress()));
                        lore.add(ChatColor.GRAY + "Blocks remaining: " + ChatColor.YELLOW + TFormatter.formatLargeNumber(placeJob.getBlocksLeft()));
                        lore.add(ChatColor.GRAY + "Time remaining: " + ChatColor.YELLOW + TFormatter.formatTime(placeJob.getEta()));

                    } else {
                        lore.add(ChatColor.GRAY + "In queue");
                    }

                    lore.add("");

                }

                lore.add(ChatColor.GOLD + "Left-click to teleport to instance");
                lore.add(ChatColor.RED + "Shift left-click to delete the instance");

                inv.setItem(slot,
                        ItemBuilder.create(isReady ? type.getIcon() : Material.NETHERITE_SCRAP)
                                .displayName(name)
                                .lore(lore)
                                    .build()
                );
                _clickMap.put(slot, instance);

                slot++;
                if((slot + 1) % 9 == 0)
                    slot += 2;

            }

        }

    }

    @Override
    public void handleClick(InventoryClickEvent ev) {

        ItemStack stack = ev.getCurrentItem();
        if(stack == null || stack.getType() == Material.AIR)
            return;

        AdminManageMenu menu = this.getMenu();

        Player ply = menu.getPlayer();

        if(stack.getType() == Material.BLUE_BED) {

            ply.playSound(ply.getEyeLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

            menu.openPage(new AdminRootPage(menu));
            return;

        }

        if(!_clickMap.containsKey(ev.getSlot()))
            return;
        SchematicInstance instance = _clickMap.get(ev.getSlot());

        if(instance == null)
            return;

        if(ev.getClick() == ClickType.LEFT) {

            Location warpPos = instance.getOrigin().toLocation(Conf.MINE_WORLD.getWorld());
            warpPos.add(instance.getType().getSpawnOffset());

            ply.teleport(warpPos);
            ply.closeInventory();

            ply.playSound(ply.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

        }

    }

}
