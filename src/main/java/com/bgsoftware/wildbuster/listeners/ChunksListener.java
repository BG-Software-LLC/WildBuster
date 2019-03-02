package com.bgsoftware.wildbuster.listeners;

import com.bgsoftware.wildbuster.WildBusterPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public final class ChunksListener implements Listener {

    private WildBusterPlugin plugin;

    public ChunksListener(WildBusterPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent e){
        if(plugin.getBustersManager().isChunkBusted(e.getChunk()))
            e.setCancelled(true);
    }

}
