package com.bgsoftware.wildbuster.hooks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public final class FactionsProvider_MassiveCore implements FactionsProvider {

    public FactionsProvider_MassiveCore() {
        WildBusterPlugin.log(" - Using MassiveCore as FactionsProvider.");
    }

    @Override
    public boolean isWilderness(Chunk chunk) {
        return BoardColl.get().getFactionAt(PS.valueOf(chunk)).getId().equals(Factions.ID_NONE);
    }

    @Override
    public boolean isPlayersClaim(Player player, Chunk chunk) {
        MPlayer mPlayer = MPlayer.get(player);
        Faction faction = BoardColl.get().getFactionAt(PS.valueOf(chunk));
        return !faction.getId().equals(Factions.ID_NONE) && faction.getMPlayers().contains(mPlayer);
    }

    @Override
    public boolean hasBypassMode(Player player) {
        return MPlayer.get(player).isOverriding();
    }

}
