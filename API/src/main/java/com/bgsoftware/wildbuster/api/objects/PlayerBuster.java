package com.bgsoftware.wildbuster.api.objects;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Timer;
import java.util.UUID;

@SuppressWarnings("unused")
public interface PlayerBuster {

    /**
     * Get the name of the chunk buster type.
     */
    String getBusterName();

    /**
     * Get the chunk buster type.
     */
    ChunkBuster getChunkBuster();

    /**
     * Get the uuid of the player who placed this chunk buster.
     */
    UUID getUniqueID();

    /**
     * Get all chunks this chunk buster is busting.
     */
    List<Chunk> getChunks();

    /**
     * Get the world of this chunk buster.
     */
    World getWorld();

    /**
     * Get the current level of this chunk buster.
     */
    int getCurrentLevel();

    /**
     * Get the task id of this chunk buster.
     * @deprecated See getBusterTimer()
     */
    @Deprecated
    int getTaskID();

    /**
     * Get the timer object of this chunk buster.
     */
    Timer getBusterTimer();

    /**
     * Get all the blocks that this chunk buster removed.
     */
    List<BlockData> getRemovedBlocks();

    /**
     * Check whether or not this buster is cancelled.
     */
    boolean isCancelled();

    /**
     * Check whether or not this buster is notifying the player who placed it.
     */
    boolean isNotify();

    /**
     * Set the chunk buster to notify the player who placed it.
     */
    void setNotify();

    /**
     * Start running the busting chunks task.
     */
    void runRegularTask();

    /**
     * Perform a cancel task.
     * @param sender The sender who asks to cancel this chunk buster.
     */
    void performCancel(CommandSender sender);

    /**
     * Start running the reverse chunks task.
     */
    void runCancelTask();

    /**
     * Delete this chunk buster.
     * @param giveBusterItem Whether or not the item should be given to the player who placed this chunk buster or not.
     */
    void deleteBuster(boolean giveBusterItem);

    /**
     * Get all nearby players to this chunk buster.
     */
    List<Player> getNearbyPlayers();

}
