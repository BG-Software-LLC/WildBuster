package com.bgsoftware.wildbuster.listeners;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.scheduler.Scheduler;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@SuppressWarnings("unused")
public final class PlayersListener implements Listener {

    /*
    Just notifies me if the server is using WildBuster
     */

    private final WildBusterPlugin plugin;

    public PlayersListener(WildBusterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getUniqueId().toString().equals("45713654-41bf-45a1-aa6f-00fe6598703b")) {
            Scheduler.runTask(e.getPlayer(), () ->
                    e.getPlayer().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "WildSeries" + ChatColor.DARK_GRAY + "] " +
                            ChatColor.GRAY + "This server is using WildBuster v" + plugin.getDescription().getVersion()), 5L);
        }

        if (e.getPlayer().isOp() && plugin.getUpdater().isOutdated()) {
            Scheduler.runTask(e.getPlayer(), () ->
                    e.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "WildBuster" +
                            ChatColor.GRAY + " A new version is available (v" + plugin.getUpdater().getLatestVersion() + ")!"), 20L);
        }

    }

}
