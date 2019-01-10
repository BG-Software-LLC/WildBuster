package xyz.wildseries.wildbuster.objects;

import org.bukkit.inventory.ItemStack;
import xyz.wildseries.wildbuster.api.objects.ChunkBuster;

public final class WChunkBuster implements ChunkBuster {

    private final String name;
    private final int radius;
    private final ItemStack busterItem;

    public WChunkBuster(String name, int radius, ItemStack busterItem){
        this.name = name;
        this.radius = radius;
        this.busterItem = busterItem;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public ItemStack getBusterItem() {
        return busterItem.clone();
    }
}
