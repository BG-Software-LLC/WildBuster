package com.bgsoftware.wildbuster.objects;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("WeakerAccess")
public final class WBlockData implements BlockData {

    private static WildBusterPlugin plugin = WildBusterPlugin.getPlugin();
    public static BlockData AIR = new WBlockData(Material.AIR, (byte) 0, 0, null, 0, 0, 0);

    private final Material type;
    private final byte data;
    private final int combinedId;
    private final World world;
    private final int x, y, z;

    private ItemStack[] contents;

    public WBlockData(Block block){
        this(block.getType(), (byte) WildBusterPlugin.getPlugin().getNMSAdapter().getMaterialData(block),
                WildBusterPlugin.getPlugin().getNMSAdapter().getCombinedId(block), block.getWorld(),
                block.getX(), block.getY(), block.getZ());
        if(block.getState() instanceof InventoryHolder)
            this.contents = ((InventoryHolder) block.getState()).getInventory().getContents();
    }

    public WBlockData(Material type, byte data, int combinedId, World world, int x, int y, int z){
        this.type = type;
        this.data = data;
        this.combinedId = combinedId;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.contents = new ItemStack[0];
    }

    @Override
    public Material getType() {
        return type;
    }

    @Override
    public int getTypeId() {
        return plugin.getNMSAdapter().getMaterialId(type);
    }

    @Override
    public byte getData() {
        return data;
    }

    @Override
    public int getCombinedId() {
        return combinedId;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public Location getLocation() {
        return new Location(world, x, y, z);
    }

    @Override
    public Block getBlock() {
        return world.getBlockAt(getLocation());
    }

    @Override
    public boolean hasContents() {
        return contents != null && contents.length > 0;
    }

    @Override
    public void setContents(ItemStack[] contents) {
        this.contents = contents.clone();
    }

    @Override
    public ItemStack[] getContents() {
        return contents.clone();
    }

}
