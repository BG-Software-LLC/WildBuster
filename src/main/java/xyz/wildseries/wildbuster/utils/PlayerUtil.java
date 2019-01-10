package xyz.wildseries.wildbuster.utils;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import xyz.wildseries.wildbuster.Locale;
import xyz.wildseries.wildbuster.WildBusterPlugin;
import xyz.wildseries.wildbuster.hooks.FactionsProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayerUtil {

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
                int permissionLimit = Integer.valueOf(matcher.group(1));
                if(permissionLimit > limit)
                    limit = permissionLimit;
            }
        }

        return limit < 0 ? 0 : limit;
    }

}
