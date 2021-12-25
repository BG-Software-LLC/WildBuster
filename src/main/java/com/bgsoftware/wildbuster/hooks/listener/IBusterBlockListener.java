package com.bgsoftware.wildbuster.hooks.listener;

import com.bgsoftware.wildbuster.api.objects.BlockData;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public interface IBusterBlockListener {

    void recordBlockAction(OfflinePlayer offlinePlayer, Location location, BlockData blockData, Action action);

    enum Action {

        BLOCK_PLACE,
        BLOCK_BREAK

    }

}
