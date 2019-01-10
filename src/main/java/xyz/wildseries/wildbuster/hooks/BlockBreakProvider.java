package xyz.wildseries.wildbuster.hooks;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public interface BlockBreakProvider {

    boolean canBuild(OfflinePlayer player, Block block);

}
