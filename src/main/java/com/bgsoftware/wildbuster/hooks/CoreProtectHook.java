package com.bgsoftware.wildbuster.hooks;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import com.bgsoftware.wildbuster.api.objects.BlockData;

public interface CoreProtectHook {

    void recordBlockChange(OfflinePlayer offlinePlayer, Location location, BlockData blockData, boolean place);

}
