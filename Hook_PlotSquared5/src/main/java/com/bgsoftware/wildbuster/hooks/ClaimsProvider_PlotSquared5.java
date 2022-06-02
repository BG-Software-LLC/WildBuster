package com.bgsoftware.wildbuster.hooks;

import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.math.BlockVector2;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public final class ClaimsProvider_PlotSquared5 implements ClaimsProvider {

    private final PlotAPI API = new PlotAPI();

    @Override
    public boolean canBuild(OfflinePlayer player, Block block) {
        BlockVector2 chunkPosition = BlockVector2.at(block.getX() >> 4, block.getZ() >> 4);

        Plot plot = API.getChunkManager().hasPlot(block.getWorld().getName(), chunkPosition);

        return plot == null || (player.isOnline() && player.getPlayer().hasPermission("plots.admin.build.other")) ||
                plot.isOwner(player.getUniqueId()) || plot.isAdded(player.getUniqueId());
    }

}
