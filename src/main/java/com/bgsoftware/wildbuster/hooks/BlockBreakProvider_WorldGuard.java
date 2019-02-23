package com.bgsoftware.wildbuster.hooks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

@SuppressWarnings("deprecation")
public final class BlockBreakProvider_WorldGuard implements BlockBreakProvider {

    @Override
    public boolean canBuild(OfflinePlayer player, Block block) {
        WorldGuardPlugin worldGuardPlugin = WorldGuardPlugin.inst();
        RegionManager regionManager = worldGuardPlugin.getRegionManager(block.getWorld());
        return regionManager == null || regionManager.getApplicableRegions(block.getLocation()).canBuild(worldGuardPlugin.wrapOfflinePlayer(player));
    }
}
