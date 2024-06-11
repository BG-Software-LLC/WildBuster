package com.bgsoftware.wildbuster.hooks;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public final class ClaimsProvider_PlotSquaredLegacy implements ClaimsProviderPerBlock {

    private final PlotAPI API = new PlotAPI();

    @Override
    public boolean canBuild(OfflinePlayer player, Location blockLocation) {
        Plot plot = API.getPlot(blockLocation);
        return plot == null || (player.isOnline() && player.getPlayer().hasPermission("plots.admin.build.other")) ||
                plot.isOwner(player.getUniqueId()) || plot.isAdded(player.getUniqueId());
    }

}
