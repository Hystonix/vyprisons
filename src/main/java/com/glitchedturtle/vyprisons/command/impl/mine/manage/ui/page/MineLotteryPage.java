package com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page;

import com.glitchedturtle.common.menu.AbstractMenuPage;
import com.glitchedturtle.common.util.ItemBuilder;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.MineManageMenu;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.player.mine.lottery.MineLotteryHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class MineLotteryPage extends AbstractMenuPage<MineManageMenu> {

    public MineLotteryPage(MineManageMenu menu) {
        super(menu, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "VyPrison > " + ChatColor.RESET + "Lottery", 54);
    }

    @Override
    public void populatePage(Inventory inv) {

        for(int i = 0; i < 9; i++)
            inv.setItem(i, ItemBuilder.create(Material.WHITE_STAINED_GLASS_PANE).build());

        inv.setItem(0, ItemBuilder.create(Material.BLUE_BED)
                .displayName(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "... Go back")
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

}
