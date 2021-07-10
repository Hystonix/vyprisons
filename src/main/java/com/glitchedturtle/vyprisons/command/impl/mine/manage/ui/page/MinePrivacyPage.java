package com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page;

import com.glitchedturtle.common.menu.AbstractMenuPage;
import com.glitchedturtle.common.menu.impl.ConfirmPage;
import com.glitchedturtle.common.util.ItemBuilder;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.MineManageMenu;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.VyPlayer;
import com.glitchedturtle.vyprisons.player.mine.MineAccessLevel;
import com.glitchedturtle.vyprisons.util.TFormatter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MinePrivacyPage extends AbstractMenuPage<MineManageMenu> {

    public MinePrivacyPage(MineManageMenu menu) {
        super(menu, Conf.UI_ELEM_PRIVACY_PAGE_NAME, 27);
    }

    @Override
    public void populatePage(Inventory inv) {

        inv.setItem(0, ItemBuilder.create(Material.BLUE_BED)
                .displayName(Conf.UI_ELEM_GO_BACK_NAME)
                .build()
        );
        this.updatePage(inv);

    }

    @Override
    public void updatePage(Inventory inv) {

        MineAccessLevel level = this.getMenu().getMineInstance()
                .getAccessLevel();

        inv.setItem(12, ItemBuilder.create(Material.OAK_DOOR)
            .displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Public")
            .lore(
                    "",
                    ChatColor.GRAY + "Anyone can access your mine",
                    "", this.getTagLine(MineAccessLevel.PUBLIC)
            ).setGlowing(level == MineAccessLevel.PUBLIC).build()
        );
        inv.setItem(13, ItemBuilder.create(Material.SPRUCE_DOOR)
                .displayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Gang Only")
                .lore(
                        "",
                        ChatColor.GRAY + "Only members of your gang can access your mine",
                        "", this.getTagLine(MineAccessLevel.GANG)
                ).setGlowing(level == MineAccessLevel.GANG).build()
        );
        inv.setItem(14, ItemBuilder.create(Material.IRON_DOOR)
                .displayName(ChatColor.RED + ChatColor.BOLD.toString() + "Private")
                .lore(
                        "",
                        ChatColor.GRAY + "Only you can access your mine",
                        "", this.getTagLine(MineAccessLevel.PRIVATE)
                ).setGlowing(level == MineAccessLevel.PRIVATE).build()
        );

    }

    private String getTagLine(MineAccessLevel newLevel) {

        MineManageMenu menu = this.getMenu();

        MineAccessLevel level = menu.getMineInstance()
                .getAccessLevel();
        Player ply = menu.getPlayer();
        VyPlayer vyPlayer = menu.getVyPlayer();

        if(!ply.hasPermission(newLevel.getPermissionNode()))
            return Conf.UI_ELEM_PRIVACY_TAG_PERMISSION;
        if(newLevel == level)
            return Conf.UI_ELEM_PRIVACY_TAG_CURRENT_SETTING;
        if(vyPlayer.hasCooldown("Update privacy", false))
            return Conf.UI_ELEM_PRIVACY_TAG_COOLDOWN_PREFIX + TFormatter.formatMs(vyPlayer.getCooldown("Update privacy"));
        return Conf.UI_ELEM_PRIVACY_TAG_CALL_TO_ACTION;

    }

    @Override
    public void handleClick(InventoryClickEvent ev) {

        ItemStack stack = ev.getCurrentItem();
        if(stack == null || stack.getType() == Material.AIR)
            return;

        MineManageMenu menu = this.getMenu();
        Player ply = menu.getPlayer();

        if(stack.getType() == Material.BLUE_BED) {

            menu.openPage(new MineManageRoot(menu));
            ply.playSound(ply.getEyeLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

            return;

        }

        VyPlayer vyPlayer = this.getMenu().getVyPlayer();
        if(vyPlayer.hasCooldown("Update privacy"))
            return;

        MineAccessLevel newLevel;
        switch(stack.getType()) {
            case OAK_DOOR:
                newLevel = MineAccessLevel.PUBLIC;
                break;
            case SPRUCE_DOOR:
                newLevel = MineAccessLevel.GANG;
                break;
            case IRON_DOOR:
                newLevel = MineAccessLevel.PRIVATE;
                break;
            default:
                return;
        }

        MineAccessLevel level = menu.getMineInstance()
                .getAccessLevel();
        if(level == newLevel)
            return;

        if(!ply.hasPermission(newLevel.getPermissionNode())) {

            ply.playSound(ply.getEyeLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
            return;

        }

        menu.openPage(new ConfirmPage<>(
                menu,
                Conf.UI_ELEM_PRIVACY_UPDATE_CONFIRM_TITLE + newLevel.name(),
                Conf.UI_ELEM_PRIVACY_UPDATE_CONFIRM_DESCRIPTION,
                false, (res) -> {

            if (res && !vyPlayer.cooldown("Update privacy", Conf.MINE_ACCESS_UPDATE_COOLDOWN))
                menu.getMineInstance().setAccessLevel(newLevel);

            menu.openPage(this);

        }
        ));

        ply.playSound(ply.getEyeLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        this.updatePage(ev.getInventory());

    }

}
