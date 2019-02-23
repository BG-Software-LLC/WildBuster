package com.bgsoftware.wildbuster.hooks;

import net.coreprotect.CoreProtect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;

@SuppressWarnings("deprecation")
public final class CoreProtectHook_CoreProtect implements CoreProtectHook {

    private CoreProtect coreProtect;
    private WildBusterPlugin plugin;

    public CoreProtectHook_CoreProtect(){
        coreProtect = (CoreProtect) Bukkit.getPluginManager().getPlugin("CoreProtect");
        plugin = WildBusterPlugin.getPlugin();
    }

    @Override
    public void recordBlockChange(OfflinePlayer offlinePlayer, Location location, BlockData blockData, boolean place) {
        if(coreProtect.getAPI().APIVersion() == 5) {
            if(!place)
                coreProtect.getAPI().logRemoval(offlinePlayer.getName(), location, blockData.getType(), blockData.getData());
            else
                coreProtect.getAPI().logPlacement(offlinePlayer.getName(), location, blockData.getType(), blockData.getData());
        }
        else if(coreProtect.getAPI().APIVersion() == 6) {
            if(!place)
                coreProtect.getAPI().logRemoval(offlinePlayer.getName(), location, blockData.getType(),
                        (org.bukkit.block.data.BlockData) plugin.getNMSAdapter().getBlockData(blockData.getCombinedId()));
            else
                coreProtect.getAPI().logPlacement(offlinePlayer.getName(), location, blockData.getType(),
                        (org.bukkit.block.data.BlockData) plugin.getNMSAdapter().getBlockData(blockData.getCombinedId()));
        }
    }

}
