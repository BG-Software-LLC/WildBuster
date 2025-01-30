package com.bgsoftware.wildbuster.nms;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public interface NMSAdapter {

    String getVersion();

    default void loadLegacy() {

    }

    void setFastBlock(Location location, BlockData blockData);

    void refreshChunk(Chunk chunk, List<Location> blocksList, List<Player> playerList);

    void refreshLight(Chunk chunk);

    void clearTileEntities(Chunk chunk, List<Location> tileEntities);

    void sendActionBar(Player player, String message);

    int getMaterialId(Material type);

    int getMaterialData(Block block);

    int getCombinedId(Block block);

    ChunkSnapshotReader createChunkSnapshotReader(ChunkSnapshot chunkSnapshot);

    Object getBlockData(int combined);

    ItemStack getPlayerSkull(ItemStack itemStack, String texture);

    boolean isInsideBorder(Location location);

    void makeItemGlow(ItemMeta itemMeta);

    default boolean isTallGrass(Material type) {
        return type == Material.LONG_GRASS;
    }

    default Object getCustomHolder(InventoryType inventoryType, InventoryHolder defaultHolder, String title) {
        return defaultHolder;
    }

    default void handleChunkUnload(org.bukkit.World world, List<org.bukkit.Chunk> chunks, WildBusterPlugin plugin, boolean unload) {

    }

    default int getWorldMinHeight(World world) {
        return 0;
    }

}
