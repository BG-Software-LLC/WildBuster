package com.bgsoftware.wildbuster.api.objects;

import org.bukkit.inventory.ItemStack;

public interface ChunkBuster {

    /**
     * Get the name of the chunk buster.
     */
    String getName();

    /**
     * Get the radius of the chunk buster.
     */
    int getRadius();

    /**
     * Get the item of the chunk buster.
     */
    ItemStack getBusterItem();

}
