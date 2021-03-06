package com.glitchedturtle.vyprisons.command.impl.amanage.ui.page;

import com.glitchedturtle.common.menu.AbstractMenuPage;
import com.glitchedturtle.common.util.ItemBuilder;
import com.glitchedturtle.common.util.StringParser;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import com.glitchedturtle.vyprisons.command.impl.amanage.ui.AdminManageMenu;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.VyPlayerManager;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineManager;
import com.glitchedturtle.vyprisons.util.TFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMinePage extends AbstractMenuPage<AdminManageMenu> {

    private Map<Integer, PlayerMineInstance> _clickMap = new HashMap<>();

    public AdminMinePage(AdminManageMenu menu) {
        super(menu, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "VyPrison > " + ChatColor.DARK_GRAY + "Mine Instances", 54);
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

        VyPlayerManager playerManager = this.getMenu().getPluginInstance()
                .getPlayerManager();
        _clickMap.clear();

        int slot = 10;
        for(VyPlayer vyPlayer : playerManager.getCachedPlayers()) {

            PlayerMineInstance instance = vyPlayer.getCachedMine();
            if(instance == null)
                continue;

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + instance.getType().getName());
            lore.add("");

            if(instance.getSchematicInstance() != null) {

                String perc = TFormatter.formatPercentage(instance.getBlocksRemaining() * 1.0d /
                        instance.getSchematicInstance().getMineRegion().getVolume());
                lore.add(ChatColor.GRAY + "Blocks remaining: " + ChatColor.YELLOW + instance.getBlocksRemaining() + " (" + perc + ")");
                if (instance.isResetting())
                    lore.add(ChatColor.GOLD + "\u23F1 Mine reset queued or in-progress");


            } else {
                lore.add(ChatColor.GRAY + "Mine instance not ready");
            }

            lore.add("");
            lore.add(ChatColor.GRAY + "Current visitors: " + ChatColor.YELLOW + instance.getVisitors().size());
            lore.add("");
            lore.add(ChatColor.GOLD + "Left-click to teleport to instance");
            lore.add(ChatColor.RED + "Right-click to unload the instance");

            inv.setItem(slot, ItemBuilder.head(vyPlayer.getUniqueId())
                    .displayName(ChatColor.GOLD.toString() + ChatColor.BOLD + vyPlayer.getName() + "'s Mine")
                    .lore(lore)
                    .build()
            );

            _clickMap.put(slot, instance);

            slot++;
            if((slot + 1) % 9 == 0)
                slot += 2;

        }

    }

    @Override
    public void handleClick(InventoryClickEvent ev) {

        ItemStack stack = ev.getCurrentItem();
        if(stack == null || stack.getType() == Material.AIR)
            return;

        AdminManageMenu menu = this.getMenu();

        Player ply = menu.getPlayer();
        VyPlayer vyPlayer = menu.getVyPlayer();

        if(stack.getType() == Material.BLUE_BED) {

            ply.playSound(ply.getEyeLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

            menu.openPage(new AdminRootPage(menu));
            return;

        }

        PlayerMineInstance instance = _clickMap.get(ev.getSlot());
        if(instance == null)
            return;

        ClickType action = ev.getClick();
        if(action == ClickType.LEFT) {

            vyPlayer.warpToMine(instance);
            return;

        }
        if(action == ClickType.RIGHT) {

            VyPlayer ownerPlayer = menu.getPluginInstance().getPlayerManager()
                    .getCachedPlayer(instance.getOwnerUniqueId());

            if(ownerPlayer != null)
                ownerPlayer.unload();
            else
                instance.unload();

        }

        this.updatePage(ev.getClickedInventory());

    }

}
