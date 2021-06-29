package com.glitchedturtle.common.menu.impl;

import com.glitchedturtle.common.menu.AbstractMenu;
import com.glitchedturtle.common.menu.AbstractMenuPage;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.CompletableFuture;

public class ThrobberPage<I extends AbstractMenu> extends AbstractMenuPage<I> {

    private static final int[] ANIMATION_SLOTS = new int[] {
            13, 14, 23, 32, 31, 30, 21, 12
    };

    private CompletableFuture _future;
    private int _i = 0;

    public ThrobberPage(I menu, String title, CompletableFuture future) {

        super(menu, title, 54);
        this.updateOnTick(5);

        _future = future;

    }

    @Override
    public void populatePage(Inventory inv) {
        this.updatePage(inv);
    }

    @Override
    public void updatePage(Inventory inv) {

    }

}
