package com.bgsoftware.wildbuster.hooks;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public interface ClaimsProvider {

    boolean canBuild(OfflinePlayer player, Block block);

}
