package com.bgsoftware.wildbuster.utils.legacy;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Materials {

    PLAYER_HEAD("SKULL_ITEM", (short) 3),
    BLACK_STAINED_GLASS_PANE("STAINED_GLASS_PANE", (short) 15);

    private static boolean isLegacy = isLegacy();

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

    private static boolean isLegacy(){
        try{
            Material.valueOf("BLACK_STAINED_GLASS_PANE");
            return false;
        }catch(Throwable ex){
            return true;
        }
    }

}
