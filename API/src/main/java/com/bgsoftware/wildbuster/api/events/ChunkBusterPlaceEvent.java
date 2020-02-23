package com.bgsoftware.wildbuster.api.events;

import com.bgsoftware.wildbuster.api.objects.ChunkBuster;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@SuppressWarnings("unused")
/**
 * ChunkBusterPlaceEvent is called when a new chunk buster is placed.
 */
public final class ChunkBusterPlaceEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ChunkBuster chunkBuster;
    private final Location location;

    private boolean cancelled;

    /**
     * The constructor of the event.
     * @param location The location where the chunk buster was placed at.
     * @param player The player who placed the chunk buster.
     * @param chunkBuster The chunk buster that was placed.
     */
    public ChunkBusterPlaceEvent(Location location, Player player, ChunkBuster chunkBuster){
        super(player);
        this.location = location;
        this.chunkBuster = chunkBuster;
        this.cancelled = false;
    }

    /**
     * Get the location where the chunk buster was placed at.
     */
    public Location getLocation() {
        return location.clone();
    }

    /**
     * Get the chunk buster that was placed.
     */
    public ChunkBuster getChunkBuster(){
        return chunkBuster;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
