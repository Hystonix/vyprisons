package com.glitchedturtle.common.menu.impl;

import com.glitchedturtle.common.menu.AbstractMenu;
import com.glitchedturtle.common.menu.AbstractMenuPage;
import com.glitchedturtle.common.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConfirmPage<I extends AbstractMenu<?>> extends AbstractMenuPage<I> {

    private String _question;
    private List<String> _description;

    private boolean _defaultResponse;
    private Consumer<Boolean> _callback;

    public ConfirmPage(I menu, String question, List<String> description, boolean defaultResponse, Consumer<Boolean> callback) {
        super(menu, question, 54);

        _question = question;

        _description = description.stream()
                .map(s -> ChatColor.GRAY + s)
                .collect(Collectors.toList());
        _description.add(0, "");

        _defaultResponse = defaultResponse;
        _callback = callback;

    }

    @Override
    public void populatePage(Inventory inv) {

        inv.setItem(13, ItemBuilder.create(Material.PAPER)
            .displayName(ChatColor.RED + _question)
                .lore(_description).build()
        );

        ItemStack confirmStack = ItemBuilder.create(Material.LIME_STAINED_GLASS_PANE)
                .displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "CONFIRM" )
                .lore(_description).build();
        for(int slot = 19; slot <= 39; slot++) {

            inv.setItem(slot, confirmStack);

            if((slot - 2) % 9 == 0)
                slot += 6;

        }

        ItemStack cancelStack = ItemBuilder.create(Material.RED_STAINED_GLASS_PANE)
                .displayName(ChatColor.RED + ChatColor.BOLD.toString() + "CANCEL" )
                .lore(_description).build();

        for(int slot = 23; slot <= 43; slot++) {

            inv.setItem(slot, cancelStack);

            if((slot + 2) % 9 == 0)
                slot += 6;

        }
    }

    @Override
    public void updatePage(Inventory inv) { }

    @Override
    public void handleClick(InventoryClickEvent ev) {

        ItemStack clicked = ev.getCurrentItem();
        if(clicked == null || clicked.getType() == Material.AIR)
            return;

        if(clicked.getType() != Material.RED_STAINED_GLASS_PANE &&
                clicked.getType() != Material.LIME_STAINED_GLASS_PANE)
                    return;

        Player ply = (Player) ev.getWhoClicked();
        boolean accept = clicked.getType() == Material.LIME_STAINED_GLASS_PANE;

        ply.playSound(ply.getEyeLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        _callback.accept(accept);

    }

    @Override
    public void handleClose() {
        _callback.accept(_defaultResponse);
    }

}
