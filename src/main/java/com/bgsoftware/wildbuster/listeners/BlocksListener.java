package com.bgsoftware.wildbuster.listeners;

import com.bgsoftware.wildbuster.Locale;
import com.bgsoftware.wildbuster.WildBusterPlugin;
import com.bgsoftware.wildbuster.api.events.ChunkBusterPlaceEvent;
import com.bgsoftware.wildbuster.api.objects.ChunkBuster;
import com.bgsoftware.wildbuster.api.objects.PlayerBuster;
import com.bgsoftware.wildbuster.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

@SuppressWarnings("unused")
public final class BlocksListener implements Listener {

    private WildBusterPlugin instance;

    public BlocksListener(WildBusterPlugin instance){
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBusterPlace(BlockPlaceEvent e){
        ChunkBuster chunkBuster = instance.getBustersManager().getChunkBuster(e.getItemInHand());

        //Checks if a player-buster was placed
        if(chunkBuster == null)
            return;

        e.setCancelled(true);

        //Checks if the player doesn't have permission to set busters
        if(!e.getPlayer().hasPermission("wildbuster.use")){
            Locale.NO_PERMISSION_PLACE.send(e.getPlayer());
            return;
        }

        List<PlayerBuster> busters = instance.getBustersManager().getPlayerBusters(e.getPlayer());

        //Checks if the player has too many running busters
        int limit = PlayerUtils.getBustersLimit(e.getPlayer());
        if(limit != 0 && busters.size() >= limit){
            Locale.MAX_BUSTERS_AMOUNT.send(e.getPlayer(), limit);
            return;
        }

        Chunk chunk = e.getBlockPlaced().getChunk();

        //Checks if the chunk is currently busted
        if(instance.getBustersManager().isChunkBusted(chunk)){
            Locale.CHUNK_ALREADY_BUSTED.send(e.getPlayer());
            return;
        }

        //Checks if the player can bust the chunk.
        if(!PlayerUtils.canBustChunk(e.getPlayer(), chunk)){
            Locale.MUST_PLACE_IN_CLAIM.send(e.getPlayer());
            return;
        }

        ChunkBusterPlaceEvent event = new ChunkBusterPlaceEvent(e.getBlockPlaced().getLocation(), e.getPlayer(), chunkBuster);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled())
            return;

        //Remove the item from the inventory of the player
        if(e.getPlayer().getGameMode() != GameMode.CREATIVE)
            e.getPlayer().getInventory().removeItem(chunkBuster.getBusterItem());

        //Register the player-buster in the system
        instance.getBustersManager().createPlayerBuster(e.getPlayer(), e.getBlockPlaced().getLocation(), chunkBuster);

        Locale.PLACED_BUSTER.send(e.getPlayer(), instance.getSettings().timeBeforeRunning / 20);
    }

}
