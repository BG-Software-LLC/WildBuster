package com.bgsoftware.wildbuster.hooks;

import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

public interface ClaimsProviderPerChunk extends ClaimsProvider {

    default Type getType() {
        return Type.CHUNK_CLAIM;
    }

    boolean canBuild(OfflinePlayer offlinePlayer, Chunk chunk);

}
