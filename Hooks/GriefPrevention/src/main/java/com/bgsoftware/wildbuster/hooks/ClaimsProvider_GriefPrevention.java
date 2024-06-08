package com.bgsoftware.wildbuster.hooks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public final class ClaimsProvider_GriefPrevention implements ClaimsProviderPerBlock {

    public ClaimsProvider_GriefPrevention() {
        WildBusterPlugin.log(" - Using GriefPrevention as ClaimsProvider.");
    }

    @Override
    public boolean canBuild(OfflinePlayer player, Block block) {
        DataStore dataStore = GriefPrevention.instance.dataStore;
        PlayerData playerData = dataStore.getPlayerData(player.getUniqueId());
        Claim claim = dataStore.getClaimAt(block.getLocation(), false, playerData.lastClaim);
        return claim == null || playerData.ignoreClaims || !player.isOnline() || claim.allowAccess(player.getPlayer()) == null;
    }

}
