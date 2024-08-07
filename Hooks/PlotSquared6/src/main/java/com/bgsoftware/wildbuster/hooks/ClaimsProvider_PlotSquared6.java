package com.bgsoftware.wildbuster.hooks;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import org.bukkit.OfflinePlayer;

public final class ClaimsProvider_PlotSquared6 implements ClaimsProviderPerBlock {

    private final PlotSquared instance = PlotSquared.get();

    @Override
    public boolean canBuild(OfflinePlayer player, org.bukkit.Location blockLocation) {
        Location plotLocation = BukkitUtil.adaptComplete(blockLocation);
        PlotArea plotArea = instance.getPlotAreaManager().getPlotArea(plotLocation);
        Plot plot = plotArea == null ? null : plotArea.getPlot(plotLocation);
        return plot == null || (player.isOnline() && player.getPlayer().hasPermission("plots.admin.build.other")) ||
                plot.isOwner(player.getUniqueId()) || plot.isAdded(player.getUniqueId());
    }

}
