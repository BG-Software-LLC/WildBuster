package com.bgsoftware.wildbuster.hooks;

import com.github.intellectualsites.plotsquared.api.PlotAPI;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.sk89q.worldedit.math.BlockVector2;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

public final class ClaimsProvider_PlotSquared4 implements ClaimsProviderPerChunk {

    private final PlotAPI API = new PlotAPI();

    @Override
    public boolean canBuild(OfflinePlayer player, Chunk chunk) {
        BlockVector2 chunkPosition = BlockVector2.at(chunk.getX(), chunk.getZ());

        Plot plot = API.getChunkManager().hasPlot(chunk.getWorld().getName(), chunkPosition);

        return plot == null || (player.isOnline() && player.getPlayer().hasPermission("plots.admin.build.other")) ||
                plot.isOwner(player.getUniqueId()) || plot.isAdded(player.getUniqueId());
    }

}
