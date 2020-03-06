package com.bgsoftware.wildbuster.api.handlers;

import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.api.objects.ChunkBuster;
import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnusedReturnValue")
public interface BustersManager {

    /**
     * Get a chunk buster by it's name.
     * @param name The name of the chunk buster.
     */
    ChunkBuster getChunkBuster(String name);

    /**
     * Get a chunk buster by it's item.
     * @param item The item of the chunk buster.
     */
    ChunkBuster getChunkBuster(ItemStack item);

    /**
     * Get all available chunk busters.
     */
    List<ChunkBuster> getChunkBusters();

    /**
     * Get an active chunk buster inside a chunk.
     * @param chunk The chunk to check for an active chunk buster.
     * @return The chunk buster if exists, otherwise null.
     */
    PlayerBuster getPlayerBuster(Chunk chunk);

    /**
     * Check whether or not a chunk is currently being busted or not.
     * @param chunk The chunk to check for being busted.
     */
    boolean isChunkBusted(Chunk chunk);

    /**
     * Get all current active chunk busters.
     */
    List<PlayerBuster> getPlayerBusters();

    /**
     * Get all current active chunk busters by a player.
     * @param player The player to check.
     */
    List<PlayerBuster> getPlayerBusters(OfflinePlayer player);

    /**
     * Create a new chunk buster type.
     * @param name The name of the new chunk buster. Must be unique.
     * @param radius The radius of the new chunk buster.
     * @param busterItem The item of the new chunk buster.
     */
    ChunkBuster createChunkBuster(String name, int radius, ItemStack busterItem);

    /**
     * Remove all chunk busters from cache.
     */
    void removeChunkBusters();

    /**
     * Create a new active chunk buster.
     * @param player The player who placed the chunk buster.
     * @param placedLocation The location the chunk buster was placed at.
     * @param buster The chunk buster type.
     * @return The new active chunk buster.
     */
    PlayerBuster createPlayerBuster(Player player, Location placedLocation, ChunkBuster buster);

    /**
     * Load an active chunk buster into cache.
     * @param busterName The name of the chunk buster type.
     * @param uuid The uuid of the player who placed the chunk buster.
     * @param world The world the chunk buster is active inside.
     * @param cancelStatus The cancel status of the chunk buster.
     * @param notifyStatus The notify status of the chunk buster.
     * @param currentLevel The current level the chunk buster is busting.
     * @param chunksList A list of chunks the chunk buster is busting.
     * @param removedBlocks A list of already removed blocks.
     * @return The new active chunk buster.
     */
    PlayerBuster loadPlayerBuster(String busterName, UUID uuid, World world, boolean cancelStatus, boolean notifyStatus, int currentLevel, List<Chunk> chunksList, List<BlockData> removedBlocks);

    /**
     * Remove an active chunk buster.
     * @param buster The active chunk buster to remove.
     */
    void removePlayerBuster(PlayerBuster buster);

    /**
     * Change the notify status of a chunk buster.
     * @param buster The chunk buster to change its notify status.
     */
    void setNotifyBuster(PlayerBuster buster);

    /**
     * Get an active chunk buster that has a notify status enabled.
     * @param uuid The uuid of the player who placed the chunk buster.
     */
    PlayerBuster getNotifyBuster(UUID uuid);

    /**
     * Get a block data from a block.
     * The block data caches important information about the block, to be used to restore it later.
     * @param block The block to get data of.
     */
    BlockData getBlockData(Block block);

    /**
     * Handle placement of chunk buster.
     * This method will not check for existing busters, permissions and such.
     * It will only handle the chunk placement event.
     * @param player The player who placed the chunk buster.
     * @param location The location the buster was placed at.
     * @param chunkBuster The chunk buster that was placed.
     */
    void handleBusterPlacement(Player player, Location location, ChunkBuster chunkBuster);

}
