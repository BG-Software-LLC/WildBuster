package com.bgsoftware.wildbuster.hooks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.PlayerManager;
import net.prosavage.factionsx.persist.data.FactionsKt;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class FactionsProvider_FactionsX implements FactionsProvider {

    public FactionsProvider_FactionsX() {
        WildBusterPlugin.log(" - Using FactionsX as FactionsProvider.");
    }

    @Override
    public boolean isWilderness(Chunk chunk) {
        Location blockLocation = new Location(chunk.getWorld(), chunk.getX() << 4, 100, chunk.getZ() << 4);
        return GridManager.INSTANCE.getFactionAt(FactionsKt.getFLocation(blockLocation)).isWilderness();
    }

    @Override
    public boolean isPlayersClaim(Player player, Chunk chunk) {
        Location blockLocation = new Location(chunk.getWorld(), chunk.getX() << 4, 100, chunk.getZ() << 4);
        return GridManager.INSTANCE.getFactionAt(FactionsKt.getFLocation(blockLocation)).getFactionMembers().contains(player.getUniqueId());
    }

    @Override
    public boolean hasBypassMode(Player player) {
        return PlayerManager.INSTANCE.getFPlayer(player).getInBypass();
    }

}
