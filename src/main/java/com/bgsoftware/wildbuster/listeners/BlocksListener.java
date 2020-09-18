package com.bgsoftware.wildbuster.listeners;

import com.bgsoftware.wildbuster.Locale;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.objects.ChunkBuster;
import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import com.bgsoftware.wildbuster.menu.BustersConfirmMenu;
import com.bgsoftware.wildbuster.utils.PlayerUtils;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;

@SuppressWarnings("unused")
public final class BlocksListener implements Listener {

    private final WildBusterPlugin plugin;

    public BlocksListener(WildBusterPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBusterPlace(BlockPlaceEvent e){
        ChunkBuster chunkBuster = plugin.getBustersManager().getChunkBuster(e.getItemInHand());

        //Checks if a player-buster was placed
        if(chunkBuster == null)
            return;

        e.setCancelled(true);

        //Checks if the player doesn't have permission to set busters
        if(!e.getPlayer().hasPermission("wildbuster.use")){
            Locale.NO_PERMISSION_PLACE.send(e.getPlayer());
            return;
        }

        List<PlayerBuster> busters = plugin.getBustersManager().getPlayerBusters(e.getPlayer());

        //Checks if the player has too many running busters
        int limit = PlayerUtils.getBustersLimit(e.getPlayer());
        if(limit != 0 && busters.size() >= limit){
            Locale.MAX_BUSTERS_AMOUNT.send(e.getPlayer(), limit);
            return;
        }

        Chunk chunk = e.getBlockPlaced().getChunk();

        //Checks if the chunk is currently busted
        if(plugin.getBustersManager().isChunkBusted(chunk)){
            Locale.CHUNK_ALREADY_BUSTED.send(e.getPlayer());
            return;
        }

        //Checks if the player can bust the chunk.
        if(!PlayerUtils.canBustChunk(e.getPlayer(), chunk)){
            Locale.MUST_PLACE_IN_CLAIM.send(e.getPlayer());
            return;
        }

        if(plugin.getSettings().confirmPlacement){
            BustersConfirmMenu.open(e.getPlayer(), e.getBlockPlaced().getLocation(), chunkBuster);
        }
        else{
            plugin.getBustersManager().handleBusterPlacement(e.getPlayer(), e.getBlockPlaced().getLocation(), chunkBuster);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent e){
        try {
            PlayerBuster playerBuster = plugin.getBustersManager().getPlayerBuster(e.getChunk());
            if (playerBuster != null)
                e.setCancelled(true);
        }catch (Exception ignored){}
    }

}
