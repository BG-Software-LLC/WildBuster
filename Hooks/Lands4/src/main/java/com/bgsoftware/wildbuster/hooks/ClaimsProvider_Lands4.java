package com.bgsoftware.wildbuster.hooks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.LandArea;
import me.angeschossen.lands.api.role.enums.RoleSetting;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public final class ClaimsProvider_Lands4 implements ClaimsProviderPerChunk {

    private final LandsIntegration landsIntegration;

    public static boolean isCompatible() {
        try {
            Class.forName("me.angeschossen.lands.api.role.enums.RoleSetting");
            return true;
        } catch (ClassNotFoundException error) {
            return false;
        }
    }

    public ClaimsProvider_Lands4(WildBusterPlugin plugin) {
        landsIntegration = new LandsIntegration(plugin, false);
        landsIntegration.initialize();
        WildBusterPlugin.log(" - Using Lands as ClaimsProvider.");
    }

    @Override
    public boolean canBuild(OfflinePlayer player, Chunk chunk) {
        Location chunkLocation = new Location(chunk.getWorld(), chunk.getX() << 4, 100, chunk.getZ() << 4);
        LandArea landArea = landsIntegration.getArea(chunkLocation);
        return landArea == null || landArea.canSetting(player.getUniqueId(), RoleSetting.BLOCK_PLACE);
    }

}
