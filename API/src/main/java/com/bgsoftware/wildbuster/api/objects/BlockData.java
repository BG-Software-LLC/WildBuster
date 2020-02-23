package com.bgsoftware.wildbuster.api.objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface BlockData {

    /**
     * Get the type of the block.
     */
    Material getType();

    /**
     * Get the type id of the block.
     */
    int getTypeId();

    /**
     * Get the data value of the block.
     */
    byte getData();

    /**
     * Get the combined id of the block.
     */
    int getCombinedId();

    /**
     * Get the world of the block.
     */
    World getWorld();

    /**
     * Get the x location of the block.
     */
    int getX();

    /**
     * Get the y location of the block.
     */
    int getY();

    /**
     * Get the z location of the block.
     */
    int getZ();

    /**
     * Get the location object of the block.
     */
    Location getLocation();

    /**
     * Get the block object from the location.
     */
    Block getBlock();

    /**
     * Check whether or not this block has container contents or not.
     */
    boolean hasContents();

    /**
     * Set container contents of the block.
     * @param contents The contents to set.
     */
    void setContents(ItemStack[] contents);

    /**
     * Get the container contents of the block.
     */
    ItemStack[] getContents();

}
