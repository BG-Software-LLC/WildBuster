package com.bgsoftware.wildbuster.api.objects;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface PlayerBuster {

    String getBusterName();

    ChunkBuster getChunkBuster();

    UUID getUniqueID();

    List<Chunk> getChunks();

    World getWorld();

    int getCurrentLevel();

    int getTaskID();

    List<BlockData> getRemovedBlocks();

    boolean isCancelled();

    boolean isNotify();

    void setNotify();

    void runRegularTask();

    void performCancel(CommandSender sender);

    void runCancelTask();

    void deleteBuster(boolean giveBusterItem);

}
