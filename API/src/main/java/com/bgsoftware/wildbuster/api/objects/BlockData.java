package com.bgsoftware.wildbuster.api.objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface BlockData {

    Material getType();

    int getTypeId();

    byte getData();

    int getCombinedId();

    World getWorld();

    int getX();

    int getY();

    int getZ();

    Location getLocation();

    Block getBlock();

    boolean hasContents();

    void setContents(ItemStack[] contents);

    ItemStack[] getContents();

}
