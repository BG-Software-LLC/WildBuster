package com.bgsoftware.wildbuster.utils;

import com.bgsoftware.wildbuster.Locale;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.hooks.FactionsProvider;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayerUtils {

    private static WildBusterPlugin plugin = WildBusterPlugin.getPlugin();

    public static boolean canBustChunk(Player player, Chunk chunk){
        FactionsProvider factionsProvider = plugin.getFactionsProvider();
        if(factionsProvider.hasBypassMode(player))
            return true;
        else if(plugin.getSettings().onlyInsideClaim){
            return factionsProvider.isPlayersClaim(player, chunk);
        }
        else{
            return factionsProvider.isPlayersClaim(player, chunk) || factionsProvider.isWilderness(chunk);
        }
    }

    public static void sendActionBar(Player player, Locale locale, Object... objects){
        String msg = locale.getMessage(objects);
        if(msg != null && player != null)
            plugin.getNMSAdapter().sendActionBar(player, msg);
    }

    public static int getBustersLimit(Player player){
        int limit = plugin.getSettings().defaultLimit;

        Pattern pattern = Pattern.compile("wildbuster.limit.(\\d$)");
        Matcher matcher;

        for(PermissionAttachmentInfo permissionAttachmentInfo : player.getEffectivePermissions()){
            if((matcher = pattern.matcher(permissionAttachmentInfo.getPermission())).matches()){
                int permissionLimit = Integer.parseInt(matcher.group(1));
                if(permissionLimit > limit)
                    limit = permissionLimit;
            }
        }

        return Math.max(limit, 0);
    }

    public static boolean isCloseEnough(Location location, Chunk chunk){
        int chunkX = location.getBlockX() >> 4, chunkZ = location.getBlockZ() >> 4;
        return Math.abs(chunkX - chunk.getX()) <= 32 && Math.abs(chunkZ - chunk.getZ()) <= 32;
    }

}
