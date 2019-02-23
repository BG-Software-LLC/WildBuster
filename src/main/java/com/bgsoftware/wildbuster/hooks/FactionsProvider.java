package com.bgsoftware.wildbuster.hooks;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public interface FactionsProvider {

    boolean isWilderness(Chunk chunk);

    boolean isPlayersClaim(Player player, Chunk chunk);

    boolean hasBypassMode(Player player);

}
