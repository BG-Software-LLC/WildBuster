package com.bgsoftware.wildbuster.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public final class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material type){
        this(type, 1);
    }

    public ItemBuilder(Material type, int amount){
        this(type, amount, (short) 0);
    }

    public ItemBuilder(Material type, short damage){
        this(type, 1, damage);
    }

    public ItemBuilder(Material type, int amount, short damage){
        this(new ItemStack(type, amount, damage));
    }

    public ItemBuilder(ItemStack itemStack){
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name){
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore){
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setOwner(String playerName){
        if(itemMeta instanceof SkullMeta){
            ((SkullMeta) itemMeta).setOwner(playerName);
        }
        return this;
    }

    public ItemStack build(){
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
