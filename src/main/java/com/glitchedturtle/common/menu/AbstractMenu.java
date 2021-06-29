package com.glitchedturtle.common.menu;

import com.glitchedturtle.common.util.TAssert;
import com.glitchedturtle.vyprisons.VyPrisonPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.ref.WeakReference;

public abstract class AbstractMenu<I extends JavaPlugin> {

    private MenuManager _menuManager;
    private I _javaPlugin;

    private WeakReference<Player> _owner;
    private boolean _active = false;
    private boolean _pageChanging = false;

    private AbstractMenuPage _activePage = null;
    private Inventory _inventory;

    void initialize(MenuManager menuManager, I javaPlugin, Player ply) {

        TAssert.assertFalse(_active, "Menu already active");
        TAssert.assertTrue(ply != null, "Player must be non-null");

        _owner = new WeakReference<>(ply);
        _menuManager = menuManager;
        _javaPlugin = javaPlugin;

        if(ply == null)
            return;

        if(_activePage == null) {

            ply.sendMessage(ChatColor.RED + "Attempting to open a menu with no root page. Please contact a server administrator.");
            return;

        }

        _inventory = _activePage.createInventory(ply);
        _activePage.populatePage(_inventory);

        ply.openInventory(_inventory);
        _active = true;

        int updateRate = _activePage.getUpdateRate();
        if(updateRate != -1) {
            _activePage.setRunnableId(Bukkit.getScheduler().scheduleSyncRepeatingTask(_javaPlugin, () -> {
                _activePage.updatePage(_inventory);
            }, updateRate, updateRate));
        }

    }

    void destroy() {

        int runnableId = _activePage.getRunnableId();
        if(runnableId != -1) {
            Bukkit.getScheduler().cancelTask(runnableId);
        }

    }

    protected void setRoot(AbstractMenuPage page) {
        _activePage = page;
    }

    public void openPage(AbstractMenuPage page) {

        if(!_active) {

            this.setRoot(page);
            return;

        }

        _pageChanging = true;

        Player ply = this.getPlayer();
        if(_activePage.isInventorySimilar(page))
            _inventory.clear();
        else {

            _inventory = page.createInventory(ply);

            ply.closeInventory();
            ply.openInventory(_inventory);

        }

        int runnableId = _activePage.getRunnableId();
        if(runnableId != -1) {
            Bukkit.getScheduler().cancelTask(runnableId);
        }

        page.populatePage(_inventory);
        _activePage = page;

        int updateRate = _activePage.getUpdateRate();
        if(updateRate != -1) {
            _activePage.setRunnableId(Bukkit.getScheduler().scheduleSyncRepeatingTask(_javaPlugin, () -> {
                _activePage.updatePage(_inventory);
            }, updateRate, updateRate));
        }

        _pageChanging = false;

    }

    public Player getPlayer() {
        return _owner.get();
    }
    public I getPlugin() {
        return _javaPlugin;
    }

    Inventory getInventory() {
        return _inventory;
    }

    public boolean isChangingPage() {
        return _pageChanging;
    }

    public void handleClick(InventoryClickEvent ev) {

        if(_activePage == null)
            return;
        _activePage.handleClick(ev);

    }
}
