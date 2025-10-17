package com.bgsoftware.wildbuster.hooks;

import com.bgsoftware.common.reflection.ReflectMethod;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Container;
import me.angeschossen.lands.api.land.LandWorld;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.Collection;

public final class ClaimsProvider_Lands7 implements ClaimsProviderPerChunk {

    private static final boolean CHUNK_LOOKUP_SUPPORT = new ReflectMethod<Collection<?>>(
            Container.class, "getAreasInChunk", int.class, int.class, boolean.class)
            .isValid();

    private final LandsIntegration landsIntegration;

    public ClaimsProvider_Lands7(WildBusterPlugin plugin) {
        landsIntegration = LandsIntegration.of(plugin);
        WildBusterPlugin.log(" - Using Lands as ClaimsProvider.");
    }

    @Override
    public boolean canBuild(OfflinePlayer player, Chunk chunk) {
        return CHUNK_LOOKUP_SUPPORT ? canBuildChunkAreasCheck(player, chunk) : canBuildLegacy(player, chunk);
    }

    private boolean canBuildChunkAreasCheck(OfflinePlayer player, Chunk chunk) {
        LandWorld landWorld = this.landsIntegration.getWorld(chunk.getWorld());
        if (landWorld == null)
            return true;

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        Container container = landWorld.getContainer(chunkX, chunkZ);
        if (container == null)
            return true;

        for (Area area : (Collection<Area>) container.getAreasInChunk(chunkX, chunkZ, true)) {
            if (!area.hasRoleFlag(player.getUniqueId(), Flags.BLOCK_BREAK))
                return false;
        }

        return true;
    }

    private boolean canBuildLegacy(OfflinePlayer player, Chunk chunk) {
        Location chunkLocation = new Location(chunk.getWorld(), chunk.getX() << 4, 100, chunk.getZ() << 4);
        Area area = this.landsIntegration.getArea(chunkLocation);
        return area == null || area.hasRoleFlag(player.getUniqueId(), Flags.BLOCK_BREAK);
    }

}
