package com.glitchedturtle.common.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractMenuPage<I extends AbstractMenu> {

    private I _menu;

    private String _title;
    private int _size;

    private int _updateRate = -1;
    private int _runnableId = -1;

    public AbstractMenuPage(I menu, String title, int size) {
        _menu = menu;
        _title = title;
        _size = size;
    }

    public void updateOnTick(int tickRate) {
        _updateRate = tickRate;
    }

    Inventory createInventory(Player ply) {
        return Bukkit.createInventory(ply, _size, _title);
    }

    boolean isInventorySimilar(AbstractMenuPage page) {
        return page._size == this._size
                && page._title.equals(this._title);
    }

    public I getMenu() {
        return _menu;
    }

    public int getUpdateRate() {
        return _updateRate;
    }

    public int getRunnableId() {
        return _runnableId;
    }

    public void setRunnableId(int runnableId) {
        _runnableId = runnableId;
    }

    public abstract void populatePage(Inventory inv);
    public abstract void updatePage(Inventory inv);
    public void handleClick(InventoryClickEvent event) { }

}
