package com.bgsoftware.wildbuster.hooks;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public final class ClaimsProvider_PlotSquaredLegacy implements ClaimsProvider {

    private final PlotAPI API = new PlotAPI();

    @Override
    public boolean canBuild(OfflinePlayer player, Block block) {
        Plot plot = API.getPlot(block.getLocation());
        return plot == null || (player.isOnline() && player.getPlayer().hasPermission("plots.admin.build.other")) ||
                plot.isOwner(player.getUniqueId()) || plot.isAdded(player.getUniqueId());
    }

}
