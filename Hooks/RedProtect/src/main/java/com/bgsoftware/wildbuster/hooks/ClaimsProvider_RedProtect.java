package com.bgsoftware.wildbuster.hooks;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public final class ClaimsProvider_RedProtect implements ClaimsProviderPerBlock {

    public ClaimsProvider_RedProtect() {
        WildBusterPlugin.log(" - Using RedProtect as ClaimsProvider.");
    }

    @Override
    public boolean canBuild(OfflinePlayer player, Location blockLocation) {
        Region region = RedProtect.get().getAPI().getRegion(blockLocation);
        String uuid = player.getUniqueId().toString();
        return region == null || region.getFlagBool("build") || region.isLeaderByUUID(uuid) ||
                region.isAdminByUUID(uuid) || region.isMemberByUUID(uuid) || (player.isOnline() &&
                player.getPlayer().hasPermission("redprotect.flag.bypass.build"));
    }

}
