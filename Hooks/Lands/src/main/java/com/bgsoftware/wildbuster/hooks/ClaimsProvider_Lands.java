package com.bgsoftware.wildbuster.hooks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.LandArea;
import me.angeschossen.lands.api.role.enums.RoleSetting;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public final class ClaimsProvider_Lands implements ClaimsProvider {

    private final LandsIntegration landsIntegration;

    public ClaimsProvider_Lands(WildBusterPlugin plugin){
        landsIntegration = new LandsIntegration(plugin, false);
        landsIntegration.initialize();
        WildBusterPlugin.log(" - Using Lands as ClaimsProvider.");
    }

    @Override
    public boolean canBuild(OfflinePlayer player, Block block) {
        LandArea landArea = landsIntegration.getArea(block.getLocation());
        return landArea == null || landArea.canSetting(player.getUniqueId(), RoleSetting.BLOCK_PLACE);
    }

}
