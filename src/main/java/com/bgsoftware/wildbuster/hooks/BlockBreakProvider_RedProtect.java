package com.bgsoftware.wildbuster.hooks;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public final class BlockBreakProvider_RedProtect implements BlockBreakProvider {

    @Override
    public boolean canBuild(OfflinePlayer player, Block block) {
        Region region = RedProtect.get().getAPI().getRegion(block.getLocation());
        String uuid = player.getUniqueId().toString();
        return region == null || region.getFlagBool("build") || region.isLeaderByUUID(uuid) ||
                region.isAdminByUUID(uuid) || region.isMemberByUUID(uuid) || (player.isOnline() &&
                player.getPlayer().hasPermission("redprotect.flag.bypass.build"));
    }

}
