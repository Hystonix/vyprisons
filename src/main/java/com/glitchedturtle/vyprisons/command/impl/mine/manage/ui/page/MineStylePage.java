package com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page;

import com.glitchedturtle.common.menu.AbstractMenuPage;
import com.glitchedturtle.common.util.ItemBuilder;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.MineManageMenu;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.schematic.SchematicManager;
import com.glitchedturtle.vyprisons.schematic.SchematicType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MineStylePage extends AbstractMenuPage<MineManageMenu> {

    private Map<Integer, SchematicType> _typeMap = new HashMap<>();

    public MineStylePage(MineManageMenu menu) {
        super(menu, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "VyPrison > " + ChatColor.RESET + "Style", 54);
    }

    @Override
    public void populatePage(Inventory inv) {

        inv.setItem(0, ItemBuilder.create(Material.BLUE_BED)
                .displayName(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "... Go back")
                .build()
        );

        this.updatePage(inv);

    }

    @Override
    public void updatePage(Inventory inv) {

        SchematicManager schematicManager =
                this.getMenu().getPlugin().getSchematicManager();
        PlayerMineInstance instance = this.getMenu().getMineInstance();

        _typeMap.clear();

        int slot = 10;
        for(SchematicType type : schematicManager.getRegisteredTypes()) {

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Default tag line");
            lore.add("");
            lore.addAll(type.getDescription().stream().map(s -> ChatColor.GRAY + s).collect(Collectors.toList()));
            lore.add("");

            if(type == instance.getType()) {
                lore.add(ChatColor.GRAY + "This style is currently equipped");
            } else if(type.canAccess(this.getMenu().getPlayer())) {
                lore.add(ChatColor.GREEN + "Click to select this style");
            } else {
                lore.add(ChatColor.RED + "This style is locked");
            }

            inv.setItem(slot, ItemBuilder.create(type.getIcon())
                    .displayName(ChatColor.GOLD + ChatColor.BOLD.toString() + type.getName()
                            + ChatColor.GRAY + (type.canAccess(this.getMenu().getPlayer()) ? " (Owned)" : ""))
                    .lore(lore).setGlowing(instance.getType() == type).build()
            );

            _typeMap.put(slot, type);

            slot++;
            if((slot + 1) % 9 == 0)
                slot += 2;

        }

    }

    @Override
    public void handleClick(InventoryClickEvent ev) {

        ItemStack stack = ev.getCurrentItem();
        if (stack == null || stack.getType() == Material.AIR)
            return;

        MineManageMenu menu = this.getMenu();
        Player ply = menu.getPlayer();

        if (stack.getType() == Material.BLUE_BED) {

            menu.openPage(new MineManageRoot(menu));
            ply.playSound(ply.getEyeLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

            return;

        }

        SchematicType type = _typeMap.get(ev.getSlot());
        if(type == null)
            return;

        if(!type.canAccess(ply)) {

            ply.playSound(ply.getEyeLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return;

        }

        if(type == menu.getMineInstance().getType())
            return;

        ply.playSound(ply.getEyeLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

        PlayerMineInstance instance = this.getMenu().getMineInstance();
        instance.setType(type).whenComplete((v, ex) -> {

            this.updatePage(ev.getInventory());
            ply.playSound(ply.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        });

    }

}
