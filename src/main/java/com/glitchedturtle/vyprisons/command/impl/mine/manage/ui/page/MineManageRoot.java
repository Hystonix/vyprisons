package com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page;

import com.glitchedturtle.common.menu.AbstractMenuPage;
import com.glitchedturtle.common.util.ItemBuilder;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.MineManageMenu;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MineManageRoot extends AbstractMenuPage<MineManageMenu> {

    public MineManageRoot(MineManageMenu menu) {
        super(menu, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "VyPrison > " + ChatColor.RESET + "Manage your mine", 27);
    }

    @Override
    public void populatePage(Inventory inv) {

        PlayerMineInstance instance = this.getMenu().getMineInstance();

        inv.setItem(10, ItemBuilder.create(Material.END_CRYSTAL)
            .displayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Teleport to mine")
                .glowing().build()
        );

        inv.setItem(12, ItemBuilder.create(Material.BEACON)
            .displayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Mine Tiers")
            .lore(
                    "",
                    ChatColor.GRAY + "Upgrade your mine's tier",
                    ChatColor.GRAY + "to increase the rate in which valuable blocks spawn!",
                    "",
                    ChatColor.GRAY + "Current tier: " + ChatColor.YELLOW + instance.getTier(),
                    ChatColor.GRAY + "Next tier cost: " + ChatColor.GOLD + "Max"
            ).build()
        );
        inv.setItem(13, ItemBuilder.create(Material.IRON_DOOR)
            .displayName(ChatColor.BLUE + ChatColor.BOLD.toString() + "Mine Privacy")
            .lore(
                    "",
                    ChatColor.GRAY + "Configure the access setting for your mine",
                    ChatColor.GRAY + "to set which player's can visit.",
                    "",
                    ChatColor.GRAY + "Current setting: "
                            + ChatColor.YELLOW + StringUtils.capitalize(instance.getAccessLevel().toString().toLowerCase())
            ).build()
        );
        inv.setItem(14, ItemBuilder.create(Material.GOLD_INGOT)
                .displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Mine Lottery")
                .lore(
                        "",
                        ChatColor.GRAY + "View and manage the mine's lottery",
                        "",
                        ChatColor.GRAY + "Current participants: " + ChatColor.YELLOW + "0",
                        ChatColor.GRAY + "Current worth: " + ChatColor.YELLOW + "$0"
                ).build()
        );
        inv.setItem(15, ItemBuilder.create(Material.ROSE_BUSH)
                .displayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Mine Style")
                .lore(
                        "",
                        ChatColor.GRAY + "Give your mine a new style",
                        "",
                        ChatColor.GRAY + "Current style: " + ChatColor.YELLOW +
                                instance.getType().getName()
                ).build()
        );

    }

    @Override
    public void updatePage(Inventory inv) {

    }

    @Override
    public void handleClick(InventoryClickEvent ev) {

        ItemStack stack = ev.getCurrentItem();
        if(stack == null || stack.getType() == Material.AIR)
            return;

        MineManageMenu menu = this.getMenu();
        Player ply = menu.getPlayer();

        ply.playSound(ply.getEyeLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

        switch(stack.getType()) {
            case END_CRYSTAL:
                ply.closeInventory();

                VyPlayer vyPlayer = this.getMenu().getVyPlayer();
                vyPlayer.warpToMine(menu.getMineInstance());

                return;
            case IRON_DOOR:
                menu.openPage(new MinePrivacyPage(menu));
                return;
            case BEACON:
                menu.openPage(new MineTierPage(menu));
                return;
            case GOLD_INGOT:
                menu.openPage(new MineLotteryPage(menu));
                return;
            case ROSE_BUSH:
                menu.openPage(new MineStylePage(menu));
        }

    }

}
