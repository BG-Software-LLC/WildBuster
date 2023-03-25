package com.bgsoftware.wildbuster.hooks;

import com.bgsoftware.common.reflection.ReflectMethod;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class ClaimsProvider_WorldGuard implements ClaimsProvider {

    private static final WorldGuardPlugin worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    private static final ReflectMethod<Boolean> CAN_BUILD_METHOD = new ReflectMethod<>(WorldGuardPlugin.class,
            "canBuild", Player.class, Block.class);

    public ClaimsProvider_WorldGuard() {
        WildBusterPlugin.log(" - Using WorldGuard as ClaimsProvider.");
    }

    @Override
    public boolean canBuild(OfflinePlayer player, Block block) {
        if (CAN_BUILD_METHOD.isValid()) {
            Player onlinePlayer = player.getPlayer();
            return onlinePlayer != null && CAN_BUILD_METHOD.invoke(worldGuard, onlinePlayer, block);
        }

        WorldGuardPlatform worldGuardPlatform = WorldGuard.getInstance().getPlatform();
        RegionContainer regionContainer = worldGuardPlatform.getRegionContainer();
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(block.getWorld());
        RegionManager regionManager = regionContainer.get(world);

        if (regionManager == null)
            return false;

        LocalPlayer localPlayer = worldGuard.wrapOfflinePlayer(player);
        BlockVector3 blockVector3 = BlockVector3.at(block.getX(), block.getY(), block.getZ());
        ApplicableRegionSet set = regionManager.getApplicableRegions(blockVector3);

        return set.testState(localPlayer, Flags.BUILD) || set.testState(localPlayer, Flags.BLOCK_BREAK);
    }

}
