package com.bgsoftware.wildbuster.hooks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public final class ClaimsProvider_Lands7 implements ClaimsProviderPerChunk {

    private final LandsIntegration landsIntegration;

    public ClaimsProvider_Lands7(WildBusterPlugin plugin) {
        landsIntegration = LandsIntegration.of(plugin);
        WildBusterPlugin.log(" - Using Lands as ClaimsProvider.");
    }

    @Override
    public boolean canBuild(OfflinePlayer player, Chunk chunk) {
        Location chunkLocation = new Location(chunk.getWorld(), chunk.getX() << 4, 100, chunk.getZ() << 4);
        Area area = landsIntegration.getArea(chunkLocation);
        return area == null || area.hasRoleFlag(player.getUniqueId(), Flags.BLOCK_PLACE);
    }

}
