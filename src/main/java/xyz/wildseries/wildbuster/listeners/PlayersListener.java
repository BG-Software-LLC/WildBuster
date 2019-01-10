package xyz.wildseries.wildbuster.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.wildseries.wildbuster.Updater;
import xyz.wildseries.wildbuster.WildBusterPlugin;

@SuppressWarnings("unused")
public final class PlayersListener implements Listener {

    /*
    Just notifies me if the server is using WildBuster
     */

    private WildBusterPlugin instance;

    public PlayersListener(WildBusterPlugin instance){
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(e.getPlayer().getUniqueId().toString().equals("45713654-41bf-45a1-aa6f-00fe6598703b")){
            Bukkit.getScheduler().runTaskLater(instance, () ->
                e.getPlayer().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "WildSeries" + ChatColor.DARK_GRAY + "] " +
                        ChatColor.GRAY + "This server is using WildBuster v" + instance.getDescription().getVersion()), 5L);
        }

        if(e.getPlayer().isOp() && Updater.isOutdated()){
            Bukkit.getScheduler().runTaskLater(instance, () ->
                e.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "WildBuster" +
                        ChatColor.GRAY + " A new version is available (v" + Updater.getLatestVersion() + ")!"), 20L);
        }

    }

}
