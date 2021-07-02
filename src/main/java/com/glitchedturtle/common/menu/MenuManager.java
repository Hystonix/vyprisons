package com.glitchedturtle.common.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager implements Listener {

    private JavaPlugin _javaPlugin;
    private Map<UUID, AbstractMenu> _activeMenuMap = new HashMap<>();

    public MenuManager(JavaPlugin javaPlugin) {
        _javaPlugin = javaPlugin;

        Bukkit.getPluginManager()
                .registerEvents(this, _javaPlugin);

    }

    public void openMenu(Player player, AbstractMenu menu) {
        menu.initialize(this, _javaPlugin, player);
        _activeMenuMap.put(player.getUniqueId(), menu);
    }

    public void destroyMenu(AbstractMenu menu) {

        Player ply = menu.getPlayer();
        if(ply == null)
            return;

        menu.destroy();

        _activeMenuMap.remove(ply.getUniqueId());
        ply.closeInventory();

    }

    @EventHandler
    public void onClick(InventoryClickEvent ev) {

        Player ply = (Player) ev.getWhoClicked();

        AbstractMenu menu = _activeMenuMap.get(ply.getUniqueId());
        if(menu == null)
            return;
        if(ply.getOpenInventory().getTopInventory() != menu.getInventory()) {
            this.destroyMenu(menu);
            return;
        }

        menu.handleClick(ev);
        ev.setCancelled(true);

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        AbstractMenu activeMenu = this._activeMenuMap.get(event.getPlayer().getUniqueId());
        if (activeMenu == null)
            return;
        if (activeMenu.isChangingPage())
            return;

        activeMenu.handleClose();
        this._activeMenuMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this._activeMenuMap.remove(event.getPlayer().getUniqueId());
    }

}
