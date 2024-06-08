package com.bgsoftware.wildbuster.hooks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public final class ClaimsProvider_Lands7 implements ClaimsProvider {

    private final LandsIntegration landsIntegration;

    public ClaimsProvider_Lands7(WildBusterPlugin plugin) {
        landsIntegration = LandsIntegration.of(plugin);
        WildBusterPlugin.log(" - Using Lands as ClaimsProvider.");
    }

    @Override
    public boolean canBuild(OfflinePlayer player, Block block) {
        Area area = landsIntegration.getArea(block.getLocation());
        return area == null || area.hasRoleFlag(player.getUniqueId(), Flags.BLOCK_PLACE);
    }

}
