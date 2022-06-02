package com.bgsoftware.wildbuster.hooks;

import com.github.intellectualsites.plotsquared.api.PlotAPI;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.sk89q.worldedit.math.BlockVector2;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public final class ClaimsProvider_PlotSquared4 implements ClaimsProvider {

    private final PlotAPI API = new PlotAPI();

    @Override
    public boolean canBuild(OfflinePlayer player, Block block) {
        BlockVector2 chunkPosition = BlockVector2.at(block.getX() >> 4, block.getZ() >> 4);

        Plot plot = API.getChunkManager().hasPlot(block.getWorld().getName(), chunkPosition);

        return plot == null || (player.isOnline() && player.getPlayer().hasPermission("plots.admin.build.other")) ||
                plot.isOwner(player.getUniqueId()) || plot.isAdded(player.getUniqueId());
    }

}
