package com.bgsoftware.wildbuster.hooks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.LandArea;
import me.angeschossen.lands.api.role.enums.RoleSetting;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public final class ClaimsProvider_Lands4 implements ClaimsProvider {

    private final LandsIntegration landsIntegration;

    public static boolean isCompatible() {
        try {
            Class.forName("me.angeschossen.lands.api.role.enums.RoleSetting");
            return true;
        } catch (ClassNotFoundException error) {
            return false;
        }
    }

    public ClaimsProvider_Lands4(WildBusterPlugin plugin){
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
