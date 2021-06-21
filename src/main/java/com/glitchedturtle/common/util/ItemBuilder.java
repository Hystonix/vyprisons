package com.glitchedturtle.common.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private Material _material;
    private int _amount = 1;

    private String _name = null;
    private List<String> _lore = null;

    private boolean _glowing = false;

    private ItemBuilder(Material mat, int amount) {

        _material = mat;
        _amount = amount;

    }

    public static ItemBuilder create(Material mat) {
        return new ItemBuilder(mat, 1);
    }

    public static ItemBuilder create(Material mat, int amount) {
        return new ItemBuilder(mat, amount);
    }

    public ItemBuilder displayName(String name) {
        _name = name;
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        _lore = lore;
        return this;
    }

    public ItemBuilder lore(String... lore) {
        return this.lore(Arrays.asList(lore));
    }

    public ItemBuilder setGlowing(boolean glowing) {
        _glowing = glowing;
        return this;
    }

    public ItemBuilder glowing() {
        return this.setGlowing(true);
    }

    public ItemStack build() {

        ItemStack stack = new ItemStack(_material);
        stack.setAmount(_amount);

        ItemMeta meta = stack.getItemMeta();
        if(meta == null)
            return stack;

        if(_name != null)
            meta.setDisplayName(_name);
        if(_lore != null)
            meta.setLore(_lore);

        if(_glowing) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        }

        stack.setItemMeta(meta);
        return stack;

    }

}
