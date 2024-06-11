package com.bgsoftware.wildbuster.hooks;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public interface ClaimsProviderPerBlock extends ClaimsProvider {

    default Type getType() {
        return Type.BLOCK_CLAIM;
    }

    boolean canBuild(OfflinePlayer offlinePlayer, Location blockLocation);

}
