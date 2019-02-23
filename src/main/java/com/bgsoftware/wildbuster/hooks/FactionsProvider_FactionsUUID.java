package com.bgsoftware.wildbuster.hooks;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public final class FactionsProvider_FactionsUUID implements FactionsProvider {

    @Override
    public boolean isWilderness(Chunk chunk) {
        FLocation fLocation = new FLocation(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        return Board.getInstance().getFactionAt(fLocation).isWilderness();
    }

    @Override
    public boolean isPlayersClaim(Player player, Chunk chunk) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        FLocation fLocation = new FLocation(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        return Board.getInstance().getFactionAt(fLocation).getFPlayers().contains(fPlayer);
    }

    @Override
    public boolean hasBypassMode(Player player) {
        return FPlayers.getInstance().getByPlayer(player).isAdminBypassing();
    }
}
