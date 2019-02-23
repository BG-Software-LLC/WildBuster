package com.bgsoftware.wildbuster.hooks;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public final class FactionsProvider_MassiveCore implements FactionsProvider {

    @Override
    public boolean isWilderness(Chunk chunk) {
        return BoardColl.get().getFactionAt(PS.valueOf(chunk)) != null;
    }

    @Override
    public boolean isPlayersClaim(Player player, Chunk chunk) {
        MPlayer mPlayer = MPlayer.get(player);
        return BoardColl.get().getFactionAt(PS.valueOf(chunk)).getMPlayers().contains(mPlayer);
    }

    @Override
    public boolean hasBypassMode(Player player) {
        return MPlayer.get(player).isOverriding();
    }
}
