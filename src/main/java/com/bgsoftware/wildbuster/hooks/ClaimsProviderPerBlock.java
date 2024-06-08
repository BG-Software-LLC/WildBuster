package com.bgsoftware.wildbuster.hooks;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public interface ClaimsProviderPerBlock extends ClaimsProvider {

    default Type getType() {
        return Type.BLOCK_CLAIM;
    }

    boolean canBuild(OfflinePlayer offlinePlayer, Block block);

}
