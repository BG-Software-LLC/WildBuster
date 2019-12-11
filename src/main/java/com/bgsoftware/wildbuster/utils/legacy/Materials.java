package com.bgsoftware.wildbuster.utils.legacy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Materials {

    PLAYER_HEAD("SKULL_ITEM", (short) 3),
    BLACK_STAINED_GLASS_PANE("STAINED_GLASS_PANE", (short) 15);

    private static boolean isLegacy = !Bukkit.getBukkitVersion().contains("1.13") && !Bukkit.getBukkitVersion().contains("1.14");

    private final String legacy;
    private final short damage;

    Materials(String legacy, short damage){
        this.legacy = legacy;
        this.damage = damage;
    }

    public ItemStack parseItem(){
        Material material = parseMaterial();
        return !isLegacy ? new ItemStack(material) : new ItemStack(material, 1, damage);
    }

    public Material parseMaterial(){
        return !isLegacy ? Material.matchMaterial(toString()) : Material.matchMaterial(legacy);
    }

}
