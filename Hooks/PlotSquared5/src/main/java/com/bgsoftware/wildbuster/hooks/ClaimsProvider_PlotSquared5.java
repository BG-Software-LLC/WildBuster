package com.bgsoftware.wildbuster.hooks;

import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.math.BlockVector2;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

public final class ClaimsProvider_PlotSquared5 implements ClaimsProviderPerChunk {

    private final PlotAPI API = new PlotAPI();

    @Override
    public boolean canBuild(OfflinePlayer player, Chunk chunk) {
        BlockVector2 chunkPosition = BlockVector2.at(chunk.getX(), chunk.getZ());

        Plot plot = API.getChunkManager().hasPlot(chunk.getWorld().getName(), chunkPosition);

        return plot == null || (player.isOnline() && player.getPlayer().hasPermission("plots.admin.build.other")) ||
                plot.isOwner(player.getUniqueId()) || plot.isAdded(player.getUniqueId());
    }

}
