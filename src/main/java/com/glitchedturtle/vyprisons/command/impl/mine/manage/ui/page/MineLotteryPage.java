package com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page;

import com.glitchedturtle.common.menu.AbstractMenuPage;
import com.glitchedturtle.common.util.ItemBuilder;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.MineManageMenu;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.player.mine.lottery.MineLotteryHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MineLotteryPage extends AbstractMenuPage<MineManageMenu> {

    public MineLotteryPage(MineManageMenu menu) {
        super(menu, Conf.UI_ELEM_LOTTERY_PAGE_NAME, 54);
    }

    @Override
    public void populatePage(Inventory inv) {

        for(int i = 0; i < 9; i++)
            inv.setItem(i, ItemBuilder.create(Material.WHITE_STAINED_GLASS_PANE).build());

        inv.setItem(0, ItemBuilder.create(Material.BLUE_BED)
                .displayName(Conf.UI_ELEM_GO_BACK_NAME)
                .build()
        );

        this.updatePage(inv);

    }

    @Override
    public void updatePage(Inventory inv) {

        PlayerMineInstance inst = this.getMenu().getMineInstance();
        MineLotteryHandler lotteryHandler = inst.getLotteryHandler();

        if(!lotteryHandler.isLotteryEnabled()) {

            inv.setItem(31, ItemBuilder.create(Material.BARRIER)
                    .displayName(ChatColor.RED.toString() + ChatColor.BOLD + "Lottery disabled")
                    .lore("", ChatColor.GRAY + "The lottery is disabled while your mine has",
                            ChatColor.GRAY + "it's access level assigned to " + ChatColor.YELLOW + "private").build()
            );

            return;

        }

        int slot = 10;
        for(UUID entryUuid : lotteryHandler.getEntries()) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entryUuid);

            inv.setItem(slot, ItemBuilder.head(entryUuid)
                    .displayName(ChatColor.GOLD.toString() + ChatColor.BOLD + offlinePlayer.getName())
                    .build()
            );

            slot++;
            if((slot + 1) % 9 == 0)
                slot += 2;

        }

    }

    @Override
    public void handleClick(InventoryClickEvent ev) {

        ItemStack clicked = ev.getCurrentItem();
        if(clicked == null || clicked.getType() == Material.AIR)
            return;

        MineManageMenu menu = this.getMenu();
        Player ply = menu.getPlayer();

        ply.playSound(ply.getEyeLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

        if(clicked.getType() == Material.BLUE_BED) {

            menu.openPage(new MineManageRoot(menu));
            ply.playSound(ply.getEyeLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

            return;

        }

    }

}
