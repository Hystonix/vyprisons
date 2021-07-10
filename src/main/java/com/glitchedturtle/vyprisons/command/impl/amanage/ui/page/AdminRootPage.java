package com.glitchedturtle.vyprisons.command.impl.amanage.ui.page;

import com.glitchedturtle.common.menu.AbstractMenuPage;
import com.glitchedturtle.common.util.ItemBuilder;
import com.glitchedturtle.vyprisons.command.impl.amanage.ui.AdminManageMenu;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AdminRootPage extends AbstractMenuPage<AdminManageMenu> {

    public AdminRootPage(AdminManageMenu menu) {
        super(menu, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "VyPrison > " + ChatColor.DARK_GRAY + "Admin", 27);
    }

    @Override
    public void populatePage(Inventory inv) {

        /*inv.setItem(10, ItemBuilder.create(Material.PLAYER_HEAD)
                .displayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Manage Players")
                .lore(
                    "",
                    ChatColor.GRAY + "Manage player data, including their personal mine"
                ).build()
        );*/
        inv.setItem(10, ItemBuilder.create(Material.IRON_BARS)
                .displayName(ChatColor.BLUE.toString() + ChatColor.BOLD + "Manage Active Mines")
                .lore(
                    "",
                    ChatColor.GRAY + "View and manage all active mines"
                ).build()
        );
        inv.setItem(11, ItemBuilder.create(Material.FILLED_MAP)
                .displayName(ChatColor.RED.toString() + ChatColor.BOLD + "Manage Schematic Instances")
                .lore(
                    "",
                    ChatColor.GRAY + "Manage schematic instances"
                ).build()
        );

    }

    @Override
    public void updatePage(Inventory inv) {
        inv.clear();
        this.populatePage(inv);
    }

    @Override
    public void handleClick(InventoryClickEvent ev) {

        ItemStack stack = ev.getCurrentItem();
        if(stack == null || stack.getType() == Material.AIR)
            return;

        AdminManageMenu menu = this.getMenu();
        Player ply = menu.getPlayer();

        ply.playSound(ply.getEyeLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

        switch(stack.getType()) {
            case PLAYER_HEAD:
                break;
            case IRON_BARS:
                menu.openPage(new AdminMinePage(menu));
                break;
            case FILLED_MAP:
                menu.openPage(new AdminSchematicPage(menu));
                break;
        }

    }

}
