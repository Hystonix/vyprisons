package com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.page;

import com.glitchedturtle.common.menu.AbstractMenuPage;
import com.glitchedturtle.common.util.ItemBuilder;
import com.glitchedturtle.common.util.TAssert;
import com.glitchedturtle.vyprisons.command.impl.mine.manage.ui.MineManageMenu;
import com.glitchedturtle.vyprisons.configuration.Conf;
import com.glitchedturtle.vyprisons.player.mine.MineTierManager;
import com.glitchedturtle.vyprisons.player.mine.PlayerMineInstance;
import com.glitchedturtle.vyprisons.util.VaultHook;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MineTierPage extends AbstractMenuPage<MineManageMenu> {

    public MineTierPage(MineManageMenu menu) {
        super(menu, Conf.UI_ELEM_TIER_PAGE_NAME, 27);
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

        MineTierManager tierManager =
                this.getMenu().getMineInstance().getMineManager().getTierManager(); // jeez

        Economy econ = VaultHook.getEconomy();
        Player ply = this.getMenu().getPlayer();

        PlayerMineInstance instance = this.getMenu().getMineInstance();
        int currentTier = instance.getTier();

        for(int i = 1; i <= tierManager.getMaximumTier(); i++) {

            MineTierManager.CompositionTier tier = tierManager.getTier(i);

            Material type;
            String tagLine;

            if(currentTier >= i) {

                type = Material.YELLOW_STAINED_GLASS_PANE;
                tagLine = ChatColor.GREEN + "Current level";

            } else if(currentTier + 1 == i) {

                type = Material.GREEN_STAINED_GLASS_PANE;
                tagLine = ChatColor.GRAY + "Upgrade for " + ChatColor.YELLOW + "$" + tier.getPurchasePrice();

            } else {

                type = Material.GRAY_STAINED_GLASS_PANE;
                tagLine = ChatColor.RED + "Upgrade to earlier tiers to unlock";

            }

            List<String> lore = new ArrayList<>();
            lore.add(tagLine);
            lore.add("");
            lore.add(ChatColor.GRAY + "Tier composition:");
            lore.addAll(tier.getCompositionDisplay());

            if(currentTier + 1 == i) { // if next tier

                lore.add("");

                boolean canAfford = econ.has(ply, tier.getPurchasePrice());
                if(canAfford)
                    lore.add(ChatColor.GREEN + "Left-click to purchase this tier");
                else
                    lore.add(ChatColor.RED + "You can not afford this tier");

            }

            inv.setItem(i + 9,
                  ItemBuilder.create(type)
                        .displayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Tier " + i
                                + ((currentTier >= i) ? ChatColor.GRAY + " (Owned)" : "")
                        ).lore(lore).build()
            );

        }

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

        PlayerMineInstance instance = this.getMenu().getMineInstance();
        int currentTier = instance.getTier();

        int clickedTier = ev.getSlot() - 9;
        if(clickedTier != currentTier + 1) {

            ply.playSound(ply.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            return;

        }

        MineTierManager tierManager =
                this.getMenu().getMineInstance().getMineManager().getTierManager(); // jeez
        MineTierManager.CompositionTier tier = tierManager.getTier(clickedTier);
        TAssert.assertTrue(tier != null, "Tier null");

        Economy econ = VaultHook.getEconomy();
        EconomyResponse res = econ.withdrawPlayer(ply, tier.getPurchasePrice());

        if(!res.transactionSuccess()) {


            ply.closeInventory();
            ply.sendMessage(Conf.CMD_TIER_INSUFFICIENT_BALANCE);

            ply.playSound(ply.getEyeLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);

            return;

        }

        instance.setTier(clickedTier).whenComplete((v, ex) -> {

            if(ex != null) {

                ply.closeInventory();
                ply.sendMessage(Conf.CMD_TIER_UPDATE_FAILED);

                System.out.println("An error occured while increasing mine tier (Owner: " + ply.getUniqueId().toString() + ")");
                ex.printStackTrace();

            }

            ply.playSound(ply.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            this.updatePage(ev.getInventory());

        });

    }

}
