package xyz.wildseries.wildbuster.hooks;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public final class FactionsProvider_Default implements FactionsProvider {

    @Override
    public boolean isWilderness(Chunk chunk) {
        return true;
    }

    @Override
    public boolean isPlayersClaim(Player player, Chunk chunk) {
        return true;
    }

    @Override
    public boolean hasBypassMode(Player player) {
        return true;
    }
}
