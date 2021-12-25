package com.bgsoftware.wildbuster.hooks;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public final class ClaimsProvider_Default implements ClaimsProvider {

    @Override
    public boolean canBuild(OfflinePlayer player, Block block) {
        return true;
    }
}
