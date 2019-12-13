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

    String getBusterName();

    ChunkBuster getChunkBuster();

    UUID getUniqueID();

    List<Chunk> getChunks();

    World getWorld();

    int getCurrentLevel();

    @Deprecated
    int getTaskID();

    Timer getBusterTimer();

    List<BlockData> getRemovedBlocks();

    boolean isCancelled();

    boolean isNotify();

    void setNotify();

    void runRegularTask();

    void performCancel(CommandSender sender);

    void runCancelTask();

    void deleteBuster(boolean giveBusterItem);

    List<Player> getNearbyPlayers();

}
