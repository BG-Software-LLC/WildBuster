package com.bgsoftware.wildbuster.hooks;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.BlockData;
import com.bgsoftware.wildbuster.hooks.listener.IBusterBlockListener;
import com.bgsoftware.wildbuster.utils.threads.Executor;
import net.coreprotect.CoreProtect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

@SuppressWarnings("deprecation")
public final class CoreProtectHook {

    private static WildBusterPlugin plugin;
    private static CoreProtect coreProtect;
    private static boolean warningDisplayed = false;

    public static void register(WildBusterPlugin plugin) {
        CoreProtectHook.plugin = plugin;
        coreProtect = (CoreProtect) Bukkit.getPluginManager().getPlugin("CoreProtect");
        plugin.getProviders().registerBusterBlockListener(CoreProtectHook::recordBlockChange);
    }

    private static void recordBlockChange(OfflinePlayer offlinePlayer, Location location, BlockData blockData,
                                          IBusterBlockListener.Action action) {
        if (!Bukkit.isPrimaryThread()) {
            Executor.sync(() -> recordBlockChange(offlinePlayer, location, blockData, action));
            return;
        }

        if (coreProtect.getAPI().APIVersion() == 5) {
            switch (action) {
                case BLOCK_BREAK:
                    coreProtect.getAPI().logRemoval(offlinePlayer.getName(), location, blockData.getType(), blockData.getData());
                    break;
                case BLOCK_PLACE:
                    coreProtect.getAPI().logPlacement(offlinePlayer.getName(), location, blockData.getType(), blockData.getData());
                    break;
            }
        } else if (coreProtect.getAPI().APIVersion() <= 9) {
            switch (action) {
                case BLOCK_BREAK:
                    coreProtect.getAPI().logRemoval(offlinePlayer.getName(), location, blockData.getType(),
                            (org.bukkit.block.data.BlockData) plugin.getNMSAdapter().getBlockData(blockData.getCombinedId()));
                    break;
                case BLOCK_PLACE:
                    coreProtect.getAPI().logPlacement(offlinePlayer.getName(), location, blockData.getType(),
                            (org.bukkit.block.data.BlockData) plugin.getNMSAdapter().getBlockData(blockData.getCombinedId()));
                    break;
            }
        }
        else if(!warningDisplayed) {
            warningDisplayed = true;
            WildBusterPlugin.log("&cDetected an API version of CoreProtect that is not supported: " + coreProtect.getAPI().APIVersion());
            WildBusterPlugin.log("&cOpen an issue on github regarding this!");
        }
    }

}
