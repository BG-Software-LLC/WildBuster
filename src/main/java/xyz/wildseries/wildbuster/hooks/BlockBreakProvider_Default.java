package xyz.wildseries.wildbuster.hooks;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public final class BlockBreakProvider_Default implements BlockBreakProvider {

    @Override
    public boolean canBuild(OfflinePlayer player, Block block) {
        return true;
    }
}
